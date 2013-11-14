package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import no.ntnu.item.ttm4160.sunspot.communication.Communications;
import no.ntnu.item.ttm4160.sunspot.communication.Message;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.util.IEEEAddress;

public class Scheduler implements Runnable {

	private final BlockingPriorityQueue queue;
	private final Vector/*<IStateMachine>*/ stateMachines;
	
	private final Hashtable/*<IStateMachine,Vector<IEventType>>*/ subscriptions;
	
	private final static Communications communications;
	
	static {
		communications = new Communications(getMacAddress());
	}

	/**
	 * Get the MAC address of the current hardware
	 * @return	the MAC address
	 */
	protected static String getMacAddress() {
		return new IEEEAddress(Spot.getInstance().getRadioPolicyManager().getIEEEAddress()).asDottedHex();
	}

	/**
	 * Construct a scheduler
	 * @param maxPriority	the maximum priority
	 */
	public Scheduler(int maxPriority) {
		queue = new BlockingPriorityQueue(maxPriority);
		stateMachines = new Vector();
		subscriptions = new Hashtable();
		communications.registerListener(new MessageEvent.Listener(this));
	}
	
	/**
	 * Construct a fair scheduler
	 * @param maxPriority	the maximum priority
	 * @param fairness	the fairness (chance that a priority class doesn't get served)
	 */
	public Scheduler(int maxPriority, double fairness) {
		queue = new BlockingPriorityQueue(maxPriority, fairness);
		stateMachines = new Vector();
		subscriptions = new Hashtable();
		communications.registerListener(new MessageEvent.Listener(this));
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
				StateMachine machine = null;
				for(
						Enumeration e=subscriptions.keys();
						e.hasMoreElements();
						machine=(StateMachine)e.nextElement()
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

	/**
	 * Send a remote message to
	 * @param message	the message
	 */
	public void sendMessage(Message message) {
		communications.sendRemoteMessage(message);
	}
	
	/**
	 * Subscribe an event type for a state machine
	 * @param machine	the state machine which is interested in a specified event type
	 * @param type	the event type the state machine is interested in
	 */
	public void subscribe(StateMachine machine, IEventType type) {
		((Vector)subscriptions.get(machine)).addElement(type);
	}

	/**
	 * Fire an event to a state machine
	 * @param event	the event to fire
	 * @param machine	the state machine to fire to
	 */
	private void fire(Event event, StateMachine machine) {
		Action action = machine.fire(event, this);
		if(action==Action.DISCARD_EVENT) {
			System.err.println("Discarded Event: "+event);
		} else if(action==Action.TERMINATE_SYSTEM) {
			stateMachines.removeElement(machine);
			subscriptions.remove(machine);
			System.err.println("Terminating machine "+machine);
		}
	}
	
	/**
	 * Get the maximum priority of the queue
	 * @return	the maximum priority
	 */
	public int getMaxPriority() {
		return queue.getMaxPriority();
	}
	
	/**
	 * Push an event, this will usually make sure {@link #fire(Event, StateMachine)} is called shortly after.
	 * @param event	the event to push
	 * @param priority	the priority of the event
	 */
	void pushEventHappened(Event event, int priority) {
		queue.push(event, priority);
	}

	/**
	 * Add a state machine
	 * @param machine	the machine to add
	 */
	public void addMachine(StateMachine machine) {
		stateMachines.addElement(machine);
		subscriptions.put(machine, new Vector());
	}

}
