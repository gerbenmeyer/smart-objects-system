package model.agent.collection;

import java.util.HashMap;
import java.util.Map;

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
	 * Creates a new agent based on its unique properties.
	 * 
	 * @param properties
	 *            the properties of the agent
	 * @return a fresh Agent
	 */
	public Agent createAgent(Map<String, Property> properties) {

		Agent a = createSpecificAgent(properties);
		if (a == null) {
			String agentID = properties.get(Agent.ID).toString();
			if (agentID.equals("index")) {
				a = new NormalIndexAgent(agentID);
			} else if (agentID.equals("mobile")) {
				a = new MobileIndexAgent(agentID);
			} else if (agentID.equals("menu")) {
				a = new MenuAgent(agentID);
			} else if (agentID.equals("search")) {
				a = new SearchAgent(agentID);
			} else if (agentID.equals("stats")) {
				a = new StatsAgent(agentID);
			} else if (agentID.equals("notifier")) {
				a = new NotifyAgent(agentID);
			}
		}
		if (a != null){
			a.setReadBuffer(properties);
		}
		return a;

	}

	/**
	 * This method should be implemented by every application specific
	 * AgentFactory. Called by {@link #createAgent(String)}. Should return null
	 * when the properties do not refer to an application specific agent.
	 * 
	 * @param properties
	 *            the properties of the agent
	 * @return a fresh Agent or null
	 */
	protected abstract Agent createSpecificAgent(Map<String, Property> properties);

	/**
	 * Converts the xml representation of an Agent to an Agent instance, without
	 * recording its history.
	 * 
	 * @param xml
	 *            the representation
	 * @return the Agent
	 */
	public Agent fromXML(String xml) {
		return fromXML(xml, false);
	}

	/**
	 * Converts the xml representation of an Agent to an Agent instance.
	 * 
	 * @param xml
	 *            the xml representation
	 * @param recordHistory
	 *            true if the history of this Agent should be recorded
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

		Agent agent = createAgent(properties);
		if (recordHistory) {
			agent.recordHistory();
		}
		agent.putProperties(properties);
		return agent;
	}
}