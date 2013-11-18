package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.StateMachine;



public class TimerEventType implements IEventType {

	/** The machine which should be notified when the timer expires */
	public final StateMachine machine;

	/**
	 * Create a new TimerEventType which matches on a state machine
	 * @param machine	the machine to match
	 */
	public TimerEventType(StateMachine machine) {
		this.machine = machine;
	}

	public boolean matches(Event event) {
		return event instanceof TimerEvent && ((TimerEvent)event).machine == machine;
	}
	
}
