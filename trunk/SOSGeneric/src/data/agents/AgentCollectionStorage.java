package data.agents;

import java.util.List;

import model.agent.Agent;

/**
 * AgentCollectionStorage provides a number of defined methods to mutate and retrieve Agents.
 * There can only be one AgentCollectionStorage per application, which is set via the {@link AgentCollectionStorage#setInstance(AgentCollectionStorage)} method.
 * 
 * @author W.H. Mook
 */
public abstract class AgentCollectionStorage {
	
	/**
	 * The instance.
	 */
	private static AgentCollectionStorage instance;
	
	/**
	 * Check if the collection contains a certain Agent.
	 * 
	 * @param id the identifier of the Agent.
	 * @return true or false
	 */
	public abstract boolean containsKey(String id);
	
	/**
	 * Get the number of agents in the collection.
	 * 
	 * @return the size
	 */
	public abstract int getSize();

	/**
	 * Get the different types of Agents in the collection.
	 * 
	 * @return the types list
	 */
	public abstract List<String> getTypes();

	/**
	 * Get the identifiers of all Agents in the collection.
	 * 
	 * @return the types list
	 */
	public abstract List<String> getIDs();
	
	/**
	 * Adds an Agent to the collection.
	 * 
	 * @param agent the Agent to be added
	 */
	public abstract void putAgent(Agent agent);

	/**
	 * Get the instance of AgentCollectionStorage for this application.
	 * 
	 * @return the instance
	 */
	public static AgentCollectionStorage getInstance(){
		return instance;
	}

	/**
	 * Sets the instance of AgentCollectionStorage for this application.
	 */
	public static void setInstance(AgentCollectionStorage agentCollectionStorage){
		instance = agentCollectionStorage;
	}
}