package model.agent.collection;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.agents.MenuAgent;
import model.agent.agents.NotifyAgent;
import model.agent.agents.SearchAgent;
import model.agent.agents.StatsAgent;
import model.agent.agents.index.MobileIndexAgent;
import model.agent.agents.index.NormalIndexAgent;
import model.agent.property.Property;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * This class is used to create new agents.
 * 
 * @author Gerben G. Meyer
 */
public abstract class AgentFactory {

	/**
	 * Creates a new agent based on its unique identifier.
	 * 
	 * @param agentID the identifier of the agent
	 * @return a fresh Agent
	 */
	public Agent createAgent(String agentID) {
		if (agentID.equals("index")) {
			return new NormalIndexAgent(agentID);
		} else if (agentID.equals("mobile")) {
			return new MobileIndexAgent(agentID);
		} else if (agentID.equals("menu")) {
			return new MenuAgent(agentID);
		} else if (agentID.equals("search")) {
			return new SearchAgent(agentID);
		} else if (agentID.equals("stats")) {
			return new StatsAgent(agentID);
		} else if (agentID.equals("notifier")) {
			return new NotifyAgent(agentID);
		} else {
			return createSpecificAgent(agentID);
		}
	}

	/**
	 * This method should be implemented by every application specific AgentFactory.
	 * Called by {@link #createAgent(String)} when not one of the default agents is being created.
	 * 
	 * @param agentID the identifier of the agent
	 * @return a fresh Agent
	 */
	protected abstract Agent createSpecificAgent(String agentID);
	

	/**
	 * Converts the xml representation of an Agent to an Agent instance, without recording its history.
	 * 
	 * @param xml the representation
	 * @return the Agent
	 */
	public Agent fromXML(String xml) {
		return fromXML(xml, false);
	}

	/**
	 * Converts the xml representation of an Agent to an Agent instance.
	 * 
	 * @param xml the xml representation
	 * @param recordHistory true if the history of this Agent should be recorded
	 * @return the Agent
	 */
	public Agent fromXML(String xml, boolean recordHistory) {
		xml = XMLTool.removeRootTag(xml);
		KeyDataVector propertiesXML = XMLTool.XMLToProperties(xml);
		
		HashMap<String, Property> properties = new HashMap<String, Property>();
		for (KeyData k : propertiesXML) {
			Property p = Property.fromXML(k.getValue());
			properties.put(p.getName(), p);
		}
		
		Agent agent = null;
		if (properties.containsKey(Agent.ID)) {
			agent = createAgent(properties.get(Agent.ID).toString());
			if (recordHistory){
				agent.recordHistory();
			}
			agent.putProperties(properties);
		}
		return agent;
	}
}