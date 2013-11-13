package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class Scheduler implements IScheduler {

	private BlockingPriorityQueue queue;
	private Vector stateMachines;

	public Scheduler(int maxPriority) {
		queue = new BlockingPriorityQueue(maxPriority);
	}

	public void run() {
		while(stateMachines.size() > 0) {
			try {
				Event event = (Event)queue.next();
				IStateMachine machine = event.getMachine();
				if (stateMachines.contains(machine) && event.isAlive())
					fire(event, machine);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	public Vector getMachinesForString(String machine) {
		Vector result = new Vector();
		for(int i=0;i<stateMachines.size();i++)
			if (((IStateMachine)stateMachines.elementAt(i)).answersTo(machine))
				result.addElement(stateMachines.elementAt(i));
		return result;
	}

}
