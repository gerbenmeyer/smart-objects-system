package util.clientconnection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import model.agent.AgentView;
import model.agent.agents.EmptyAgent;
import model.agent.collection.AgentCollectionView;
import model.agent.index.AgentIndex;
import model.agent.index.AgentIndexView;
import model.agent.property.PropertiesObject;
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
public class RemoteAgentCollection implements AgentCollectionView {

	private Map<String, AgentView> agents = Collections.synchronizedMap(new HashMap<String, AgentView>());
	private XMLServerConnection connection;
	private AgentIndex index;

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

	/**
	 * Synchronizes this collection with the one on the server.
	 */
	public void synchronizeWithServer() {
		connection.connect();

		// objects
		agents.clear();
		
		index = new AgentIndex(this);

		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT_IDS, ""));

		if (result != "error" && result != "unknown") {

			KeyDataVector prop = XMLTool.XMLToProperties(result);

			for (KeyData item : prop) {

				if (item.getTag().equals("Code")) {
					String code = XMLTool.removeRootTag(item.getValue());

					String objectResult = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_AGENT, code));
					if (objectResult != "error" && objectResult != "unknown") {
						EmptyAgent a = new EmptyAgent("",this);
						a.putProperties(PropertiesObject.fromXML(objectResult),this,a);
						agents.put(code, a);
						index.update(a);
					}

				} else {
					System.err.println("Unknown data in xml: " + item.getTag());
				}
			}
		}
		connection.disconnect();
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
	 * Sends a PropertiesObject to the server.
	 * 
	 * @param o the object
	 */
	public void sendObjectToServer(PropertiesObject o) {
		connection.connect();
		connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, o.toXML()));
		connection.disconnect();
	}

	/**
	 * Sends a collection of PropertiesObjects to the server.
	 * 
	 * @param objects the objects
	 */
	public void sendObjectsToServer(Collection<PropertiesObject> objects) {
		connection.connect();
		for (PropertiesObject o : objects) {
			connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, o.toXML()));
		}
		connection.disconnect();

	}

	@Override
	public boolean containsKey(String id) {
		return agents.containsKey(id);
	}

	@Override
	public AgentView get(String id) {
		return agents.get(id);
	}

	@Override
	public AgentIndexView getIndex() {
		return index;
	}
}