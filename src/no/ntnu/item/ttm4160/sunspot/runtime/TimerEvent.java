package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Timer;

public class TimerEvent extends Event {

	private final Timer timer;
	
	public TimerEvent(IStateMachine stateMachine, Timer timer) {
		super(stateMachine);
		this.timer = timer;
	}
	
	public void cancel() {
		super.cancel();
		timer.cancel();
	}

}
