package model.agent.execution;

import model.agent.Agent;

/**
 * Class for executing an Agent.
 * 
 * @author Gijs B. Roest
 */
public class AgentExecutor extends Thread {

	private volatile boolean done, interrupted;
	private boolean DEBUG;
	private Agent agent;
	private int delay;

	/**
	 * Construct a new AgentExecutor.
	 * 
	 * @param delay
	 *            in miliseconds
	 */
	public AgentExecutor(int delay) {
		this(delay, false);
	}

	/**
	 * Construct a new AgentExecutor.
	 * 
	 * @param delay
	 *            in miliseconds
	 * @param DEBUG
	 *            set to true if debug output must be provided
	 */
	public AgentExecutor(int delay, boolean DEBUG) {
		this.DEBUG = DEBUG;
		this.done = true;
		this.delay = delay;
		super.start();

		if (DEBUG)
			System.out.println("Processor: New Processor");
	}

	/**
	 * Sets the agent to be executed.
	 * 
	 * @param agent
	 *            the agent to be executed.
	 */
	public synchronized void setAgent(Agent agent) {
		this.agent = agent;
		this.done = false;
	}

	/**
	 * Returns whether the execution of this agent is finished.
	 * 
	 * @return whether this AgentExecutor is finished.
	 */
	public synchronized boolean isDone() {
		return this.done;
	}

	/**
	 * Returns whether this agent used too much execution time.
	 * 
	 * @param timeOut
	 *            The maximum amount of allowed execution time
	 * @return boolean whether too much execution time is used
	 * @throws InterruptedException
	 */
	public boolean timeout(long timeOut) throws InterruptedException {
		long base = System.currentTimeMillis();
		long delay = 0;

		synchronized (this) {
			while (!done && !interrupted) {
				delay = timeOut - (System.currentTimeMillis() - base);
				if (delay <= 0) {
					if (DEBUG)
						System.out.println("Processor: terminated");
					interrupt();
					interrupted = true;
				} else {
					wait(delay);
				}
			}
		}
		return interrupted;
	}

	/**
	 * Executes the currently setted one agent.
	 */
	public void run() {
		while (!interrupted) {
			if (agent != null && !done && !interrupted) {
				if (DEBUG)
					System.out.println("Processor: " + agent.toString() + " > start.");
				try {
					agent.actAndSave();
					synchronized (this) {
						if (DEBUG)
							System.out.println("Processor: " + agent.toString() + " < completed.");
						done = true;
						notifyAll();
					}
				} catch (InterruptedException ie) {
					interrupted = true;
				}
			} else {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (DEBUG)
			System.out.println("Processor: Interrupted.");
	}
}