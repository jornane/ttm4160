package no.ntnu.item.ttm4160.sunspot.runtime.util;

import java.util.Vector;

import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IScheduler;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;

public final class SwitchEvent extends Event {

	public static class SwitchListener implements ISwitchListener {

		private final int button;

		public SwitchListener(int button) {
			this.button = button;
		}

		/**
		 * Schedule a SwitchEvent for the pressed
		 */
		public void switchPressed(ISwitch s) {
			for(int i=0;i<schedulers.size();i++) {
				((IScheduler)schedulers.elementAt(i)).eventHappened(new SwitchEvent(button), 0);
			}
		}

		public void switchReleased(ISwitch arg0) {/* do nothing */}
	}

	private static ISwitch[] switches;
	private static Vector schedulers = new Vector();

	static {
		switches = EDemoBoard.getInstance().getSwitches();
		for(int i=0;i<switches.length;i++)
			switches[i].addISwitchListener(new SwitchListener(i+1));
	}
	
	/**
	 * Add a scheduler on which SwitchEvents get scheduled
	 * @param scheduler	the scheduler to add
	 */
	public static void addScheduler(IScheduler scheduler) {
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
