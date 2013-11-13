package no.ntnu.item.ttm4160.sunspot.runtime;

public abstract class Event {
	
    /**
     * Whether this event should be executed upon pop'ing from the queue
     */
    private boolean alive;

    /**
     * Construct a new event
     */
    public Event(){
        alive = true;
    }

    /**
     * Returns whether this event is still relevant.
     * Non-alive events should be ignored.
     * @return	whether the event is still alive.
     */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Cancel the event, meaning that it should not be executed.
	 * If the event already started an execution, this will not abort it.
	 */
	public void cancel() {
		alive = false;
	}
	

}
