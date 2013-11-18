package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Action;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IScheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.util.LocalMessageEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.LocalMessageEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.util.SwitchEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.SwitchEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.util.TimerEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.TimerEventType;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;

public class Receiver extends CommunicatingStateMachine {

	private State state;
	private TimerEvent timer;
	private String otherSpot;
	private static int maxObservedLight = 1;
	
	public static interface State {
		public State FREE = new State(){public String toString(){return "FREE";}};
		public State WAIT_APPROVED = new State(){public String toString(){return "WAIT_APPROVED";}};
		public State BUSY = new State(){public String toString(){return "BUSY";}};
	}

	/**
	 * Construct a new receiver.
	 * The receiver will wait until it receives a {@value Message#CanYouDisplayMyReadings} from another state machine.
	 * When it does, it will reply with {@value Message#ICanDisplayReadings}.
	 * If it then receives {@value Message#Approved}, it will listen for readings and display them using the LEDs of the hardware it runs on.
	 * 
     * @param communications	the handle for sending and receiving messages to another state machine
	 */
	public Receiver(ICommunicationLayer communications) {
		super(communications);
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
	 */
	public Action fire(Event event, IScheduler scheduler) {
		State state = this.state; // concurrency
		if (state == null)
			return fireOnInit(event, scheduler);
		if (state == State.FREE)
			return fireOnFree(event, scheduler);
		if (state == State.WAIT_APPROVED)
			return fireOnWaitApproved(event, scheduler);
		if (state == State.BUSY)
			return fireOnBusy(event, scheduler);
		throw new IllegalStateException(state.toString());
	}
	
	private Action fireOnInit(Event event, IScheduler scheduler) {
			scheduler.subscribe(this, new SwitchEventType(2));
			scheduler.subscribe(this, LocalMessageEventType.BROADCAST);
			scheduler.subscribe(this, new LocalMessageEventType(this));
			scheduler.subscribe(this, new TimerEventType(this));
			state = State.FREE;
			return Action.EXECUTE_TRANSITION;
	}
	
	private Action fireOnFree(Event event, IScheduler scheduler) {
			if (event instanceof LocalMessageEvent) {
				LocalMessageEvent messageEvent = (LocalMessageEvent) event;
				if (Message.BROADCAST_ADDRESS.equals(messageEvent.message.getReceiver())) {
					otherSpot = messageEvent.message.getSender();
					sendRemoteMessage(
							otherSpot, 
							Message.ICanDisplayReadings
						);
					
					state = State.WAIT_APPROVED;
					return Action.EXECUTE_TRANSITION;
				}
			}
			return Action.DISCARD_EVENT;
	}
	
	private Action fireOnWaitApproved(Event event, IScheduler scheduler) {
		if (event instanceof LocalMessageEvent) {
			LocalMessageEvent messageEvent = (LocalMessageEvent) event;
			if (Message.Approved.equals(messageEvent.message.getContent())) {
				timer = TimerEvent.schedule(this, scheduler, 5000);
				prepareLEDsForBusy();
				state = State.BUSY;
				return Action.EXECUTE_TRANSITION;
			}
			else if (Message.Denied.equals(messageEvent.message.getContent())) {
				state = State.FREE;
				return Action.EXECUTE_TRANSITION;
			}
			else if (Message.CanYouDisplayMyReadings.equals(messageEvent.message.getContent())) {
				scheduler.defer(this, event);
			}
		}
		return Action.DISCARD_EVENT;
	}
	
	private Action fireOnBusy(Event event, IScheduler scheduler) {
			if (event instanceof LocalMessageEvent) {
				LocalMessageEvent messageEvent = (LocalMessageEvent) event;
				if (messageEvent.message.getContent().startsWith(Message.Reading)) {
					int result = Integer.parseInt(messageEvent.message.getContent().substring(Message.Reading.length()));
					setLEDs(result);
					timer.reset();
					return Action.EXECUTE_TRANSITION;
				}
				if (Message.SenderDisconnect.equals(messageEvent.message.getContent())) {
					timer.cancel();
					timer = null;
					blink(LEDColor.WHITE);
					state = State.FREE;
					return Action.EXECUTE_TRANSITION;
				}
			}
			if (event instanceof SwitchEvent && ((SwitchEvent)event).button == 2) {
				timer.cancel();
				sendRemoteMessage(
						otherSpot,
						Message.ReceiverDisconnect
					);
				otherSpot = null;
				blink(LEDColor.WHITE);
				state = State.FREE;
				return Action.EXECUTE_TRANSITION;
			}
			if (event == timer) {
				state = State.FREE;
				return Action.EXECUTE_TRANSITION;
			}
			return Action.DISCARD_EVENT;
	}

	private void setLEDs(int lightLevel) {
		if (lightLevel > maxObservedLight)
			maxObservedLight = lightLevel;
		ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
		double percent = lightLevel / (double) maxObservedLight;
		int amount = (int)(leds.length * percent);
		for(int i=0;i<leds.length;i++) {
			if (i == amount) {
				int rest = (int) ((percent * leds.length * 255) % 255);
				leds[i].setRGB(rest, rest, i == 0 ? 255 : rest);
			} else {
				leds[i].setOn(i <= amount);
				leds[i].setColor(LEDColor.WHITE);
			}
		}
	}

	private void prepareLEDsForBusy() {
		ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
		for(int i=0;i<leds.length;i++) {
			leds[i].setOff();
		}
	}

}
