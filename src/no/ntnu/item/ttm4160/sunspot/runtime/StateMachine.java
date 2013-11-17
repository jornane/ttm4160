package no.ntnu.item.ttm4160.sunspot.runtime;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;

public abstract class StateMachine {

	/**
	 * Indicate that a new event is received from the scheduler.
	 * The call to this method is one round to completion step.
	 * @param event	the event that occurred
	 * @param scheduler	the scheduler that had scheduled the event
	 * @return	the action which was taken upon receiving the event
	 */
	public abstract Action fire(Event event, IScheduler scheduler);

	protected void blink(LEDColor color) {
		ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
		for(int j=0;j<4;j++) {
	        for (int i = 0; i < 8; i++) {
	            leds[i].setColor(color);
	            leds[i].setOn(i%2==0);
	        }
	        Utils.sleep(50);
	        for (int i = 0; i < 8; i++) {
	            leds[i].setOn(i%2!=0);
	        }
	        Utils.sleep(50);
		}
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
        }
	}

}
