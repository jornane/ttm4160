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

import java.io.IOException;

public class Transmitter extends StateMachine {

    private State state;
    private String lightReadingsReceiver;
    private TimerEvent currentTimer;

    private interface State {
        public State READY = new State() {public String toString() {return"READY";}};
        public State WAIT_RESPONSE = new State() {public String toString() {return"WAIT_RESPONSE";}};
        public State SENDING = new State() {public String toString() {return"SENDING";}};
    }


    /* (non-Javadoc)
     * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
     */
	public Action fire(Event event, Scheduler scheduler) {
		if(state == State.WAIT_RESPONSE){
            return fireOnStateWaitResponse(event, scheduler);
        }
		if(event instanceof MessageEvent && ((MessageEvent)event).message.getContent().equals(Message.ICanDisplayReadings)){
			return fireCatchICanDisplayReadings((MessageEvent) event, scheduler);
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
    private Action fireCatchICanDisplayReadings(MessageEvent event, Scheduler scheduler) {
		sendMessage(scheduler, event.message.getSender(), Message.Denied);
		return Action.EXECUTE_TRANSITION;
	}
	private Action fireOnInit(Event event, Scheduler scheduler) {
		scheduler.subscribe(this, new SwitchEventType(1));
		scheduler.subscribe(this, new SwitchEventType(2));
		scheduler.subscribe(this, new MessageEventType(this.getName()));
		scheduler.subscribe(this, new TimerEventType(this));
		state = State.READY;
		return Action.EXECUTE_TRANSITION;
	}
	private Action fireOnStateReady(Event event, Scheduler scheduler){
        if (event instanceof SwitchEvent && ((SwitchEvent)event).button == 1){
            currentTimer = TimerEvent.schedule(this, scheduler, 500);
            sendMessage(scheduler, Message.BROADCAST_ADDRESS, Message.CanYouDisplayMyReadings);
            state = State.WAIT_RESPONSE;
            return Action.EXECUTE_TRANSITION;
        }
        return Action.DISCARD_EVENT;

    }
    private Action fireOnStateWaitResponse(Event event, Scheduler scheduler){
        if(event instanceof MessageEvent){
            if(((MessageEvent) event).message.getContent().equals(Message.ICanDisplayReadings)){
                sendMessage(scheduler, ((MessageEvent) event).message.getSender(), Message.Approved);
                if (currentTimer != null)
                	currentTimer.cancel();
                currentTimer = TimerEvent.schedule(this, scheduler, 100);
                lightReadingsReceiver = ((MessageEvent) event).message.getSender();
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
    private Action fireOnStateSending(Event event, Scheduler scheduler){
        if(event instanceof TimerEvent){
            if (currentTimer != null)
            	currentTimer.cancel();
           	sendLightReadings(scheduler);
            currentTimer = TimerEvent.schedule(this, scheduler, 100);
            return Action.EXECUTE_TRANSITION;
        }
        else if(event instanceof MessageEvent && ((MessageEvent) event).message.getContent().equals(Message.ReceiverDisconnect)){
        	blink(LEDColor.WHITE);
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        else if(event instanceof SwitchEvent && ((SwitchEvent)event).button == 2){
            blink(LEDColor.WHITE);
            sendMessage(scheduler, lightReadingsReceiver, Message.SenderDisconnect);
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        return Action.DISCARD_EVENT;
    }

    private void sendLightReadings(Scheduler scheduler) {
        int lightReadings = -1;
        try {
            lightReadings = EDemoBoard.getInstance().getLightSensor().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessage(scheduler, lightReadingsReceiver, Message.Reading + lightReadings);
    }

}
