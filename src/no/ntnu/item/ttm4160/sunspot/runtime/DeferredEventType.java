package no.ntnu.item.ttm4160.sunspot.runtime;

final class DeferredEventType implements IEventType {

	private final StateMachine machine;

	/**
	 * 
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
