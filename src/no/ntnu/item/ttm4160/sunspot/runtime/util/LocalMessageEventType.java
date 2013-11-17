package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.example.CommunicatingStateMachine;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IEventType;

public class LocalMessageEventType implements IEventType {

	public final static IEventType BROADCAST = new LocalMessageEventType();
	
	public final CommunicatingStateMachine machine;

	/**
	 * Construct a new MessageEvent Type.
	 * It will match on a receiver.
	 * 
	 * @param machine	the receiver
	 */
	public LocalMessageEventType(CommunicatingStateMachine machine) {
		if (machine == null)
			throw new NullPointerException();
		this.machine = machine;
	}

	private LocalMessageEventType() {
		machine = null;
	}

	public boolean matches(Event event) {
		return event instanceof LocalMessageEvent
				&& (machine == null
					? Message.BROADCAST_ADDRESS
					: (CommunicatingStateMachine.getMacAddress()+":"+machine.hashCode())
				).equals(((LocalMessageEvent)event).message.getReceiver())
			;
	}

}
