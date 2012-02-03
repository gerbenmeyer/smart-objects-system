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
	 * @param connection the server connection
	 */
	public RemoteAgentCollection(XMLServerConnection connection) {
		super();
		this.connection = connection;
	}
	
	public void connect(){
		this.connection.connect();
	}
	
	public void disconnect(){
		this.connection.disconnect();
	}

	@Override
	public List<String> getIDs() {
		Vector<String> ids = new Vector<String>();
		String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_AGENT_IDS, ""));
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
		return ids;
	}

	/**
	 * Trains an Agent with a certain status.
	 * 
	 * @param agentCode the agent's identifier
	 * @param status the status
	 */
	public void addTrainingInstance(String agentCode, AgentStatus status) {
		KeyDataVector properties = new KeyDataVector();

		properties.add(new KeyData("AgentCode", "" + agentCode));
		properties.add(new KeyData("Status", "" + status));

		connection.sendCommandToServer(new XMLCommand(XMLCommand.ADD_TRAINING_INSTANCE, XMLTool.PropertiesToXML(properties)));
	}

	/**
	 * Sends an Agent to the server.
	 * 
	 * @param agent the agent
	 */
	@Override
	public void put(Agent agent) {
		connection.sendCommandToServer(new XMLCommand(XMLCommand.PUT_AGENT, agent.toXML()));
	}

	/**
	 * Sends a collection of Agents to the server.
	 * 
	 * @param agents the agents
	 */
	public void put(Collection<Agent> agents) {
		for (Agent o : agents) {
			connection.sendCommandToServer(new XMLCommand(XMLCommand.PUT_AGENT, o.toXML()));
		}
	}

	@Override
	public boolean containsKey(String id) {
		return get(id) != null;
	}

	@Override
	public AgentViewable get(String id) {
		Agent agent = null;
		String objectResult = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_AGENT, id));
		if (objectResult != "error" && objectResult != "unknown") {
			agent = fromXML(objectResult);
//		} else {
//			System.err.println("training client: server tells me: "+objectResult+"! : "+id);
		}
		return agent;
	}

	@Override
	public int getSize() {
		return getIDs().size();
	}

	@Override
	public List<String> getTypes() {
		Vector<String> types = new Vector<String>();
		String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_AGENT_TYPES, ""));
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

	@Override
	public void delete(Agent agent) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public List<AgentViewable> searchAgents(String search, String sort, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
}