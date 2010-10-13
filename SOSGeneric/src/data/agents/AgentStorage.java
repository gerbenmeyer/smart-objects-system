package data.agents;

import java.util.HashMap;
import java.util.Set;

import model.agent.property.Property;

/**
 * AgentStorage provides a number of defined methods to mutate and retrieve Agent properties.
 * There can only be one AgentStorage per application, which is set via the {@link AgentStorage#setInstance(AgentStorage)} method.
 * 
 * @author W.H. Mook
 */
public abstract class AgentStorage {
	
	/**
	 * the instance
	 */
	private static AgentStorage instance;
	
	/**
	 * Retrieves a Property of a certain Agent from the storage.
	 * 
	 * @param id the identifier of the Agent
	 * @param name the Property's name
	 * @return the Property
	 */
	public abstract Property getProperty(String id, String name);
	
	/**
	 * Retrieves the value of a Property of a certain Agent from the storage.
	 * 
	 * @param id the identifier of the Agent
	 * @param name the Property's name
	 * @return the value
	 */
	
	public abstract String getPropertyValue(String id, String name);

	/**
	 * Retrieves the properties of a certain Agent from the storage.
	 * 
	 * @param id the identifier of the Agent
	 * @return the properties
	 */
	public abstract HashMap<String, Property> getProperties(String id);
	
	/**
	 * Get a Set with the names of each Property of a certain Agent.
	 * 
	 * @param id the identifier of the Agent
	 * @return the Set
	 */
	public abstract Set<String> getPropertiesKeySet(String id);
	
	/**
	 * Stores a Property for a certain Agent.
	 * 
	 * @param id the identifier of the Agent
	 * @param p the Property to be stored
	 */
	public abstract void putProperty(String id, Property p);

	/**
	 * Stores properties for a certain Agent.
	 * 
	 * @param id the identifier of the Agent
	 * @param properties the properties to be stored
	 */
	public abstract void putProperties(String id, HashMap<String, Property> properties);
	
	/**
	 * Deletes a Property for a certain Agent.
	 * 
	 * @param id the identifier of the Agent
	 * @param name the name of the Property to be deleted
	 */
	public abstract void removeProperty(String id, String name);
	
	/**
	 * Deletes an Agent entirely from the storage.
	 * 
	 * @param id the identifier of the Agent
	 * @return success
	 */
	public abstract boolean delete(String id);
	
	/**
	 * Get the instance of AgentStorage for this application.
	 * 
	 * @return the instance
	 */
	public static AgentStorage getInstance(){
		return instance;
	}
	
	/**
	 * Sets the instance of AgentStorage for this application.
	 */
	public static void setInstance(AgentStorage agentStorage){
		instance = agentStorage;
	}
}