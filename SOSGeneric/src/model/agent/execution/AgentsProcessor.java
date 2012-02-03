package model.agent.execution;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import data.agents.AgentCollectionStorage;
import data.agents.AgentStorage;

import main.SOSServer;
import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;

/**
 * The AgentsProcessor controls the processing of agents.
 * 
 * @author G.G. Meyer
 */
public class AgentsProcessor implements Runnable {

	private static int maxProcessingTimeMillis = 10000;
	private static int delayMilliseconds = 50;
	private long resumeAgentExecutionFromTimeStampInMillis = 0;
	private boolean paused = false;

	private static AgentsProcessor instance;
	private Vector<AgentExecutor> executors = new Vector<AgentExecutor>();
	
	private int index = 0;

	/**
	 * Constructs a new AgentsProcessor.
	 */
	public AgentsProcessor() {
		instance = this;

		String priority = Settings.getProperty(Settings.AGENT_EXECUTION_PRIORITY);
		
		executors.add(new AgentExecutor(delayMilliseconds));

		if (priority != null) {
			if (priority.equals("low")) {
				delayMilliseconds = 100;
				System.out.println("Agent execution priorty: low");
			} else if (priority.equals("high")) {
				delayMilliseconds = 0;
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				System.out.println("Agent execution priorty: high");
			} else {
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));
				executors.add(new AgentExecutor(delayMilliseconds));				
				System.out.println("Agent execution priorty: normal");
			}
		} else {
			System.out.println("Agent execution priorty: not set");
		}

		(new Thread(this)).start();
	}

	/**
	 * Get the one instance of AgentsProcessor.
	 * 
	 * @return the instance
	 */
	public static AgentsProcessor getInstance() {
		return instance;
	}

	/**
	 * Pauses the current processing thread.
	 */
	public void pause() {
		resumeAgentExecutionFromTimeStampInMillis = new GregorianCalendar().getTimeInMillis() + 5000;
		while (!paused) {
			try {
				Thread.sleep(delayMilliseconds);
			} catch (InterruptedException e) {
			}
			resumeAgentExecutionFromTimeStampInMillis = new GregorianCalendar().getTimeInMillis() + 5000;
		}
	}

	/**
	 * Runs the agent processor, by executing every agent in a round-robin
	 * manner.
	 */
	public void run() {


		long agentsProcessed = 0;
		long startTime = System.currentTimeMillis();

		while (true) {
			if (new GregorianCalendar().getTimeInMillis() < resumeAgentExecutionFromTimeStampInMillis) {
				if (!paused) {
					long diff = System.currentTimeMillis() - startTime;
					long agentsPerMinute = 0;
					if (diff > 60000) {
						agentsPerMinute = agentsProcessed / (diff / 60000);
					}
					SOSServer.getDevLogger().info("Execution of " + AgentCollection.getInstance().getSize() + " agents paused (average execution speed: " + agentsPerMinute + " agents/min)");
					paused = true;
					agentsProcessed = 0;
				}
			} else {
				if (paused) {
					SOSServer.getDevLogger().info("Execution of " + AgentCollection.getInstance().getSize() + " agents resumed");
					paused = false;
					startTime = System.currentTimeMillis();
				}
			}

			if (!paused) {
				for (AgentExecutor executor : executors){
					if (executor.isDone()){
						String id = getNextID();
						AgentViewable av = AgentCollection.getInstance().get(id);
						if (av == null) {
							continue;
						}
						if (av instanceof Agent) {

							Agent agent = (Agent) av;
							agentsProcessed++;

							// delete agent
							if (agent.isMarkedForDeletion()) {
								if (AgentCollectionStorage.getInstance() != null) {
									AgentCollectionStorage.getInstance().delete(id);
								}
								if (AgentStorage.getInstance() != null) {
									AgentStorage.getInstance().delete(id);
								}
								continue;
							}

							// collect garbage
							if (agent.isGarbage()) {
								agent.lastWish();
								agent.delete();
								continue;
							}
							
							// insert agent into the processor.
							executor.setAgent(agent);
						}
					} else {
						// check if not using too much time
						boolean timedOut = true;
						try {
							timedOut = executor.timeout(maxProcessingTimeMillis);
						} catch (InterruptedException e) {
						}

						// if the processor timed out, create a new one.
						if (timedOut) {
							SOSServer.getDevLogger().warning("Execution of agent " + executor.getAgent().get(Agent.LABEL) + " (" + executor.getAgent().get(Agent.TYPE) + ") timed out!");
							executor = new AgentExecutor(delayMilliseconds);
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
	
	private String getNextID(){
		List<String> ids = AgentCollection.getInstance().getIDs();
		if (index >= ids.size()){
			index = 0;
		}
		String id = ids.get(index);
		index++;
		return id;
	}
}