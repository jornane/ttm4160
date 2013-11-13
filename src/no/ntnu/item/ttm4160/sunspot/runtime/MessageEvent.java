package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

/**
 * Event for messages to state machines
 */
public class MessageEvent extends Event {
    public Message message;

    /**
     *
     * @param stateMachine is the state machine to which the message should be sent
     * @param message is the message that's being sent
     */
    public MessageEvent(IStateMachine stateMachine, Message message){
        super(stateMachine);
        this.message = message;
    }

}
