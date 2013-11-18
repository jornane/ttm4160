package no.ntnu.item.ttm4160.sunspot.runtime;

/**
 * An deferred event. This event is only for internal use in the runtime package.
 * It contains another event which a state machine 
 * has deferred before using {@link IScheduler#defer(StateMachine, Event)}.
 * The scheduler will subscribe every state machine to deferred events directed at them,
 * and will extract the containing event every time such an event is received.
 * This ensures that deferred events never get picked up (again) by other state machines,
 * since other state machines will either have handled the event,
 * deferred it themselves or weren't interested in the first place.
 */
final class DeferredEvent extends Event {

	final StateMachine machine;
	final Event event;

	/**
	 * Construct an instance of DeferredEvent.
	 * This DeferredEvent is intended to be scheduled as soon as the state machine executes a transition.
	 * When run, it will be extracted by the scheduler so that the state machine receives the deferred event itself,
	 * instead of this instance of DeferredEvent (which it couldn't handle since it is package private).
	 * 
	 * @param machine	the state machine which deferred the event
	 * @param theEvent	the event which was deferred
	 * @return	A DeferredEvent instance which contains the deferred event. It will only fire the appropriate state machine.
	 */
	static DeferredEvent defer(StateMachine machine, Event theEvent) {
		if (theEvent instanceof DeferredEvent) {
			DeferredEvent deferredEvent = (DeferredEvent) theEvent;
			if (deferredEvent.machine != machine)
				throw new IllegalArgumentException("Stacking an impossible combination of Deferred Events");
			return deferredEvent;
		}
		return new DeferredEvent(machine, theEvent);
	}
	
	private DeferredEvent(StateMachine machine, Event theEvent) {
		this.machine = machine;
		event = theEvent;
	}

}
