/**
 * 
 */
package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.MessageEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.MessageEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.SwitchEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.TimerEvent;

/**
 * @author yorn
 *
 */
public class Receiver implements IStateMachine {

	private State state;
	
	public static interface State {
		public State FREE = new State(){public String toString(){return "FREE";}};
		public State WAIT_APPROVED = new State(){public String toString(){return "WAIT_APPROVED";}};
		public State BUSY = new State(){public String toString(){return "BUSY";}};
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
	 */
	public EAction fire(Event event, Scheduler scheduler) {
		if (state == null) {
			scheduler.subscribe(this, new SwitchEventType(2));
			scheduler.subscribe(this, MessageEventType.BROADCAST);
			state = State.FREE;
			return EAction.EXECUTE_TRANSITION ;
		} else if (state == State.FREE) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (Message.BROADCAST_ADDRESS.equals(messageEvent.message.getReceiver())) {
					sendMessage(new Message(
							getName(), 
							messageEvent.message.getSender(), 
							Message.ICanDisplayReadings
						));
					
					state = State.WAIT_APPROVED;
					return EAction.EXECUTE_TRANSITION ;
				}
			}
			return EAction.DISCARD_EVENT;
		} else if (state == State.WAIT_APPROVED) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (Message.Approved.equals(messageEvent.message)) {
					TimerEvent.schedule(this, scheduler, 5000);
					state = State.BUSY;
					return EAction.EXECUTE_TRANSITION ;
				}
				else if (Message.Denied.equals(messageEvent.message)) {
					state = State.FREE;
					return EAction.EXECUTE_TRANSITION ;
				}
			}
			return EAction.DISCARD_EVENT;
		} else if (state == State.BUSY) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (messageEvent.message.getContent().startsWith(Message.Reading)) {
					String result = messageEvent.message.getContent().substring(Message.Reading.length());
					// Do something with result
					// reset timer
					return EAction.EXECUTE_TRANSITION ;
				}
				if (Message.SenderDisconnect.equals(messageEvent.message.getContent())) {
					// stop timer
					// blink LEDs
					state = State.FREE;
					return EAction.EXECUTE_TRANSITION ;
				}
			}
			if (event instanceof ButtonEvent && event.id == 2) {
				// stop timer
				sendMessage(new Message(
						getName(),
						null, // name of the other party
						Message.ReceiverDisconnect
					));
				// blink LEDs
				state = State.FREE;
				return EAction.EXECUTE_TRANSITION ;
			}
			if (event instanceof TimerEvent && event.machine == this) {
				state = State.FREE;
				return EAction.EXECUTE_TRANSITION ;
			}
			return EAction.DISCARD_EVENT;
		}
		
		return EAction.DISCARD_EVENT;
	}

}
