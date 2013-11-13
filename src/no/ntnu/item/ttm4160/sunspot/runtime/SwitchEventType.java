package no.ntnu.item.ttm4160.sunspot.runtime;

public class SwitchEventType implements IEventType {

	public final int id;

	public SwitchEventType(int id) {
		this.id = id;
	}

	public boolean isInterestedIn(Event event) {
		if (event instanceof SwitchEvent && ((SwitchEvent)event).id == id);
	}

}
