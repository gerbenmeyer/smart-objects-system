package util.clientconnection;

import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import model.locations.LocationCollection;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * The RemoteLocationCollection is used for remote location retrieval and putting.
 * This is an abstraction layer between a client and the server.
 * 
 * @author Gerben G. Meyer
 */
public class RemoteLocationCollection extends LocationCollection {

	private static final long serialVersionUID = -2317679702202977997L;
	private XMLServerConnection connection;

	/**
	 * Constructs a new RemoteLocationCollection instance for a certain server.
	 * 
	 * @param serverAddress the server address
	 * @param serverPort the server port
	 * @param username a valid username
	 * @param password the password for the user
	 */
	public RemoteLocationCollection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.connection = new XMLServerConnection(serverAddress, serverPort, username, password);
	}

	/**
	 * Synchronizes this collection with the one on the server.
	 */
	public void synchronizeWithServer() {
		connection.connect();
		clear();
		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_LOCATION_COLLECTION, ""));
		if (result != "error" && result != "unknown") {
			handleXMLLocationCollection(result);
		}
		connection.disconnect();
	}

	@Override
	public synchronized LocationProperty getLocation(String address) {
		String normAddress = Property.normalize(address);
		LocationProperty result = this.get(normAddress);
		if (result == null) { // street not known
			result = locationLookup(normAddress);

			if (result != null) {
				putLocationInfo(result);
			}
		}
		return result;
	}
	
	@Override
	public synchronized void putLocationInfo(LocationProperty location){
		// store result
		if (!this.containsKey(location.getAddress())){
			// don't store address name
			location.setAddressName("");
			// new result, so add it to an XML file
			this.put(location.getAddress(), location);
			connection.connect();
			connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.PUT_LOCATION_INFO, location.toXML()));
			connection.disconnect();
		}

	}

	private LocationProperty locationLookup(String address) {
		KeyDataVector parameters = new KeyDataVector();
		parameters.add(new KeyData("Address", address));
		connection.connect();
		String locationResult = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_LOCATION_INFO, XMLTool
				.PropertiesToXML(parameters)));
		connection.disconnect();
		return (LocationProperty) LocationProperty.fromXML(locationResult);
	}
}