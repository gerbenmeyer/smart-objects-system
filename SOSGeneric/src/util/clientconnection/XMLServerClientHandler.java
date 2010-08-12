package util.clientconnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

import main.SOSServer;
import main.Settings;
import model.agent.AgentView;
import model.agent.property.PropertiesObject;
import model.agent.property.properties.LocationProperty;
import model.agent.utility.Relation;
import model.agent.utility.RelationCollection;
import util.enums.AgentStatus;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * XMLServerClientHandler handles the XML requests sent to the server by a client.
 * 
 * @author Gerben G. Meyer
 */
public class XMLServerClientHandler extends Thread {

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
	public XMLServerClientHandler(SOSServer server, Socket clientSocket) {
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
					XMLServerCommand cmd = XMLServerCommand.fromXML(XMLTool.removeRootTag(msg));
					String result = XMLTool.addRootTag(executeCommand(cmd), "Result");
					pw.println(result);
					yield();
					msg = br.readLine();
				}
			} else {
				System.out.println("XML user \"" + clientUsername + "\" provided wrong password");
			}
			// System.out.println("XML user \""+clientUsername+"\" has disconnected");

		} catch (Exception e) {
			System.out.println("XML user \"" + clientUsername + "\" has disconnected abnormally");

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
	private String executeCommand(XMLServerCommand cmd) {
		try {
			if (cmd.getName().equals(XMLServerCommand.GET_AGENT)) {
				return getAgent(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.PUT_AGENT)) {
				return putAgent(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.GET_AGENT_IDS)) {
				return getAgentIDs(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.GET_LOCATION_INFO)) {
				return getLocation(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.PUT_LOCATION_INFO)) {
				return putLocation(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.GET_LOCATION_COLLECTION)) {
				return getLocationCollection(cmd.getParameter());
			}
			if (cmd.getName().equals(XMLServerCommand.ADD_TRAINING_INSTANCE)) {
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
		AgentView av = server.getAgentCollection().get(id);
		if (av == null) {
			return "error";
		} else {
			return av.toXML();
		}
	}

	/**
	 * Add an agent to the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String putAgent(String xml) {
		try {
			PropertiesObject o = PropertiesObject.fromXML(xml);
			server.getAgentCollection().putAgentFromPropertiesObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "done";
	}

	/**
	 * Get the identifiers of all agents on the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getAgentIDs(String xml) {
		Vector<String> codes = server.getAgentCollection().getIndex().getAgentIDs();
		String[] sortedCodes = codes.toArray(new String[codes.size()]);
		Arrays.sort(sortedCodes);

		KeyDataVector properties = new KeyDataVector();
		for (String code : sortedCodes) {
			properties.add(new KeyData("Code", "" + code));
		}

		return XMLTool.PropertiesToXML(properties);
	}

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
	 * Adds a location to the server.
	 *
	 * @param xml the XML request
	 * @return the result
	 */
	private String putLocation(String xml) {
		LocationProperty li = (LocationProperty) LocationProperty.fromXML(xml);
		server.getLocations().putLocationInfo(li);
		return "done";
	}

	/**
	 * Get the collection of locations on the server.
	 * 
	 * @param xml the XML request
	 * @return the result
	 */
	private String getLocationCollection(String xml) {
		String path = Settings.getProperty(Settings.LOCATIONS_DATA_DIR) + "locationdata.xml";
		return XMLTool.xmlFromFile(path);
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
		AgentView av = server.getAgentCollection().get(agentCode);
		if (av != null) {
			Relation r = RelationCollection.getInstance().getRelation(av.getType(), av.getArffAttributesString());
			r.addInstance(av.getArffInstanceString(), status);
		} else {
			return "error";
		}
		return "done";
	}
}