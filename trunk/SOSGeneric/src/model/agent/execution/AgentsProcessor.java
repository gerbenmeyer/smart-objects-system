package model.agent.execution;

import java.util.GregorianCalendar;
import java.util.List;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;

public class AgentsProcessor implements Runnable {

	private static int maxProcessingTimeMillis = 10000;
	private static int delayMilliseconds = 10;
	private long agentExecutionEnabledFromTimeStampInMillis = 0;
	private boolean paused = false;

	/**
	 * Constructs a new AgentsProcessor.
	 * 
	 * @param agents
	 *            the agents to be processed
	 * @param index
	 *            the AgentIndex to be updated after processing an agent
	 */
	public AgentsProcessor() {
		(new Thread(this)).start();
	}

	/**
	 * Pauses the current processing thread.
	 */
	public void pause() {
		agentExecutionEnabledFromTimeStampInMillis = new GregorianCalendar().getTimeInMillis() + 5000;
		while (!paused) {
			try {
				Thread.sleep(delayMilliseconds);
			} catch (InterruptedException e) {
			}
			agentExecutionEnabledFromTimeStampInMillis = new GregorianCalendar().getTimeInMillis() + 5000;
		}
	}

	/**
	 * Runs the agent processor, by executing every agent in a round-robin
	 * manner.
	 */
	public void run() {
		// AgentProcessor processor = null;
		AgentExecutor processor = new AgentExecutor(delayMilliseconds);

		long agentsProcessed = 0;
		long startTime = System.currentTimeMillis();

		while (true) {

			List<String> ids = AgentCollection.getInstance().getIDs();

			for (String id : ids) {

				if (new GregorianCalendar().getTimeInMillis() < agentExecutionEnabledFromTimeStampInMillis) {
					if (!paused) {
						long diff = System.currentTimeMillis() - startTime;
						long agentsPerMinute = 0;
						if (diff > 60000) {
							agentsPerMinute = agentsProcessed / (diff / 60000);
						}
						System.out.println("Execution of " + AgentCollection.getInstance().getSize()
								+ " agents paused (average execution speed: " + agentsPerMinute + " agents/min)");
						paused = true;
						agentsProcessed = 0;
					}
				} else {
					if (paused) {
						System.out.println("Execution of " + AgentCollection.getInstance().getSize() + " agents resumed");
						paused = false;
						startTime = System.currentTimeMillis();
					}
				}

				if (!paused) {


					AgentViewable av = AgentCollection.getInstance().get(id);
					if (av == null){
						continue;
					}
					if (av instanceof Agent) {
						Agent agent = (Agent) av;
						agentsProcessed++;

						// collect garbage
						if (agent.isGarbage()) {
							agent.lastWish();
							agent.delete();
							continue;
						}

						// insert agent into the processor.
						processor.setAgent(agent);

						// check if not using too much time
						boolean timedOut = true;
						try {
							timedOut = processor.timeout(maxProcessingTimeMillis);
						} catch (InterruptedException e) {
						}

						// if the processor timed out, create a new one.
						if (timedOut) {
							System.out.println("Execution of agent " + agent.get(Agent.LABEL) + " ("
									+ agent.get(Agent.TYPE) + ") timed out!");
							processor = new AgentExecutor(delayMilliseconds);
						}

						// wait till processor is done
						while (!processor.isDone()) {
							System.out.println("Processor is not done with agent " + agent.get(Agent.LABEL) + " ("
									+ agent.get(Agent.TYPE) + ")!");
							try {
								Thread.sleep(delayMilliseconds);
							} catch (InterruptedException e) {
							}
						}

					}
				}
				try {
					Thread.sleep(delayMilliseconds);
				} catch (InterruptedException e) {

				}
			}
		}
	}
}