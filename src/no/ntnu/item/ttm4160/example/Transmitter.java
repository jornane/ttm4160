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
public class Transmitter implements IStateMachine {

    private final String id;
    private final Scheduler scheduler;
    private State state;
    private String lightReadingsReceiver;
    private TimerEvent currentTimer;


    private interface State {
        public State READY = new State() {public String toString() {return"READY";}};
        public State WAIT_RESPONSE = new State() {public String toString() {return"WAIT_RESPONSE";}};
        public State SENDING = new State() {public String toString() {return"SENDING";}};
    }

    public Transmitter(String id, Scheduler scheduler){
        this.id = id;
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
	public EAction fire(Event event, Scheduler scheduler) {
		switch (state.toString()){
            case "READY":
                if (event instanceof SwitchEvent && event.isSwitchNumber(1)){
                    startTimer(500);
                    sendMessage(Message.BROADCAST_ADDRESS, Message.CanYouDisplayMyReadings);
                    state = State.WAIT_RESPONSE;
                }
                return null;
            case "WAIT_RESPONSE" :
                if(((MessageEvent) event).message.getContent().equals(Message.ICanDisplayReadings)){
                    sendMessage(((MessageEvent) event).message.getSender(), Message.Approved);
                    startTimer(100);
                    lightReadingsReceiver = ((MessageEvent) event).message.getReceiver();
                    state = State.SENDING;


                }else if(event instanceof TimerEvent){
                    blinkLeds();
                    state = State.READY;

                }
                return null;
            case "SENDING":
                if(event instanceof TimerEvent ){
                    startTimer(100);
                    sendLightReadings();
                }
                else if(event instanceof MessageEvent && ((MessageEvent) event).message.equals(Message.ReceiverDisconnect)){
                    currentTimer.cancel();
                    state = State.READY;
                }
                else if(event instanceof SwitchEvent /*TODO check for right button id*/){
                    sendMessage(lightReadingsReceiver, Message.SenderDisconnect);
                    currentTimer.cancel();
                    state = State.READY;
                }

            default:
                if(event instanceof MessageEvent&& ((MessageEvent)event).message.getContent().equals(Message.CanYouDisplayMyReadings)){
                    sendMessage(((MessageEvent) event).message.getReceiver(), Message.Denied);
                }
                return null;


        }
		return EAction.EXECUTE_TRANSITION ;
	}

    private void sendLightReadings() {
        //TODO get LightReadings
        String lightReadings = "-1";
        sendMessage(lightReadingsReceiver, lightReadings);
    }

    private void blinkLeds() {
        //TODO make led blinking method
    }

    private void sendMessage(String receiver, String content) {
        Message message = new Message(id, receiver, content);
        Event event = new MessageEvent(message);
        scheduler.schedule(event);
    }

    private void startTimer(long delay) {
        Event timer = new TimerEvent(this, scheduler, delay);
        scheduler.schedule(timer);
        this.currentTimer = (TimerEvent)timer;
    }

    /* (non-Javadoc)
     * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#answersTo(java.lang.String)
     */
	public boolean answersTo(String id) {
        return this.id.equals(id) || Message.BROADCAST_ADDRESS.equals(id);
	}

}
