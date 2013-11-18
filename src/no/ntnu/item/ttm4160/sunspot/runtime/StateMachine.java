package no.ntnu.item.ttm4160.sunspot.runtime;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.Utils;

/**
 * A state machine will take action upon calling {@link #fire(Event, IScheduler)}
 * return what {@link Action} it took upon this call and keep state information.
 */
public abstract class StateMachine {

	/**
	 * Indicate that a new event is received from the scheduler.
	 * The call to this method is one round to completion step.
	 * 
	 * During the first call to {@link #fire(Event, IScheduler)} it is supposed to subscribe
	 * to events its interested in towards the calling {@link IScheduler}.
	 * In this call, event will be null. This will never be the case for subsequent calls.
	 * 
	 * @param event	the event that occurred
	 * @param scheduler	the scheduler that had scheduled the event
	 * @return	the action which was taken upon receiving the event
	 */
	public abstract Action fire(Event event, IScheduler scheduler);

	/**
	 * Blink the LEDs of the SunSPOT the state machine is running on.
	 * This method is a quick-fix; a better solution would be to implement a Blinker state machine,
	 * which would receive BlinkEvents with a color and execute them.
	 * 
	 * @param color	the color to blink with
	 */
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
