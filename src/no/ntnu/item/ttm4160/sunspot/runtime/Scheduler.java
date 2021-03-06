package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Scheduler implements IScheduler {

	private final BlockingPriorityQueue/*<Event>*/ queue;
	private final Hashtable/*<IStateMachine,Vector<IEventType>>*/ subscriptions;
	private final Hashtable/*<IStateMachine,Vector<DeferredEvent>>*/ save;

	/**
	 * Construct a scheduler
	 * @param maxPriority	the maximum priority
	 */
	public Scheduler(int maxPriority) {
		queue = new BlockingPriorityQueue(maxPriority);
		subscriptions = new Hashtable();
		save = new Hashtable();
	}

	/**
	 * Construct a fair scheduler
	 * @param maxPriority	the maximum priority
	 * @param fairness	the fairness (chance that a priority class doesn't get served)
	 */
	public Scheduler(int maxPriority, double fairness) {
		queue = new BlockingPriorityQueue(maxPriority, fairness);
		subscriptions = new Hashtable();
		save = new Hashtable();
	}

	public void run() {
		queue.clear();
		while(subscriptions.size() > 0) {
			try {
				Event event = (Event) queue.nextBlock();
				Enumeration e=subscriptions.keys();
				while(e.hasMoreElements()) {
					StateMachine machine = (StateMachine) e.nextElement();
					Vector list = (Vector) subscriptions.get(machine);
					if (list != null && event.isAlive()) for(int i=0;i<list.size();i++) {
						if (((IEventType) list.elementAt(i)).matches(event)) {
							if (event instanceof DeferredEvent)
								fire(((DeferredEvent) event).event, machine);
							else
								fire(event, machine);
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void subscribe(StateMachine machine, IEventType type) {
		((Vector)subscriptions.get(machine)).addElement(type);
	}
	
	public void unsubscribe(StateMachine machine, IEventType type) {
		((Vector)subscriptions.get(machine)).removeElement(type);
	}

	/**
	 * Fire an event to a state machine
	 * @param event	the event to fire
	 * @param machine	the state machine to fire to
	 */
	private void fire(Event event, StateMachine machine) {
		Action action = machine.fire(event, this);
		if (action == Action.DISCARD_EVENT) {
			System.err.println("Discarded Event: "+event);
		} else if (action == Action.TERMINATE_SYSTEM) {
			subscriptions.remove(machine);
			save.remove(machine);
			System.err.println("Terminating machine "+machine);
		} else if (action == Action.EXECUTE_TRANSITION) {
			Vector/*<Event>*/ deferredEvents = ((Vector) save.get(machine));
			if (deferredEvents != null && deferredEvents.size() > 0) {
				synchronized(deferredEvents) {
					for(int i=0;i<deferredEvents.size();i++) {
						queue.push(
								((DeferredEvent) deferredEvents.elementAt(i)).event, 
								queue.getMaxPriority() > 1 ? queue.getMaxPriority()-1 : 0 
							);
					}
					deferredEvents.removeAllElements();
				}
			}
		}
	}
	
	public int getMaxPriority() {
		return queue.getMaxPriority();
	}

	public void eventHappened(Event event, int priority) {
		queue.push(event, priority);
	}

	public void defer(StateMachine machine, Event event) {
		if (!save.containsKey(machine)) {
			synchronized(save) {
				if (!save.containsKey(machine))
					save.put(machine, new Vector());
			}
		}
		Vector/*<DeferredEvent>*/ deferredEvents = (Vector) save.get(machine);
		synchronized(deferredEvents) {
			deferredEvents.addElement(DeferredEvent.defer(machine, event));
		}
	}

	public void addMachine(StateMachine machine) {
		subscriptions.put(machine, new Vector());
		fire(null, machine);
		subscribe(machine, new DeferredEventType(machine));
	}

}
