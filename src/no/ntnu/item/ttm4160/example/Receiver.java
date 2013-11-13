/**
 * 
 */
package no.ntnu.item.ttm4160.example;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.EAction;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

/**
 * @author yorn
 *
 */
public class Receiver implements IStateMachine {

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#fire(no.ntnu.item.ttm4160.sunspot.runtime.Event, no.ntnu.item.ttm4160.sunspot.runtime.Scheduler)
	 */
	public EAction fire(Event event, Scheduler scheduler) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine#answersTo(java.lang.String)
	 */
	public boolean answersTo(String id) {
		return this.id.equals(id) || Message.BROADCAST_ADDRESS.equals(id);
	}

}
