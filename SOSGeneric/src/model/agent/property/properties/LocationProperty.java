package model.agent.property.properties;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.property.Property;
import util.Capitalize;
import util.enums.AgentStatus;
import util.enums.GoogleLocationType;
import util.enums.PropertyType;
import util.htmltool.HtmlMapContentGenerator;

/**
 * 
 * A Property implementation representing a geograpic location.
 * 
 */
public class LocationProperty extends Property {

	private String addressName = "";
	private String address = "";

	private GoogleLocationType type = GoogleLocationType.APPROXIMATE;

	private double latitude = 0.0;
	private double longitude = 0.0;

	private String seperator = "-sep-";

	/**
	 * Constructs a named LocationProperty instance.
	 * 
	 * @param name
	 *            the location name
	 */
	public LocationProperty(String name) {
		super(name, PropertyType.LOCATION);
	}

	/**
	 * Constructs a named LocationProperty instance with an initial value.
	 * 
	 * @param name
	 *            the location name
	 * @param value
	 *            the value, as defined by {@link #parseHint()} and outputted by
	 *            {@link #toString()}
	 */
	public LocationProperty(String name, String value) {
		this(name);
		this.parseString(value);
	}

	/**
	 * Gets the address name of this location.
	 * 
	 * @return the name
	 */
	public String getAddressName() {
		return addressName;
	}

	/**
	 * Sets the address name of this location.
	 * 
	 * @param addressName
	 *            the name
	 */
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	/**
	 * Gets the address of this location.
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address of this location.
	 * 
	 * @param address
	 *            the address
	 */
	public void setAddress(String address) {
		this.address = Property.normalize(address);
	}

	/**
	 * Gets the location type.
	 * 
	 * @return the type
	 */
	public GoogleLocationType getLocationType() {
		return type;
	}

	/**
	 * Sets the type of this location.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setLocationType(GoogleLocationType type) {
		this.type = type;
	}

	/**
	 * Gets the longitude of this location.
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude of this location.
	 * 
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the latitude of this location.
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude of this location.
	 * 
	 * @param latitude
	 *            the longitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return this.addressName + seperator + this.address + seperator + this.getLocationType().toString() + seperator
				+ this.latitude + seperator + this.longitude;
	}

	@Override
	public String toInformativeString() {
		String result = "";
		String[] split = this.address.split(",");
		if (split.length >= 3) {
			result = ", " + Capitalize.capitalizeLine(split[1].trim());
		}
		return this.addressName + result;
	}

	@Override
	public void parseString(String str) {
		String[] split = str.split(seperator);
		if (split.length >= 5) {
			this.addressName = Capitalize.capitalizeLine(split[0].trim());
			this.address = split[1].trim();
			this.type = GoogleLocationType.valueOf(split[2].trim());
			this.latitude = Double.parseDouble(split[3].trim());
			this.longitude = Double.parseDouble(split[4].trim());
		}
	}

	public static String parseHint() {
		return "name;address;type;latitude;longitude";
	}

	/**
	 * Returns true if the location is not set.
	 * 
	 * @return true or false
	 */
	public boolean isNull() {
		return latitude == 0.0 && longitude == 0.0;
	}

	/**
	 * Returns true if this location is near another location. The threshold is
	 * set to 3 km.
	 * 
	 * @param lp
	 *            the other location
	 * @return true or false
	 */
	public boolean nearby(LocationProperty lp) {
		double distance = distanceTo(lp);
		double threshold = 3.0;

		if (this.getLocationType() == GoogleLocationType.APPROXIMATE
				|| lp.getLocationType() == GoogleLocationType.APPROXIMATE) {
			threshold *= 10.0;
		} else if (this.getLocationType() == GoogleLocationType.GEOMETRIC_CENTER
				|| lp.getLocationType() == GoogleLocationType.GEOMETRIC_CENTER) {
			threshold *= 5.0;
		} else if (this.getLocationType() == GoogleLocationType.RANGE_INTERPOLATED
				|| lp.getLocationType() == GoogleLocationType.RANGE_INTERPOLATED) {
			threshold *= 2.0;
		}

		return distance <= threshold;
	}

	/**
	 * Returns the distance from this location to another location.
	 * 
	 * @param lp
	 *            the other location
	 * @return the distance in kilometers
	 */
	public double distanceTo(LocationProperty lp) {
		if (this.isNull() || lp.isNull()) {
			return 0.0;
		}

		double a = this.getLatitude() / 57.29577951;
		double b = this.getLongitude() / 57.29577951;
		double c = lp.getLatitude() / 57.29577951;
		double d = lp.getLongitude() / 57.29577951;

		double distance = 0;

		if (a == c && b == d) {
			distance = 0;
		} else {
			double x = Math.sin(a) * Math.sin(c) + Math.cos(a) * Math.cos(c) * Math.cos(b - d);
			if (x > 1) {
				distance = 6378.0 * Math.acos(1);
			} else {
				distance = 6378.0 * Math.acos(x);
			}
		}
		return distance;
	}

	public String getIcon() {
		String icon = "location.png";
		if (getAgentView() != null) {
			icon = getAgentView().getIcon();
		}
		if (getName().equals("SourceLocation")) {
			icon = "source_icon.png";
		} else if (getName().equals("DestinationLocation")) {
			icon = "destination_icon.png";
		}
		return icon;
	}

	@Override
	public void toScript(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		toScript(mapContent, params, 32, false);
	}

	/**
	 * Returns the LocationProperty specific javascript to be used for map
	 * generation. Set openInfoWindowOnLoad to pop up the infoWindow when the
	 * page loads.
	 * 
	 * @param mapContent
	 *            a content generator for the map
	 * @param params
	 *            the request parameters
	 * @param openInfoWindowOnLoad
	 *            true or false
	 */
	public void toScript(HtmlMapContentGenerator mapContent, HashMap<String, String> params,
			boolean openInfoWindowOnLoad) {
		toScript(mapContent, params, 32, openInfoWindowOnLoad);
	}

	/**
	 * Returns the LocationProperty specific javascript to be used for map
	 * generation. Set mapIconSize to the desired size of the marker. Set
	 * openInfoWindowOnLoad to pop up the infoWindow when the page loads.
	 * 
	 * @param mapContent
	 *            a content generator for the map
	 * @param params
	 *            the request parameters
	 * @param mapIconSize
	 *            the size of the map icon
	 * @param openInfoWindowOnLoad
	 *            true or false
	 */
	public void toScript(HtmlMapContentGenerator mapContent, HashMap<String, String> params, int mapIconSize,
			boolean openInfoWindowOnLoad) {
		if (isNull()) {
			return;
		}
		if (getAgentView() == null) {
			System.err.println("LocationProperty: Unable to create script, agentView is null");
			return;
		}

		String mapIcon = getAgentView().getMapMarkerImage();
		String smallIcon = getAgentView().getIcon();

		if (getName().equals("SourceLocation")) {
			mapIcon = "source.png";
			smallIcon = "source_icon.png";
		}

		if (getName().equals("DestinationLocation")) {
			mapIcon = "destination.png";
			smallIcon = "destination_icon.png";
		}

		AgentStatus status = getAgentView().getStatus();

		int zIndex = -status.getValue();
//		boolean finished = getAgentView().get("Finished").equals(Boolean.toString(true));
//		if (finished && status == AgentStatus.OK) {
//			zIndex = AgentStatus.UNKNOWN.getValue();
//		}
		zIndex++;
		boolean label = !getAgentView().get(Agent.HIDDEN).equals(Boolean.toString(true))
				&& (status == AgentStatus.WARNING || status == AgentStatus.ERROR);

		boolean showDetails = Settings.getProperty(Settings.SHOW_AGENT_DETAILS).equals("true");

		mapContent.addMapMarker(latitude, longitude, getAgentView().get(Agent.LABEL), getAgentView().get(
				Agent.DESCRIPTION), mapIcon, mapIconSize, smallIcon, (showDetails ? getAgentView().getID() : null),
				zIndex, label, openInfoWindowOnLoad, "?" + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "="
						+ getAgentView().getID(), getAgentView().getID());

	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String getArffData() {
		double distance = 0.0;

		try {
			LocationProperty lp = new LocationProperty("", getAgentView().get(Agent.LOCATION));
			distance = this.distanceTo(lp);
		} catch (Exception e) {
		}
		return "" + distance;
	}

	/**
	 * Get the string representation of a location.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return the string representation
	 */
	public static String getCoordinate(double latitude, double longitude) {
		LocationProperty lp = new LocationProperty("");
		lp.setLocationType(GoogleLocationType.ROOFTOP);
		lp.setHidden(true);
		lp.setLatitude(latitude);
		lp.setLongitude(longitude);
		return lp.toString();
	}
}