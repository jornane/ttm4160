package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayerListener;
import no.ntnu.item.ttm4160.sunspot.communication.Message;

/**
 * Event for messages to state machines
 */
public class MessageEvent extends Event {
    public static class Listener implements ICommunicationLayerListener {

		private final Scheduler scheduler;

		public Listener(Scheduler scheduler) {
			this.scheduler = scheduler;
		}

		public void inputReceived(Message message) {
			scheduler.pushEventHappened(new MessageEvent(message), 0);
		}

	}

    /**
     * The message which was received
     */
	public Message message;

    /**
     *
     * @param message is the message that's being sent
     */
    private MessageEvent(Message message){
        this.message = message;
    }

}
