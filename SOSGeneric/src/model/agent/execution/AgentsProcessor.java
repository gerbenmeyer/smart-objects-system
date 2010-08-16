package model.agent.execution;

import java.util.GregorianCalendar;

import model.agent.Agent;
import model.agent.collection.AgentCollection;
import model.agent.index.AgentIndex;

public class AgentsProcessor implements Runnable {

	private static int maxProcessingTimeMillis = 10000;
	private static int delayMilliseconds = 10;
	private int iterator;
	private long agentExecutionEnabledFromTimeStampInMillis = 0;
	private boolean paused = false;
	private AgentCollection agents;
	private AgentIndex index;

	/**
	 * Constructs a new AgentsProcessor.
	 * 
	 * @param agents
	 *            the agents to be processed
	 * @param index
	 *            the AgentIndex to be updated after processing an agent
	 */
	public AgentsProcessor(AgentCollection agents, AgentIndex index) {
		this.agents = agents;
		this.index = index;
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

			if (new GregorianCalendar().getTimeInMillis() < agentExecutionEnabledFromTimeStampInMillis) {
				if (!paused) {
					long diff = System.currentTimeMillis() - startTime;
					long agentsPerMinute = 0;
					if (diff > 60000) {
						agentsPerMinute = agentsProcessed / (diff / 60000);
					}
					System.out.println("Execution of " + agents.getIndex().getAgentIDs().size()
							+ " agents paused (average execution speed: " + agentsPerMinute + " agents/min)");
					paused = true;
					agentsProcessed = 0;
				}
			} else {
				if (paused) {
					System.out.println("Execution of " + agents.getIndex().getAgentIDs().size() + " agents resumed");
					paused = false;
					startTime = System.currentTimeMillis();
				}
			}

			if (!paused) {

				if (index.getAgentIDs().size() == 0)
					continue;

				// Retrieve agent from the iterator.
				iterator = ((iterator + 1) % index.getAgentIDs().size());
				String id = index.getAgentIDs().get(iterator);

				if (agents.get(id) instanceof Agent) {
					Agent agent = (Agent) agents.get(id);
					agentsProcessed++;

					// collect garbage
					if (agent.isGarbage()) {
						agent.lastWish();
						agents.remove(agent.getID());
						iterator--;
						continue;
					}

					String oldStatus = agent.toXML();

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
						System.out.println("Execution of agent " + agent.getLabel() + " (" + agent.getType()
								+ ") timed out!");
						processor = new AgentExecutor(delayMilliseconds);
					}

					// wait till processor is done
					while (!processor.isDone()) {
						System.out.println("Processor is not done with agent " + agent.getLabel() + " ("
								+ agent.getType() + ")!");
						try {
							Thread.sleep(delayMilliseconds);
						} catch (InterruptedException e) {
						}
					}

					String newStatus = agent.toXML();

					if (!oldStatus.equals(newStatus)) {
						index.update(agent);
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