package no.ntnu.item.ttm4160.sunspot.runtime.util;

import no.ntnu.item.ttm4160.example.CommunicatingStateMachine;
import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayerListener;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IScheduler;

/**
 * Event for messages to local state machines. Characteristic is that the receiver is local,
 * the sender may still be remote.
 * Another way of saying this, is that the message contained in a LocalMessageEvent
 * should not be sent over any network connection but it may have been sent over one before.
 * 
 * Opposed to this is the (not implemented) RemoveMessageEvent and (not implemented) MessageEvent,
 * which will respectively only deliver the message remote or
 * decide what to do with the message depending on the recipient.
 * 
 * The MessageEvent class is not implemented because it is not necessary for the example application,
 * and the RemoteMessageEvent is replaced by a quick-fix to implement
 * {@link CommunicatingStateMachine#sendRemoteMessage(Message)}.
 */
public class LocalMessageEvent extends Event {
    private static class Listener implements ICommunicationLayerListener {

		private final IScheduler scheduler;

		public Listener(IScheduler scheduler) {
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
    public LocalMessageEvent(Message message){
        this.message = message;
    }

    /**
     * Make sure that when a remote message is received (which will continue as LocalMessage),
     * it will be sent to the scheduler.
     * 
     * @param communications	the {@link ICommunicationLayer} to listen on for messages
     * @param scheduler	the scheduler to deliver the message to
     */
	public static void addScheduler(ICommunicationLayer communications,
			IScheduler scheduler) {
        communications.registerListener(new LocalMessageEvent.Listener(scheduler));
	}

}
