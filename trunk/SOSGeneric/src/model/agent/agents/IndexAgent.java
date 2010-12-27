package model.agent.agents;

import java.util.HashMap;

import model.agent.Agent;
import util.enums.PropertyType;

/**
 * This abstract agent must be extended by agents which generate an index page.
 * 
 * @author Gerben G., Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 */
public abstract class IndexAgent extends Agent {

	/**
	 * Constructs a new IndexAgent object.
	 * 
	 * @param id the identifier for the agent
	 */
	public IndexAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	/**
	 * Generates a HTML page.
	 * 
	 * @param params the parameters for the request
	 * @return the HTML page
	 */
	public abstract StringBuffer generatePage(HashMap<String, String> params);

}