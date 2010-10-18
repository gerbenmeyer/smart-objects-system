package util.clientconnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.agents.EmptyAgent;
import model.agent.collection.AgentCollectionMutable;
import model.agent.property.Property;
import util.enums.AgentStatus;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * The RemoteAgentCollection is used for remote Agent retrieval and putting.
 * This is an abstraction layer between a client and the server.
 * 
 * @author Gerben G. Meyer
 */
public class RemoteAgentCollection implements AgentCollectionMutable {

//	private Map<String, AgentViewable> agents = Collections.synchronizedMap(new HashMap<String, AgentViewable>());
	private XMLServerConnection connection;

	/**
	 * Constructs a new RemoteAgentCollection instance for a certain server.
	 * 
	 * @param serverAddress the server address
	 * @param serverPort the server port
	 * @param username a valid username
	 * @param password the password for the user
	 */
	public RemoteAgentCollection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.connection = new XMLServerConnection(serverAddress, serverPort, username, password);
	}

	@Override
	public List<String> getIDs() {
		connection.connect();
		Vector<String> ids = new Vector<String>();
		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT_IDS, ""));
		if (result != "error" && result != "unknown") {
			KeyDataVector prop = XMLTool.XMLToProperties(result);
			for (KeyData item : prop) {
				if (item.getTag().equals(Agent.ID)) {
					String id = XMLTool.removeRootTag(item.getValue());
					ids.add(id);
				} else {
					System.err.println("Unknown data in xml: " + item.getTag());
				}
			}
		}
		connection.disconnect();
		return ids;
	}

	/**
	 * Trains an Agent with a certain status.
	 * 
	 * @param agentCode the agent's identifier
	 * @param status the status
	 */
	public void addTrainingInstance(String agentCode, AgentStatus status) {
		connection.connect();

		KeyDataVector properties = new KeyDataVector();

		properties.add(new KeyData("AgentCode", "" + agentCode));
		properties.add(new KeyData("Status", "" + status));

		connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.ADD_TRAINING_INSTANCE, XMLTool.PropertiesToXML(properties)));

		connection.disconnect();
	}

	/**
	 * Sends an Agent to the server.
	 * 
	 * @param agent the agent
	 */
	@Override
	public void put(Agent agent) {
		connection.connect();
		connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, agent.toXML()));
		connection.disconnect();
	}

	/**
	 * Sends a collection of Agents to the server.
	 * 
	 * @param agents the agents
	 */
	public void put(Collection<Agent> agents) {
		connection.connect();
		for (Agent o : agents) {
			connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, o.toXML()));
		}
		connection.disconnect();

	}

	@Override
	public boolean containsKey(String id) {
		return get(id) != null;
	}

	@Override
	public AgentViewable get(String id) {
		connection.connect();
		Agent agent = null;
		String objectResult = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT, id));
		if (objectResult != "error" && objectResult != "unknown") {
			agent = fromXML(objectResult);
		}
		connection.disconnect();	
		return agent;
	}

	@Override
	public int getSize() {
		return getIDs().size();
	}

	@Override
	public List<String> getTypes() {
		connection.connect();
		Vector<String> types = new Vector<String>();
		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT_TYPES, ""));
		if (result != "error" && result != "unknown") {
			KeyDataVector prop = XMLTool.XMLToProperties(result);
			for (KeyData item : prop) {
				if (item.getTag().equals(Agent.TYPE)) {
					String type = XMLTool.removeRootTag(item.getValue());
					types.add(type);
				} else {
					System.err.println("Unknown data in xml: " + item.getTag());
				}
			}
		}
		connection.disconnect();
		return types;
	}

	/**
	 * Converts the xml representation of an Agent to an Agent instance.
	 * 
	 * @param xml the xml representation
	 * @return the Agent
	 */
	private Agent fromXML(String xml) {
		xml = XMLTool.removeRootTag(xml);
		KeyDataVector propertiesXML = XMLTool.XMLToProperties(xml);
		
		HashMap<String, Property> properties = new HashMap<String, Property>();
		for (KeyData k : propertiesXML) {
			Property p = Property.fromXML(k.getValue());
			properties.put(p.getName(), p);
		}
		
		Agent agent = null;
		if (properties.containsKey(Agent.ID)) {
			agent = new EmptyAgent(properties.get(Agent.ID).toString());
			agent.putProperties(properties);
		}
		return agent;
	}

	@Override
	public List<AgentViewable> get(List<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AgentViewable> searchAgents(String search) {
		// TODO Auto-generated method stub
		return null;
	}

}