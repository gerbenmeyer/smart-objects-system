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
	private boolean hasSynced = false;

	/**
	 * Constructs a new RemoteLocationCollection instance to a certain server.
	 * 
	 * @param connection the server connection
	 */
	public RemoteLocationCollection(XMLServerConnection connection) {
		super();
		this.connection = connection;
	}
	
	public void connect(){
		this.connection.connect();
	}
	
	public void disconnect(){
		this.connection.disconnect();
	}

	/**
	 * Get all locations from the server and buffer them.
	 */
	private void syncLocations() {
		if (hasSynced){
			return;
		}
		connect();
		locationsBuffer.clear();
		Collection<LocationProperty> locations = getLocations();
		for (LocationProperty location : locations) {
			String address = Property.normalize(location.getAddress());
			locationsBuffer.put(address, location);
		}
		disconnect();
		this.hasSynced = true;
	}

	@Override
	public synchronized LocationProperty getLocation(String address) {
		syncLocations();
		String normAddress = Property.normalize(address);
		LocationProperty location = locationsBuffer.get(normAddress);
		if (location == null) {
			KeyDataVector parameters = new KeyDataVector();
			parameters.add(new KeyData("Address", normAddress));
			String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_LOCATION_INFO, XMLTool
					.PropertiesToXML(parameters)));
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
		String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_LOCATION_COLLECTION, ""));
		if (!result.equals("error") && !result.equals("unknown")) {
			KeyDataVector prop = XMLTool.XMLToProperties(XMLTool.removeRootTag(result));
			for (KeyData item : prop) {
				locations.add((LocationProperty) LocationProperty.fromXML(item.getValue()));
			}
		}
		return locations;
	}

	@Override
	public String getCountry(LocationProperty lp) {
		String country = null;
		if (lp != null && !lp.isNull()) {
			String result = connection.sendCommandToServer(new XMLCommand(XMLCommand.GET_LOCATION_COUNTRY, lp.toXML()));
			if (!result.equals("error") && !result.equals("unknown")) {
				country = result;
			}
		}
		return country;
	}

	@Override
	public String getCountry(double latitude, double longitude) {
		LocationProperty lp = new LocationProperty("");
		lp.setLatitude(latitude);
		lp.setLongitude(longitude);
		return getCountry(lp);
	}
}