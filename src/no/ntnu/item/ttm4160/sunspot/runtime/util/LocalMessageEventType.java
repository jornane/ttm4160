package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.example.CommunicatingStateMachine;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IEventType;

/**
 * EventType to indicate interest in a LocalMessageEvent.
 * This should be used by a state machine to either indicate interest in messages directly sent to it,
 * or to indicate interest in broadcast messages.
 * However, it can also be used to intercept messages to other state machines,
 * if the state machine object is available to the interscepting state machine.
 */
public class LocalMessageEventType implements IEventType {

	/**
	 * EventType which indicates interest in messages which have broadcast as recipient.
	 */
	public final static IEventType BROADCAST = new LocalMessageEventType();
	
	/**
	 * This EventType matches messages with this machine as the recipient.
	 * If null, it matches messages with broadcast as recipient (see {@link #BROADCAST}).
	 */
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
