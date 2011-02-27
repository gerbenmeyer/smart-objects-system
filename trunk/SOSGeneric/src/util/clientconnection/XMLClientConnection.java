package util.clientconnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import main.SOSServer;
import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.classification.Classifier;
import model.agent.classification.ClassifierCollection;
import model.agent.collection.AgentCollection;
import model.agent.property.properties.LocationProperty;
import util.enums.AgentStatus;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * XMLServerClientHandler handles the XML requests sent to the server by a client.
 * 
 * @author Gerben G. Meyer
 */
public class XMLClientConnection extends Thread {

	private Socket clientSocket = null;
	private PrintWriter pw = null;
	private BufferedReader br = null;
	private SOSServer server;

	/**
	 * Constructs a new XMLServerClientHandler instance for a server and a socket.
	 * 
	 * @param server the server
	 * @param clientSocket the socket
	 */
	public XMLClientConnection(SOSServer server, Socket clientSocket) {
		super();
		this.server = server;
		this.clientSocket = clientSocket;

		super.start();
	}

	/**
	 * Start handling the requests from the socket.
	 */
	public void run() {
		String clientUsername = "unknown";
		try {
			pw = new PrintWriter(clientSocket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			clientUsername = br.readLine();
			// System.out.println("XML user \""+clientUsername+"\" has connected");
			String clientPassword = br.readLine();

			if (clientPassword.equals(Settings.getProperty(Settings.XML_SERVER_PASSWORD))) {
				String msg = br.readLine();
				while (msg != null) {
					XMLCommand cmd = XMLCommand.fromXML(XMLTool.removeRootTag(msg));
					String result = XMLTool.addRootTag(executeCommand(cmd), "Result");
					pw.println(result);
					yield();
					msg = br.readLine();
				}
			} else {
				SOSServer.getDevLogger().severe("XML user \"" + clientUsername + "\" provided wrong password");
			}
			// System.out.println("XML user \""+clientUsername+"\" has disconnected");

		} catch (Exception e) {
			SOSServer.getDevLogger().severe("XML user \"" + clientUsername + "\" has disconnected abnormally: '"+e.toString()+"'");

		} finally {
			try {
				pw.close();
			} catch (Exception e1) {
			}
			try {
				br.close();
			} catch (Exception e2) {
			}
			try {
				clientSocket.close();
			} catch (Exception e3) {
			}
		}
	}

	/**
	 * Execute a requested command on the server.
	 * 
	 * @param cmd the command
	 * @return the result
	 */
	private String executeCommand(XMLCommand cmd) {
		try {
			if (cmd.getName().equals(XMLCommand.GET_AGENT)) {
				return getAgent(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLCommand.PUT_AGENT)) {
				return putAgent(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLCommand.GET_AGENT_IDS)) {
				return getAgentIDs(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLCommand.GET_AGENT_TYPES)) {
				return getAgentTypes(cmd.getParameter());
			}			
			if (cmd.getName().equals(XMLCommand.GET_LOCATION_INFO)) {
				return getLocation(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLCommand.GET_LOCATION_COLLECTION)) {
				return getLocationCollection();
			}
			if (cmd.getName().equals(XMLCommand.ADD_TRAINING_INSTANCE)) {
				return addTrainingInstance(cmd.getParameter());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}

		return "unknown";
	}

	// --------------------------------------------------------------------------------
	// IMPLEMENTATION OF COMMANDS
	// --------------------------------------------------------------------------------

	/**
	 * Get an agent from the server and convert it to XML.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getAgent(String xml) {
		String id = xml;
		AgentViewable av = AgentCollection.getInstance().get(id);
		if (av == null) {
			return "error";
		} else {
			return av.toXML();
		}
	}
	
	/**
	 * Get all agent IDs from the server and convert it to XML.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getAgentIDs(String xml) {
		List<String> ids = AgentCollection.getInstance().getIDs();
		Collections.sort(ids);

		KeyDataVector properties = new KeyDataVector();
		for (String id : ids) {
			properties.add(new KeyData(Agent.ID, id));
		}

		return XMLTool.PropertiesToXML(properties);
	}	

	
	/**
	 * Get all agent types from the server and convert it to XML.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getAgentTypes(String xml) {
		List<String> types = AgentCollection.getInstance().getTypes();
		Collections.sort(types);

		KeyDataVector properties = new KeyDataVector();
		for (String type : types) {
			properties.add(new KeyData(Agent.TYPE, type));
		}

		return XMLTool.PropertiesToXML(properties);
	}	
	
	/**
	 * Add an agent to the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String putAgent(String xml) {
		try {
			Agent agent = server.getFactory().fromXML(xml, true);
			if (agent != null) {
				server.getAgentCollection().put(agent);
			} else {
				System.out.println("Agent is null!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "done";
	}

//	/**
//	 * Get the identifiers of all agents on the server.
//	 * 
//	 * @param xml the XML request
//	 * @return the result
//	 */
//	private String getAgentIDs(String xml) {
//		List<String> codes = server.getAgentCollection().getIDs();
//		String[] sortedCodes = codes.toArray(new String[codes.size()]);
//		Arrays.sort(sortedCodes);
//
//		KeyDataVector properties = new KeyDataVector();
//		for (String code : sortedCodes) {
//			properties.add(new KeyData("Code", "" + code));
//		}
//
//		return XMLTool.PropertiesToXML(properties);
//	}

	/**
	 * Get the location of an address from the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getLocation(String xml) {
		String address = xml;
		LocationProperty li = server.getLocations().getLocation(address);
		if (li == null) {
			return "error";
		} else {
			return li.toXML();
		}
	}

	/**
	 * Get the collection of locations on the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getLocationCollection() {
		Collection<LocationProperty> locations = server.getLocations().getLocations();
		StringBuffer xml = new StringBuffer();
		for (LocationProperty location : locations) {
			xml.append(location.toXML());
		}
		return XMLTool.addRootTag(xml.toString(), "LocationCollection");
	}

	/**
	 * Train the status of an Agent on the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String addTrainingInstance(String xml) {
		String agentCode = "";
		AgentStatus status = AgentStatus.UNKNOWN;

		KeyDataVector prop = XMLTool.XMLToProperties(xml);

		for (KeyData item : prop) {
			if (item.getTag().equals("AgentCode")) {
				agentCode = XMLTool.removeRootTag(item.getValue());
			} else if (item.getTag().equals("Status")) {
				status = AgentStatus.valueOf(XMLTool.removeRootTag(item.getValue()));
			}
		}
		AgentViewable av = AgentCollection.getInstance().get(agentCode);
		if (av != null) {
			Classifier r = ClassifierCollection.getInstance().get(av.get(Agent.TYPE), av.getArffAttributesString());
			r.addTrainingInstance(av.getArffInstanceString(), status);
		} else {
			return "error";
		}
		return "done";
	}
}