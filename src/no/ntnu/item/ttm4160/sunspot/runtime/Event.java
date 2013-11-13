package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;


public class Event {
    public Message message;
    private IStateMachine addressStateMachine;
    private boolean alive;

    public Event(Message message, IStateMachine addressStateMachine){
        this.message = message;
        this.addressStateMachine = addressStateMachine;
        alive = true;
    }
    public IStateMachine getMachine(){
        return addressStateMachine;
    }
    public boolean isAlive(){
        return alive;
    }

}
