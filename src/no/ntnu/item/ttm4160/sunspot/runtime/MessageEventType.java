package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

public class MessageEventType implements IEventType {

	public final static IEventType BROADCAST = new MessageEventType();
	
	public final String sender;

	private MessageEventType() {
		this.sender = null;
	}
	
	public MessageEventType(String sender) {
		this.sender = sender;
	}

	public boolean isInterestedIn(Event event) {
		Message message = ((MessageEvent)event).message;
		return event instanceof MessageEvent && this == BROADCAST
				? Message.BROADCAST_ADDRESS.equals(message.getReceiver())
				: sender.equals(message.getSender())
				;
	}

}
