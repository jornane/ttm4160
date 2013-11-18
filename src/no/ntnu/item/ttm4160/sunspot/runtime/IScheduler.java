package no.ntnu.item.ttm4160.sunspot.runtime;

public interface IScheduler {

	/**
	 * Add a state machine to the scheduler.
	 * This means the state machine will be considered to notify
	 * using {@link StateMachine#fire(Event, IScheduler)}
	 * when an event is received by the scheduler.
	 * @param machine	the machine to add
	 */
	public void addMachine(StateMachine machine);
	
	/**
	 * Execute the scheduler.
	 * Before this gets called,
	 * at least one state machine must have been added.
	 * The scheduler will terminate when there are no more state machines.
	 */
	public void run();

	/**
	 * Subscribe (indicate interest for) an event type for a state machine
	 * @param machine	the state machine which is interested in a specified event type
	 * @param type	the event type the state machine is interested in
	 */
	void subscribe(StateMachine machine, IEventType type);

	/**
	 * Unsubscribe (remove interest for) an event type for a state machine
	 * @param machine	the state machine which is no longer interested in a specified event type
	 * @param type	the event type the state machine is no longer interested in
	 */
	void unsubscribe(StateMachine machine, IEventType type);

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
