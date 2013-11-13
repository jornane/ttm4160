package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Scheduler implements IScheduler {

	private final BlockingPriorityQueue queue;
	private final Vector/*<IStateMachine>*/ stateMachines;
	
	private final Hashtable/*<IStateMachine,Vector<IEventType>>*/ subscriptions;

	public Scheduler(int maxPriority) {
		queue = new BlockingPriorityQueue(maxPriority);
		stateMachines = new Vector();
		subscriptions = new Hashtable();
	}
	
	public Scheduler(int maxPriority, double fairness) {
		queue = new BlockingPriorityQueue(maxPriority, fairness);
		stateMachines = new Vector();
		subscriptions = new Hashtable();
	}

	/**
	 * Execute the scheduler.
	 * Before this gets called,
	 * at least one state machine must have been added.
	 * The scheduler will terminate when there are no more state machines.
	 */
	public void run() {
		queue.clear();
		while(stateMachines.size() > 0) {
			try {
				Event event = (Event)queue.next();
				IStateMachine machine = null;
				for(
						Enumeration e=subscriptions.keys();
						e.hasMoreElements();
						machine=(IStateMachine)e.nextElement()
				) {
					Vector list = (Vector) subscriptions.get(machine);
					for(int i=0;i<list.size();i++) {
						if (event.isAlive() && ((IEventType) list.elementAt(i)).isInterestedIn(event)) {
							fire(event, machine);
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void subscribe(IStateMachine machine, IEventType type) {
		if (!subscriptions.containsKey(machine))
			subscriptions.put(machine, new Vector());
		((Vector)subscriptions.get(machine)).addElement(type);
	}

	private void fire(Event event, IStateMachine machine) {
		EAction action = machine.fire(event, this);
		if(action==EAction.DISCARD_EVENT) {
			System.err.println("Discarded Event: "+event);
		} else if(action==EAction.TERMINATE_SYSTEM) {
			stateMachines.removeElement(machine);
			System.err.println("Terminating machine "+machine);
		}
	}
	
	public int getMaxPriority() {
		return queue.getMaxPriority();
	}
	
	void schedule(Event event, int priority) {
		queue.push(event, priority);
	}

}
