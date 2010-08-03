package model.agent.collection;

import model.agent.AgentView;
import model.agent.index.AgentIndexView;

/**
 * An interface to get read access to a collection of agents
 * 
 * @author G.G. Meyer
 *
 */
public interface AgentCollectionView {
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
	public AgentView get(String id);
	/**
	 * Get the AgentIndexView, which is used for searching.
	 * 
	 * @return the AgentIndexView
	 */
	public AgentIndexView getIndex();
}