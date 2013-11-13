package no.ntnu.item.ttm4160.sunspot.runtime;

public class TimerEventType implements IEventType {

	public final IStateMachine machine;

	public TimerEventType(IStateMachine machine) {
		this.machine = machine;
	}

	public boolean isInterestedIn(Event event) {
		return event instanceof TimerEvent && ((TimerEvent)event).machine == machine;
	}
	
}
