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

		/**
		 * Schedules the enclosing event in the scheduler with the highest priority.
		 */
		public void run() {
			scheduler.schedule(event, scheduler.getMaxPriority());
			event.cancel(); // ensure the event fires only once
		}
		
	}
	
	private Timer timer;
	
	/**
	 * The state machine to which this Timer is directed.
	 */
	public final IStateMachine machine;

	private final Scheduler scheduler;
	private final long delay;
	
	/**
	 * Construct a new timer event.
	 * The event will need to be started using #start() after construction.
	 * 
	 * @param machine	The state machine to which this Timer is directed
	 * @param scheduler	The scheduler of machine
	 * @param delay	The amount of milliseconds to wait until the event fires
	 */
	public TimerEvent(IStateMachine machine, Scheduler scheduler, long delay) {
		this.machine = machine;
		this.scheduler = scheduler;
		this.delay = delay;
		timer = new Timer();
	}
	
	/**
	 * Start the timer,
	 * it will fire (add schedule itself) after {@link #delay} milliseconds.
	 * @throws IllegalStateException	if not {@link #isAlive()}
	 */
	public void start() throws IllegalStateException {
		if (!isAlive())
			throw new IllegalStateException("Timer is dead");
		timer.schedule(new TimerEventTimerTask(scheduler, this), delay);
	}
	
	/**
	 * Cancel the timer.
	 * After calling this function, it will not fire anymore and cannot be restarted.
	 */
	public void cancel() {
		super.cancel();
		timer.cancel();
	}
	
	/**
	 * Cancels the timer without killing it and restarts it right away.
	 * This means that the timer will fire after {@link #delay} milliseconds from now,
	 * regardless on how long ago {@link #start()} was called - as long as the timer hasn't fired already.
	 * @throws IllegalStateException	if not {@link #isAlive()}
	 */
	public void reset() throws IllegalStateException {
		timer.cancel();
		timer = new Timer();
		start();
	}

}
