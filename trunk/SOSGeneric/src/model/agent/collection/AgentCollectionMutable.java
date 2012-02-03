package model.agent.collection;

import model.agent.Agent;

public interface AgentCollectionMutable extends AgentCollectionViewable {
	
	/**
	 * Adds an agent to the collection.
	 * 
	 * @param agent the agent to be added to the collection
	 */
	public void put(Agent agent);

	/**
	 * Deletes an agent from the collection.
	 * 
	 * @param agent the Agent to be removed from the collection
	 */
	@Deprecated
	public void delete(Agent agent);
	
}