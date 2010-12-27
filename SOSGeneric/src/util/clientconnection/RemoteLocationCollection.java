package util.clientconnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import model.locations.LocationCollectionViewable;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * The RemoteLocationCollection is used for remote location retrieval and putting.
 * This is an abstraction layer between a client and the server.
 * 
 * @author Gerben G. Meyer
 */
public class RemoteLocationCollection implements LocationCollectionViewable {

	private XMLServerConnection connection;
	private HashMap<String, LocationProperty> locationsBuffer = new HashMap<String, LocationProperty>();

	/**
	 * Constructs a new RemoteLocationCollection instance to a certain server.
	 * 
	 * @param serverAddress the server address
	 * @param serverPort the server port
	 * @param username a valid username
	 * @param password the password for the user
	 */
	public RemoteLocationCollection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.connection = new XMLServerConnection(serverAddress, serverPort, username, password);
		syncLocations();
	}

	/**
	 * Get all locations from the server and buffer them.
	 */
	private void syncLocations() {
		locationsBuffer.clear();
		Collection<LocationProperty> locations = getLocations();
		for (LocationProperty location : locations) {
			String address = Property.normalize(location.getAddress());
			locationsBuffer.put(address, location);
		}
	}

	@Override
	public synchronized LocationProperty getLocation(String address) {
		String normAddress = Property.normalize(address);
		LocationProperty location = locationsBuffer.get(normAddress);
		if (location == null) {
			KeyDataVector parameters = new KeyDataVector();
			parameters.add(new KeyData("Address", normAddress));
			connection.connect();
			String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_LOCATION_INFO, XMLTool
					.PropertiesToXML(parameters)));
			connection.disconnect();
			if (!result.equals("error")) {
				location = (LocationProperty) LocationProperty.fromXML(result);
				locationsBuffer.put(normAddress, location);
			}
		}
		return location;
	}

	@Override
	public Collection<LocationProperty> getLocations() {
		Collection<LocationProperty> locations = new Vector<LocationProperty>();
		connection.connect();
		String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_LOCATION_COLLECTION, ""));
		connection.disconnect();
		if (result != "error" && result != "unknown") {
			KeyDataVector prop = XMLTool.XMLToProperties(XMLTool.removeRootTag(result));
			for (KeyData item : prop) {
				locations.add((LocationProperty) LocationProperty.fromXML(item.getValue()));
			}
		}
		return locations;
	}
}