package no.ntnu.item.ttm4160.sunspot.runtime;

public abstract class Event {
	
	/**
	 * The state machine which should handle this event
	 */
    private IStateMachine addressStateMachine;

    /**
     * Construct a new event
     * @param addressStateMachine	The state machine which should handle this event
     */
    public Event(IStateMachine addressStateMachine){
        this.addressStateMachine = addressStateMachine;
    }

    /**
     * Returns the state machine which should handle this event
     * @return	the statemachine
     */
    public IStateMachine getMachine(){
        return addressStateMachine;
    }

    /**
     * Returns whether this event is still relevant.
     * Non-alive events should be ignored.
     * @return	whether the event is still alive.
     */
    public abstract boolean isAlive();

}
