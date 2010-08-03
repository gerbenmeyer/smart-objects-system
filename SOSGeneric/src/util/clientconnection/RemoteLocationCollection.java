package util.clientconnection;

import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import model.locations.LocationCollection;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class RemoteLocationCollection extends LocationCollection {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2317679702202977997L;
	private XMLServerConnection connection;

	/**
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param username
	 * @param password
	 */
	public RemoteLocationCollection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.connection = new XMLServerConnection(serverAddress, serverPort, username, password);
	}

	public void synchronizeWithServer() {
		connection.connect();

		// objects
		clear();

		String result = connection.sendCommandToServer(new XMLServerCommand(XMLServerCommand.GET_LOCATION_COLLECTION, ""));

		if (result != "error" && result != "unknown") {
			handleXMLLocationCollection(result);
		}

		connection.disconnect();
	}

	@Override
	public synchronized LocationProperty getLocationInfo(String address) {
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
