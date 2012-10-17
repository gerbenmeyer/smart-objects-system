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
import model.messageboard.MessageBoard;

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
	
	private List<String> agentIds = null;;

	/**
	 * Constructs a new AgentsProcessor.
	 */
	public AgentsProcessor() {
		instance = this;

		String priority = Settings.getProperty(Settings.AGENT_EXECUTION_PRIORITY);

		if (priority.equals("normal")) {
			delayMilliseconds = 50;
			for (int i = 0; i < 4; i++){
				executors.add(new AgentExecutor(delayMilliseconds));	
			}
		} else if (priority.equals("high")) {
			delayMilliseconds = 0;
			for (int i = 0; i < 8; i++){
				executors.add(new AgentExecutor(delayMilliseconds));	
			}
		} else {
			delayMilliseconds = 100;
			executors.add(new AgentExecutor(delayMilliseconds));
		}

		agentIds = AgentCollection.getInstance().getIDs();
		
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
						if (id == null){
							continue;
						}
						AgentViewable av = AgentCollection.getInstance().get(id);
						if (av == null) {
							continue;
						}
						if (av instanceof Agent) {

							Agent agent = (Agent) av;
							agentsProcessed++;

							// collect garbage
							if (!agent.isMarkedForDeletion() && agent.isGarbage()) {
								agent.lastWish();
								agent.delete();
							}
							
							// delete agent
							if (agent.isMarkedForDeletion()) {
								if (AgentCollectionStorage.getInstance() != null) {
									AgentCollectionStorage.getInstance().delete(id);
								}
								if (AgentStorage.getInstance() != null) {
									AgentStorage.getInstance().delete(id);
								}
								MessageBoard.getInstance().removeAgent(id);
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
		if (index >= agentIds.size()){
			index = 0;
			agentIds = AgentCollection.getInstance().getIDs();
		}
		if (agentIds.isEmpty()){
			return null;
		}		
		String id = agentIds.get(index);
		index++;
		return id;
	}
}