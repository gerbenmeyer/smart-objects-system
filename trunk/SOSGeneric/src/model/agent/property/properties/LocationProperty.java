package model.agent.property.properties;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.property.Property;
import util.Capitalize;
import util.enums.GoogleLocationType;
import util.enums.PropertyType;

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
		if (str.isEmpty()){
			return;
		}
		String[] split = str.split(seperator);
		this.addressName = Capitalize.capitalizeLine(split[0].trim());
		this.address = split[1].trim();
		this.type = GoogleLocationType.valueOf(split[2].trim());
		this.latitude = Double.parseDouble(split[3].trim());
		this.longitude = Double.parseDouble(split[4].trim());
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
		double threshold = 5.0;

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
		return "location.png";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String getArffData(AgentViewable av) {
		double distance = 0.0;

		try {
			LocationProperty lp = new LocationProperty("", av.get(Agent.LOCATION));
			distance = this.distanceTo(lp);
		} catch (Exception e) {
		}
		return "" + distance;
	}

	/**
	 * Get the LocationProperty of a location.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return the LocationProperty
	 */
	public static LocationProperty getCoordinate(double latitude, double longitude) {
		LocationProperty lp = new LocationProperty("");
		lp.setLocationType(GoogleLocationType.ROOFTOP);
		lp.setHidden(true);
		lp.setLatitude(latitude);
		lp.setLongitude(longitude);
		return lp;
	}
}