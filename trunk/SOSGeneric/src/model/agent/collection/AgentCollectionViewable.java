package model.agent.collection;

import java.util.List;

import model.agent.AgentViewable;

/**
 * An interface to get read access to a collection of agents
 * 
 * @author Gerben G. Meyer
 *
 */
public interface AgentCollectionViewable {
	
	/**
	 * True if the collection contains an Agent with a certain identifier.
	 * 
	 * @param id the identifier of an agent
	 * @return true or false
	 */
	public boolean containsKey(String id);
	
	/**
	 * Gets the view of a certain agent, if it is present in the collection.
	 * 
	 * @param id the identifier of the agent
	 * @return the view
	 */
	public AgentViewable get(String id);
	
	/**
	 * Returns the types of agents in this collection.
	 * 
	 * @return a collection with the types
	 */
	public List<String> getTypes();
	
	/**
	 * Returns the number of agents in this collection
	 * 
	 * @return the number
	 */
	public int getSize();
	
	/**
	 * Returns the ids of agents in this collection
	 * 
	 * @return the ids
	 */
	public List<String> getIDs();
}