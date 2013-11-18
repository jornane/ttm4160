package no.ntnu.item.ttm4160.example;

import java.io.IOException;

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
import com.sun.spot.sensorboard.peripheral.LEDColor;

public class Transmitter extends CommunicatingStateMachine {

    private State state;
    private String lightReadingsReceiver;
    private TimerEvent currentTimer;

    private interface State {
        public State READY = new State() {public String toString() {return"READY";}};
        public State WAIT_RESPONSE = new State() {public String toString() {return"WAIT_RESPONSE";}};
        public State SENDING = new State() {public String toString() {return"SENDING";}};
    }

    /**
     * Construct a new transmitter.
     * The transmitter will wait until button 1 is pressed.
     * It will then broadcast a message {@value Message#CanYouDisplayMyReadings},
     * and wait until some state machine answers with {@value Message#ICanDisplayReadings}.
     * It will then send readings with an interval, until it receives {@value Message#SenderDisconnect}
     * or button 2 is pressed. 
     * 
     * @param communications	the handle for sending and receiving messages to another state machine
     */
	public Transmitter(ICommunicationLayer communications) {
		super(communications);
	}

    /* (non-Javadoc)
     * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
     */
	public Action fire(Event event, IScheduler scheduler) {
		if(state == State.WAIT_RESPONSE){
            return fireOnStateWaitResponse(event, scheduler);
        }
		if(event instanceof LocalMessageEvent && ((LocalMessageEvent)event).message.getContent().equals(Message.ICanDisplayReadings)){
			return fireCatchICanDisplayReadings((LocalMessageEvent) event, scheduler);
		}
		if(state == null) {
			return fireOnInit(event, scheduler);
		}
        if(state == State.READY){
            return fireOnStateReady(event, scheduler);
        }
        
        if(state == State.SENDING){
            return fireOnStateSending(event, scheduler);
        }
		throw new IllegalStateException(state.toString());
	}
    private Action fireCatchICanDisplayReadings(LocalMessageEvent event, IScheduler scheduler) {
		sendRemoteMessage(event.message.getSender(), Message.Denied);
		return Action.EXECUTE_TRANSITION;
	}
	private Action fireOnInit(Event event, IScheduler scheduler) {
		scheduler.subscribe(this, new SwitchEventType(1));
		scheduler.subscribe(this, new SwitchEventType(2));
		scheduler.subscribe(this, new LocalMessageEventType(this));
		scheduler.subscribe(this, new TimerEventType(this));
		state = State.READY;
		return Action.EXECUTE_TRANSITION;
	}
	private Action fireOnStateReady(Event event, IScheduler scheduler){
        if (event instanceof SwitchEvent && ((SwitchEvent)event).button == 1){
            currentTimer = TimerEvent.schedule(this, scheduler, 500);
            sendRemoteMessage(Message.BROADCAST_ADDRESS, Message.CanYouDisplayMyReadings);
            state = State.WAIT_RESPONSE;
            return Action.EXECUTE_TRANSITION;
        }
        return Action.DISCARD_EVENT;

    }
    private Action fireOnStateWaitResponse(Event event, IScheduler scheduler){
        if(event instanceof LocalMessageEvent){
            if(((LocalMessageEvent) event).message.getContent().equals(Message.ICanDisplayReadings)){
                sendRemoteMessage(((LocalMessageEvent) event).message.getSender(), Message.Approved);
                if (currentTimer != null)
                	currentTimer.cancel();
                currentTimer = TimerEvent.schedule(this, scheduler, 100);
                lightReadingsReceiver = ((LocalMessageEvent) event).message.getSender();
                state = State.SENDING;
                return Action.EXECUTE_TRANSITION;
            }
        }
        else if(event instanceof TimerEvent){
            blink(LEDColor.RED);
            state = State.READY;
            return Action.EXECUTE_TRANSITION;
        }
        return Action.DISCARD_EVENT;
    }
    private Action fireOnStateSending(Event event, IScheduler scheduler){
        if(event instanceof TimerEvent){
            if (currentTimer != null)
            	currentTimer.cancel();
           	sendLightReadings(scheduler);
            currentTimer = TimerEvent.schedule(this, scheduler, 100);
            return Action.EXECUTE_TRANSITION;
        }
        else if(event instanceof LocalMessageEvent && ((LocalMessageEvent) event).message.getContent().equals(Message.ReceiverDisconnect)){
        	blink(LEDColor.WHITE);
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        else if(event instanceof SwitchEvent && ((SwitchEvent)event).button == 2){
            sendRemoteMessage(lightReadingsReceiver, Message.SenderDisconnect);
            blink(LEDColor.WHITE);
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        return Action.DISCARD_EVENT;
    }

    private void sendLightReadings(IScheduler scheduler) {
        int lightReadings = -1;
        try {
            lightReadings = EDemoBoard.getInstance().getLightSensor().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendRemoteMessage(lightReadingsReceiver, Message.Reading + lightReadings);
    }

}
