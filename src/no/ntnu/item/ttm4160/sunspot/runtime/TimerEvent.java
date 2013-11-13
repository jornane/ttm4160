package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Timer;
import java.util.TimerTask;

public class TimerEvent extends Event {

	public static class TimerEventTimerTask extends TimerTask {

		private Scheduler scheduler;
		private TimerEvent event;

		public TimerEventTimerTask(Scheduler scheduler, TimerEvent event) {
			this.scheduler = scheduler;
			this.event = event;
		}

		public void run() {
			scheduler.schedule(event, scheduler.getMaxPriority());
		}
		
	}
	
	private final Timer timer;
	
	public final IStateMachine machine;
	
	public TimerEvent(IStateMachine machine, Scheduler scheduler, long delay) {
		this.machine = machine;
		timer = new Timer();
		timer.schedule(new TimerEventTimerTask(scheduler, this), delay);
	}
	
	public void cancel() {
		super.cancel();
		timer.cancel();
	}

}
