package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Random;
import java.util.Stack;

public class BlockingPriorityQueue {

	private final Stack[] queue;
	private final double fairness;
	private final Random random = new Random();
	
	/**
	 * Construct a new queue
	 * @param maxPriority	the maximum priority an object can be given
	 * @param fairness	Chance that a task with a lower priority gets scheduled,
	 * 		even though higher priorities are available
	 */
	public BlockingPriorityQueue(int maxPriority, double fairness) {
		// assert maxPriority >= 0;
		queue = new Stack[maxPriority+1];
		for(int i=0;i<queue.length;i++)
			queue[i] = new Stack();
		this.fairness = fairness;
	}
	
	/**
	 * Get the next object from the queue. Higher priority objects will be favoured.
	 * This method will block until there is an object to return.
	 * If this is undesired, use #peek()
	 * @return the next object from the queue
	 * @throws InterruptedException if thread was interrupted during blocking
	 */
	public Object next() throws InterruptedException {
		while(true) {
			Object result = peek();
			if (result != null)
				return result;
			wait();
		}
	}
	
	/**
	 * Get the next object from the queue. Higher priority objects will be favoured.
	 * If there is no object to return, this method will return null. It will not block.
	 * For blocking, use #next()
	 * @return the next object from the queue
	 */
	public synchronized Object peek() {
		for(int i=getMaxPriority();i>=0;i--) {
			double number = random.nextDouble();
			if (!queue[i].empty() || number > fairness)
				return queue[i].pop();
		}
		return null;
	}

	/**
	 * Add an object to the queue
	 * @param obj	the object to add
	 * @param priority	priority of the object,
	 * 		0 is minimal and maxPriority from the constructor is maximal
	 */
	public void push(Object obj, int priority) {
		queue[priority].addElement(obj);
		notifyAll();
	}
	
	/**
	 * Get the maximum priority.
	 * This is the highest valid value for a priority argument in any function.
	 * @return	the maximum priority
	 */
	public int getMaxPriority() {
		return queue.length-1;
	}
	
}
