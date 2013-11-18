package no.ntnu.item.ttm4160.sunspot.runtime;

/**
 * An EventType which every state machine gets subscribed to upon registration with the scheduler.
 * This EventType is only for internal use in the Scheduler, which is why it is package private.
 */
final class DeferredEventType implements IEventType {

	private final StateMachine machine;

	/**
	 * Construct a new DeferredEventType for a state machine.
	 */
	DeferredEventType(StateMachine machine) {
		this.machine = machine;
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IEventType#matches(no.ntnu.item.ttm4160.sunspot.runtime.Event)
	 */
	public boolean matches(Event event) {
		return event instanceof DeferredEvent
				&& ((DeferredEvent) event).machine == machine;
	}

}
