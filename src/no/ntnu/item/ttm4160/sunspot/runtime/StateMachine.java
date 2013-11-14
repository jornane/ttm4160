package no.ntnu.item.ttm4160.sunspot.runtime;

import no.ntnu.item.ttm4160.sunspot.communication.Message;

public abstract class StateMachine {

	private static int nextId = 1;
	
	private int id;
	
	public StateMachine() {
		id = nextId++;
	}
	
	/**
	 * Indicate that a new event is received from the scheduler.
	 * The call to this method is one round to completion step.
	 * @param event	the event that occurred
	 * @param scheduler	the scheduler that had scheduled the event
	 * @return	the action which was taken upon receiving the event
	 */
	public abstract Action fire(Event event, Scheduler scheduler);
	
	/**
	 * Get the name of this state machine, as used in Message objects
	 * @return	the name of this state machine
	 */
	protected String getName() {
		return Scheduler.getMacAddress()+":"+id;
	}
	
	/**
	 * Send a remote message
	 * @param scheduler	the scheduler which must send the message
	 * @param recipient	the recipient (may be {@link Message#BROADCAST_ADDRESS})
	 * @param content	the content
	 */
	protected void sendMessage(Scheduler scheduler, String recipient, String content) {
		scheduler.sendMessage(new Message(
				getName(),
				recipient,
				content
			));
	}

}
