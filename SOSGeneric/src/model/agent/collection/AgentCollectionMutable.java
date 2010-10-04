package model.agent.collection;

import model.agent.Agent;

public interface AgentCollectionMutable extends AgentCollectionViewable {
	
	/**
	 * Adds an agent to the collection.
	 * 
	 * @param agent the agent to be added to the collection
	 */
	public void put(Agent agent);

}