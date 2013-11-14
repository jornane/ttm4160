package no.ntnu.item.ttm4160.sunspot.runtime.util;

import java.util.Vector;

import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

import com.sun.spot.peripheral.SpotFatalException;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;

public final class SwitchEvent extends Event {

	public static class SwitchListener implements ISwitchListener {

		/**
		 * Schedule a SwitchEvent for the pressed
		 */
		public void switchPressed(ISwitch s) {
			for(int button=0;button<switches.length;button++)
				if (switches[button] == s) {
					for(int i=0;i<schedulers.size();i++)
						((Scheduler)schedulers.elementAt(i)).pushEventHappened(new SwitchEvent(button+1), 0);
					return;
				}
			throw new IllegalArgumentException("Received event from switch not in EDemoBoard");
		}

		public void switchReleased(ISwitch arg0) {/* do nothing */}
	}

	private static ISwitch[] switches;
	private static Vector schedulers;

	static {
		try {
		switches = EDemoBoard.getInstance().getSwitches();
		for(int i=0;i<switches.length;i++)
			switches[i].addISwitchListener(new SwitchListener());
		} catch (SpotFatalException e) {
			switches = new ISwitch[2];
		}
	}
	
	/**
	 * Add a scheduler on which SwitchEvents get scheduled
	 * @param scheduler	the scheduler to add
	 */
	public static void addScheduler(Scheduler scheduler) {
		schedulers.addElement(scheduler);
	}

	/**
	 * The number of the button, starting with 1
	 */
	public int button;
	
	private SwitchEvent(int button) {
		checkButtonNumber(button);
		this.button = button;
	}

	/**
	 * Throws an exception if button number is out of bounds.
	 * Does nothing if the number is valid. 
	 */
	public static void checkButtonNumber(int button) {
		if (button < 1)
			throw new IllegalArgumentException("Button number must be at least 1");
		if (button > switches.length)
			throw new IllegalArgumentException("Button number must be less or equal to "+switches.length);
	}

}
