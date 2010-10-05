package util.clientconnection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollectionViewable;
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
public class RemoteAgentCollection implements AgentCollectionViewable {

	private Map<String, AgentViewable> agents = Collections.synchronizedMap(new HashMap<String, AgentViewable>());
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

//	/**
//	 * Synchronizes this collection with the one on the server.
//	 */
//	public void synchronizeWithServer() {
//		connection.connect();
//
//		// objects
//		agents.clear();
//		
//		index = new AgentIndexMemory(this);
//
//		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT_IDS, ""));
//
//		if (result != "error" && result != "unknown") {
//
//			KeyDataVector prop = XMLTool.XMLToProperties(result);
//
//			for (KeyData item : prop) {
//
//				if (item.getTag().equals("Code")) {
//					String code = XMLTool.removeRootTag(item.getValue());
//
//					String objectResult = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT, code));
//					if (objectResult != "error" && objectResult != "unknown") {
//						Agent a = EmptyAgent.fromXML(objectResult);
////						a.putProperties(MemoryPropertiesObject.fromXML(objectResult),this,a);
//						agents.put(code, a);
//						index.update(a);
//					}
//
//				} else {
//					System.err.println("Unknown data in xml: " + item.getTag());
//				}
//			}
//		}
//		connection.disconnect();
//	}

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
	 * @param a the agent
	 */
	public void sendObjectToServer(Agent a) {
		connection.connect();
		connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, a.toXML()));
		connection.disconnect();
	}

	/**
	 * Sends a collection of Agents to the server.
	 * 
	 * @param agents the agents
	 */
	public void sendObjectsToServer(Collection<Agent> agents) {
		connection.connect();
		for (Agent o : agents) {
			connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, o.toXML()));
		}
		connection.disconnect();

	}

	@Override
	public boolean containsKey(String id) {
		return agents.containsKey(id);
	}

	@Override
	public AgentViewable get(String id) {
		return agents.get(id);
	}

	@Override
	public int getSize() {
		return agents.size();
	}

	@Override
	public List<String> getTypes() {
		Vector<String> vec = new Vector<String>();
		for (AgentViewable agent : agents.values()) {
			String agentType = agent.get(Agent.TYPE);
			if (!vec.contains(agentType)) {
				vec.add(agentType);
			}
		}
		return vec;
	}

	@Override
	public List<String> getIDs() {
		return new Vector<String>(agents.keySet());
	}

}