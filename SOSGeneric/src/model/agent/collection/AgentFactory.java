package model.agent.collection;

import model.agent.Agent;
import model.agent.property.PropertiesObject;

/**
 * This class is used to create new agents.
 * 
 * @author G.G. Meyer
 *
 */
public abstract class AgentFactory {

	/**
	 * Creates a new agent based on its properties.
	 * 
	 * @param po the PropertiesObject to be used as a template for the agent
	 * @param acv an AgentCollectionView
	 * @return a fresh Agent
	 */
	public abstract Agent createAgent(PropertiesObject po, AgentCollectionView acv);

}