package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Action;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.MessageEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.MessageEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.StateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.SwitchEvent;
import no.ntnu.item.ttm4160.sunspot.runtime.SwitchEventType;
import no.ntnu.item.ttm4160.sunspot.runtime.TimerEvent;

public class Receiver extends StateMachine {

	private State state;
	private TimerEvent timer;
	
	public static interface State {
		public State FREE = new State(){public String toString(){return "FREE";}};
		public State WAIT_APPROVED = new State(){public String toString(){return "WAIT_APPROVED";}};
		public State BUSY = new State(){public String toString(){return "BUSY";}};
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
	 */
	public Action fire(Event event, Scheduler scheduler) {
		State state = this.state; // concurrency
		if (state == null)
			return fireOnInit(event, scheduler);
		if (state == State.FREE)
			return fireOnFree(event, scheduler);
		if (state == State.WAIT_APPROVED)
			return fireOnWaitApproved(event, scheduler);
		if (state == State.BUSY)
			return fireOnBusy(event, scheduler);
		throw new IllegalStateException(state.toString());
	}
	
	public Action fireOnInit(Event event, Scheduler scheduler) {
			scheduler.subscribe(this, new SwitchEventType(2));
			scheduler.subscribe(this, MessageEventType.BROADCAST);
			state = State.FREE;
			return Action.EXECUTE_TRANSITION ;
	}
	
	public Action fireOnFree(Event event, Scheduler scheduler) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (Message.BROADCAST_ADDRESS.equals(messageEvent.message.getReceiver())) {
					sendMessage(
							scheduler, 
							messageEvent.message.getSender(), 
							Message.ICanDisplayReadings
						);
					
					state = State.WAIT_APPROVED;
					return Action.EXECUTE_TRANSITION ;
				}
			}
			return Action.DISCARD_EVENT;
	}
	
	public Action fireOnWaitApproved(Event event, Scheduler scheduler) {
		if (event instanceof MessageEvent) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (Message.Approved.equals(messageEvent.message)) {
				timer = TimerEvent.schedule(this, scheduler, 5000);
				state = State.BUSY;
				return Action.EXECUTE_TRANSITION ;
			}
			else if (Message.Denied.equals(messageEvent.message)) {
				state = State.FREE;
				return Action.EXECUTE_TRANSITION ;
			}
		}
		return Action.DISCARD_EVENT;
	}
	
	public Action fireOnBusy(Event event, Scheduler scheduler) {
			if (event instanceof MessageEvent) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (messageEvent.message.getContent().startsWith(Message.Reading)) {
					String result = messageEvent.message.getContent().substring(Message.Reading.length());
					// Do something with result
					timer.reset();
					return Action.EXECUTE_TRANSITION ;
				}
				if (Message.SenderDisconnect.equals(messageEvent.message.getContent())) {
					timer.cancel();
					timer = null;
					// blink LEDs
					state = State.FREE;
					return Action.EXECUTE_TRANSITION ;
				}
			}
			if (event instanceof SwitchEvent && ((SwitchEvent)event).button == 2) {
				// stop timer
				sendMessage(scheduler,
						null, // name of the other party
						Message.ReceiverDisconnect
					);
				// blink LEDs
				state = State.FREE;
				return Action.EXECUTE_TRANSITION ;
			}
			if (event == timer) {
				state = State.FREE;
				return Action.EXECUTE_TRANSITION ;
			}
			return Action.DISCARD_EVENT;
	}

}
