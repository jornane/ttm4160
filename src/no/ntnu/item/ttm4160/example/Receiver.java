package no.ntnu.item.ttm4160.example;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Action;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.StateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.util.MessageEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.MessageEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.util.SwitchEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.SwitchEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.util.TimerEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.util.TimerEventType;

public class Receiver extends StateMachine {

	private State state;
	private TimerEvent timer;
	private String otherSpot;
	private static int maxObservedLight = 1;
	
	public static interface State {
		public State FREE = new State(){public String toString(){return "FREE";}};
		public State WAIT_APPROVED = new State(){public String toString(){return "WAIT_APPROVED";}};
		public State BUSY = new State(){public String toString(){return "BUSY";}};
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
	 */
	public Action fire(Event event, Scheduler scheduler) {
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
	
	public Action fireOnInit(Event event, Scheduler scheduler) {
			scheduler.subscribe(this, new SwitchEventType(2));
			scheduler.subscribe(this, MessageEventType.BROADCAST);
			scheduler.subscribe(this, new MessageEventType(this.getName()));
			scheduler.subscribe(this, new TimerEventType(this));
			state = State.FREE;
			return Action.EXECUTE_TRANSITION;
	}
	
	public Action fireOnFree(Event event, Scheduler scheduler) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (Message.BROADCAST_ADDRESS.equals(messageEvent.message.getReceiver())) {
					otherSpot = messageEvent.message.getSender();
					sendMessage(
							scheduler, 
							otherSpot, 
							Message.ICanDisplayReadings
						);
					
					state = State.WAIT_APPROVED;
					return Action.EXECUTE_TRANSITION;
				}
			}
			return Action.DISCARD_EVENT;
	}
	
	public Action fireOnWaitApproved(Event event, Scheduler scheduler) {
		if (event instanceof MessageEvent) {
			MessageEvent messageEvent = (MessageEvent) event;
				timer = TimerEvent.schedule(this, scheduler, 5000);
			if (Message.Approved.equals(messageEvent.message.getContent())) {
				prepareLEDsForBusy();
				state = State.BUSY;
				return Action.EXECUTE_TRANSITION;
			}
			else if (Message.Denied.equals(messageEvent.message.getContent())) {
				state = State.FREE;
				return Action.EXECUTE_TRANSITION;
			}
		}
		return Action.DISCARD_EVENT;
	}
	
	public Action fireOnBusy(Event event, Scheduler scheduler) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (messageEvent.message.getContent().startsWith(Message.Reading)) {
					int result = Integer.parseInt(messageEvent.message.getContent().substring(Message.Reading.length()));
					setLEDs(result);
					timer.reset();
					return Action.EXECUTE_TRANSITION;
				}
				if (Message.SenderDisconnect.equals(messageEvent.message.getContent())) {
					timer.cancel();
					timer = null;
					blink(LEDColor.PUCE);
					state = State.FREE;
					return Action.EXECUTE_TRANSITION;
				}
			}
			if (event instanceof SwitchEvent && ((SwitchEvent)event).button == 2) {
				timer.cancel();
				sendMessage(scheduler,
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
				leds[i].setRGB(rest, rest, rest);
			} else
				leds[i].setColor(LEDColor.WHITE);
			leds[i].setOn(i <= amount);
		}
	}

	private void prepareLEDsForBusy() {
		ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
		for(int i=0;i<leds.length;i++) {
			leds[i].setOff();
		}
	}

}
