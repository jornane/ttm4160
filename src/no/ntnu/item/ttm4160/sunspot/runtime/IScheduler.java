package no.ntnu.item.ttm4160.sunspot.runtime;

public interface IScheduler {

	/**
	 * Subscribe an event type for a state machine
	 * @param machine	the state machine which is interested in a specified event type
	 * @param type	the event type the state machine is interested in
	 */
	void subscribe(StateMachine machine, IEventType type);

	/**
	 * Get the maximum priority of the queue
	 * @return	the maximum priority
	 */
	int getMaxPriority();

	/**
	 * Push an event, this will usually make sure {@link #fire(Event, StateMachine)} is called shortly after.
	 * @param event	the event to push
	 * @param priority	the priority of the event
	 */
	void eventHappened(Event event, int priority);

	/**
	 * Schedule an event for fireing when the state machine changes state.
	 * @param machine	the machine which deferred the event
	 * @param event	the event
	 */
	void defer(StateMachine machine, Event event);

}
