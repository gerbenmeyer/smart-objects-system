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
 * 
 * @author Gerben G. Meyer
 * 
 */
public class RemoteAgentCollection implements AgentCollectionView {

	private Map<String, AgentView> agents = Collections.synchronizedMap(new HashMap<String, AgentView>());

	private XMLServerConnection connection;
	
	private AgentIndex index;

	/**
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param username
	 * @param password
	 */
	public RemoteAgentCollection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.connection = new XMLServerConnection(serverAddress, serverPort, username, password);
	}

	/**
 * 
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
	 * 
	 * @param agentCode
	 * @param utility
	 */
	public void addTrainingInstance(String agentCode, AgentStatus status) {
		connection.connect();

		KeyDataVector properties = new KeyDataVector();

		properties.add(new KeyData("AgentCode", "" + agentCode));
		properties.add(new KeyData("Status", "" + status));

		connection
				.sendCommandToServer(new XMLServerCommand(XMLServerCommand.ADD_TRAINING_INSTANCE, XMLTool.PropertiesToXML(properties)));

		connection.disconnect();
	}

	/**
	 * 
	 * @param o
	 */
	public void sendObjectToServer(PropertiesObject o) {
		connection.connect();
		connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_AGENT, o.toXML()));
		connection.disconnect();
	}

	/**
	 * 
	 * @param world
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
