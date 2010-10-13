package data.index;

import java.util.Set;
import java.util.Vector;

import model.agent.AgentViewable;

/**
 * This class must be extended by searchable agents.
 * Only one AgentIndex instance can exist per application.
 * 
 * @author Gerben G. Meyer
 */
public abstract class AgentIndex {
	
	/**
	 * The instance.
	 */
	private static AgentIndex instance;

	/**
	 * Get the instance of AgentIndex for this application.
	 * 
	 * @return the instance
	 */
	public static AgentIndex getInstance(){
		return instance;
	}

	/**
	 * Sets the instance of AgentIndex for this application.
	 */
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
	@Deprecated
	public abstract Set<String> getKeywords();

	/**
	 * Updates the agentIndex if necessary.
	 * 
	 * @param agent the agent to update
	 */
	@Deprecated
	public abstract void update(AgentViewable agent);
}