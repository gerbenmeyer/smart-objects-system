package model.agent.collection;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import main.SOSServer;
import model.agent.Agent;
import model.agent.property.Property;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * This class is used to create new agents.
 * 
 * @author Gerben G. Meyer
 */
public class AgentFactory {
	
	/**
	 * The instance.
	 */
	private static AgentFactory instance = null;

	/**
	 * Constructs a new AgentFactory object.
	 */
	public AgentFactory() {
		super();
	}
	
	public static synchronized AgentFactory getInstance() {
		if (instance == null){
			instance = new AgentFactory();
		}
		return instance;
	}

	/**
	 * Creates a new agent based on its unique properties.
	 * 
	 * @param properties
	 *            the properties of the agent
	 * @return a fresh Agent
	 */
	public Agent createAgent(Map<String, Property> properties) {

		String id = properties.get(Agent.ID).toString();
		String className = properties.get(Agent.CLASS).toString();

		Agent a = null;

		if (!id.isEmpty() && !className.isEmpty()) {
			try {
				@SuppressWarnings("rawtypes")
				Class theClass = Class.forName(className);
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Constructor con = theClass.getConstructor(new Class[] { String.class });
				a = (Agent) con.newInstance(new Object[] { id });
			} catch (Exception e) {
				SOSServer.getDevLogger().severe("Unable to instantiate agent: '" + e.toString() + "'");
				e.printStackTrace();
			}

		}

		if (a != null) {
			a.setReadBuffer(properties);
		}
		return a;

	}

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