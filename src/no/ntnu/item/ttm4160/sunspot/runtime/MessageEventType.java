package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

public class MessageEventType implements IEventType {

	public final static IEventType BROADCAST = new MessageEventType(Message.BROADCAST_ADDRESS);
	
	public final String receiver;

	/**
	 * Construct a new MessageEvent Type.
	 * It will match on a receiver.
	 * 
	 * @param receiver	the receiver
	 */
	public MessageEventType(String receiver) {
		if (receiver == null)
			throw new NullPointerException();
		this.receiver = receiver;
	}

	public boolean isInterestedIn(Event event) {
		return event instanceof MessageEvent
				&& receiver.equals(((MessageEvent)event).message.getReceiver())
				;
	}

}
