/**
 * 
 */
package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.*;

/**
 * @author yorn
 *
 */
public class Transmitter extends StateMachine {

    private Scheduler scheduler;
    private State state;
    private String lightReadingsReceiver;
    private TimerEvent currentTimer;


    private interface State {
        public State READY = new State() {public String toString() {return"READY";}};
        public State WAIT_RESPONSE = new State() {public String toString() {return"WAIT_RESPONSE";}};
        public State SENDING = new State() {public String toString() {return"SENDING";}};
    }

    public Transmitter(){
        subscribeToButtons();
        state= State.READY;
    }
    public Transmitter( Scheduler scheduler){
        this.scheduler = scheduler;
        subscribeToButtons();
        state = State.READY;
    }

    private void subscribeToButtons() {
        //To change body of created methods use File | Settings | File Templates.
    }


    /* (non-Javadoc)
     * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
     */
	public Action fire(Event event, Scheduler scheduler) {

        if(state.toString().equals("READY")){
            fireOnStateReady(event, scheduler);
        }
        if(state.toString().equals("WAIT_RESPONSE")){
            fireOnStateWaitResponse(event, scheduler);
        }
        if(state.toString().equals("SENDING")){
            fireOnStateSending(event, scheduler);
        }


        if(event instanceof MessageEvent&& ((MessageEvent)event).message.getContent().equals(Message.CanYouDisplayMyReadings)){
            super.sendMessage(scheduler, ((MessageEvent) event).message.getReceiver(), Message.Denied);
            return Action.EXECUTE_TRANSITION;

        }
		return Action.DISCARD_EVENT;
	}
    private Action fireOnStateReady(Event event, Scheduler scheduler){
        if (event instanceof SwitchEvent /*Needs check for right button*/){
            startTimer(500);
            super.sendMessage(scheduler, Message.BROADCAST_ADDRESS, Message.CanYouDisplayMyReadings);
            state = State.WAIT_RESPONSE;
            return Action.EXECUTE_TRANSITION;
        }
        return Action.DISCARD_EVENT;

    }
    private Action fireOnStateWaitResponse(Event event, Scheduler scheduler){
        if(event instanceof MessageEvent){
            if(((MessageEvent) event).message.getContent().equals(Message.ICanDisplayReadings)){
                super.sendMessage(scheduler, ((MessageEvent) event).message.getSender(), Message.Approved);
                startTimer(100);
                lightReadingsReceiver = ((MessageEvent) event).message.getReceiver();
                state = State.SENDING;
                return Action.EXECUTE_TRANSITION;
            }
        }
        else if(event instanceof TimerEvent){
            blinkLeds();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;
        }
        return Action.DISCARD_EVENT;
    }
    private Action fireOnStateSending(Event event, Scheduler scheduler){
        if(event instanceof TimerEvent ){
            startTimer(100);
            sendLightReadings();
            return Action.EXECUTE_TRANSITION;
        }
        else if(event instanceof MessageEvent && ((MessageEvent) event).message.getContent().equals(Message.ReceiverDisconnect)){
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        else if(event instanceof SwitchEvent /*TODO check for right button id*/){
            super.sendMessage(scheduler, lightReadingsReceiver, Message.SenderDisconnect);
            currentTimer.cancel();
            state = State.READY;
            return Action.EXECUTE_TRANSITION;

        }
        return Action.DISCARD_EVENT;
    }

    private void sendLightReadings() {
        //TODO get LightReadings
        String lightReadings = "-1";
        super.sendMessage(scheduler, lightReadingsReceiver, lightReadings);
    }

    private void blinkLeds() {
        //TODO make led blinking method
    }

    private void startTimer(long delay) {
        Event timer = new TimerEvent(this, scheduler, delay);
        //start Timer
        this.currentTimer = (TimerEvent)timer;
    }
}
