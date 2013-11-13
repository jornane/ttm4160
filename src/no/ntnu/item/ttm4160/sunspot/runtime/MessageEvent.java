package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

/**
 * Event for messages to state machines
 */
public class MessageEvent extends Event {
    public Message message;

    /**
     *
     * @param message is the message that's being sent
     */
    public MessageEvent(Message message){
        this.message = message;
    }

}
