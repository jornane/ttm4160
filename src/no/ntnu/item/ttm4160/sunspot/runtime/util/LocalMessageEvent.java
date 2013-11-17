package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayerListener;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

/**
 * Event for messages to state machines
 */
public class LocalMessageEvent extends Event {
    private static class Listener implements ICommunicationLayerListener {

		private final Scheduler scheduler;

		public Listener(Scheduler scheduler) {
			this.scheduler = scheduler;
		}

		public void inputReceived(Message message) {
			scheduler.eventHappened(new LocalMessageEvent(message), 0);
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
    private LocalMessageEvent(Message message){
        this.message = message;
    }

	public static void addScheduler(ICommunicationLayer communications,
			Scheduler scheduler) {
        communications.registerListener(new LocalMessageEvent.Listener(scheduler));
	}

}
