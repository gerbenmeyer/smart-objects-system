package model.agent.index;

import java.util.Set;
import java.util.Vector;

/**
 * This interface must be implemented by searchable agents. 
 * 
 * @author G.G. Meyer
 */
public interface AgentIndexView {
	/**
	 * Searches the agents for a string.
	 * 
	 * @param search string to search for
	 * @return a Vector with the identifiers of agents which contain the search string 
	 */
	public Vector<String> searchAgents(String search);
	/**
	 * All keywords in this AgentIndex
	 * 
	 * @return a Set with the keywords
	 */
	public Set<String> getKeywords();
	/**
	 * Get all agent identifiers in this AgentIndex.
	 * 
	 * @return the agent identifiers
	 */
	public Vector<String> getAgentIDs();
	/**
	 * Get all agent types in this AgentIndex.
	 * 
	 * @return the agent types
	 */
	public Vector<String> getAgentTypes();
}