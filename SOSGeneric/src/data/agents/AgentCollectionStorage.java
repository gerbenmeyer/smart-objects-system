package data.agents;

import java.util.List;
import java.util.Map;

import model.agent.Agent;
import model.agent.property.Property;

/**
 * AgentCollectionStorage provides a number of defined methods to mutate and
 * retrieve Agents. There can only be one AgentCollectionStorage per
 * application, which is set via the
 * {@link AgentCollectionStorage#setInstance(AgentCollectionStorage)} method.
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
	 * @param id
	 *            the identifier of the Agent
	 * @return true or false
	 */
	public abstract boolean containsKey(String id);

	/**
	 * Returns the main properties of an agent with the given id.
	 * 
	 * @param id
	 *            the id
	 * @return the properties
	 */
	public abstract Map<String, Property> get(String id);

	/**
	 * Returns the main properties of a list of agent with a given id.
	 * 
	 * @param ids the identifiers
	 * @return the list of with a map of properties
	 */
	public abstract List<Map<String, Property>> get(List<String> ids);

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
	 * @param agent
	 *            the Agent to be added
	 */
	public abstract void putAgent(Agent agent);
	
	public abstract List<Map<String, Property>> searchAgents(String search);

	/**
	 * Get the instance of AgentCollectionStorage for this application.
	 * 
	 * @return the instance
	 */
	public static AgentCollectionStorage getInstance() {
		return instance;
	}

	/**
	 * Sets the instance of AgentCollectionStorage for this application.
	 */
	public static void setInstance(AgentCollectionStorage agentCollectionStorage) {
		instance = agentCollectionStorage;
	}

	/**
	 * Deletes an Agent from the storage.
	 * 
	 * @param id the identifier of the Agent to be deleted
	 * @return success
	 */
	public abstract boolean delete(String id);
}