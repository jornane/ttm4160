package no.ntnu.item.ttm4160.sunspot.runtime;

final class DeferredEvent extends Event {

	final StateMachine machine;
	final Event event;

	static DeferredEvent defer(StateMachine machine, Event theEvent) {
		if (theEvent instanceof DeferredEvent) {
			DeferredEvent deferredEvent = (DeferredEvent) theEvent;
			if (deferredEvent.machine != machine)
				throw new IllegalArgumentException("Stacking an impossible combination of Deferred Events");
			return deferredEvent;
		}
		return new DeferredEvent(machine, theEvent);
	}
	
	/**
	 * 
	 */
	private DeferredEvent(StateMachine machine, Event theEvent) {
		this.machine = machine;
		event = theEvent;
	}

}
