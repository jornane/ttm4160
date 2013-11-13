package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

/**
 * Created with IntelliJ IDEA.
 * User: eivind
 * Date: 11/13/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Event {
    public Message message;
    public IStateMachine addressStateMachine;

    public Event(Message message, IStateMachine addressStateMachine){
        this.message = message;
        this.addressStateMachine = addressStateMachine;
    }

}
