package no.ntnu.item.ttm4160.sunspot.runtime;

public class TimerEventType implements IEventType {

	public final StateMachine machine;

	/**
	 * Create a new TimerEventType which matches on a state machine
	 * @param machine	the machine to match
	 */
	public TimerEventType(StateMachine machine) {
		this.machine = machine;
	}

	public boolean isInterestedIn(Event event) {
		return event instanceof TimerEvent && ((TimerEvent)event).machine == machine;
	}
	
}
