package data.index;

import java.util.Set;
import java.util.Vector;

import model.agent.AgentViewable;

/**
 * This interface must be implemented by searchable agents. 
 * 
 * @author Gerben G. Meyer
 */
public abstract class AgentIndex {
	
	private static AgentIndex instance;
	
	public static AgentIndex getInstance(){
		return instance;
	}
	
	public static void setInstance(AgentIndex agentIndex){
		instance = agentIndex;
	}
	
	/**
	 * Searches the agents for a string.
	 * 
	 * @param search string to search for
	 * @return a Vector with the identifiers of agents which contain the search string 
	 */
	public abstract Vector<String> searchAgents(String search);
	/**
	 * All keywords in this AgentIndex
	 * 
	 * @return a Set with the keywords
	 */
	public abstract Set<String> getKeywords();
//	/**
//	 * Get all agent identifiers in this AgentIndex.
//	 * 
//	 * @return the agent identifiers
//	 */
//	public abstract Vector<String> getAgentIDs();
//	/**
//	 * Get all agent types in this AgentIndex.
//	 * 
//	 * @return the agent types
//	 */
//	public abstract Vector<String> getAgentTypes();
	/**
	 * Updates the agentIndex if necessary.
	 * 
	 * @param agent the agent to update
	 */
	public abstract void update(AgentViewable agent);
}