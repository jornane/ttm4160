package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IEventType;



public class SwitchEventType implements IEventType {

	public final int button;

	/**
	 * Construct a new SwitchEventType. It will match any SwitchEvent with the same button number.
	 * Button numbers start at 1
	 * @param button	the button number
	 * @throws IllegalArgumentException	when button is less than 1 or too high
	 */
	public SwitchEventType(int button) throws IllegalArgumentException {
		SwitchEvent.checkButtonNumber(button);
		this.button = button;
	}

	public boolean isInterestedIn(Event event) {
		return event instanceof SwitchEvent && ((SwitchEvent)event).button == button;
	}

}
