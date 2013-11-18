package no.ntnu.item.ttm4160.sunspot.runtime;

/**
 * This class is used by State Machines to indicate interest in a specific Event type.
 * The IEventType instance is constructed by a state machine and given to the Scheduler.
 * If the scheduler receives an event, it must check the event using the {@link #matches(Event)} method.
 * If it returns true, the state machine may be interested in the event
 * and {@link StateMachine#fire(Event, IScheduler)} should be called.
 */
public interface IEventType {

	/**
	 * Determine if this EventType matches a concrete Event.
	 * This is used to determine whether the state machine associated with this instance
	 * is interested in the provided Event.
	 *  
	 * @param event	the event to check whether it matches this event type
	 * @return The state machine associated with <code>this</code> is interested in <code>event</code>.
	 */
	boolean matches(Event event);

}
