package model.agent.property.properties;

import java.util.HashMap;

import main.Settings;
import model.agent.property.Property;
import util.enums.AgentStatus;
import util.enums.GoogleLocationType;
import util.enums.PropertyType;
import util.htmltool.HtmlMapContentGenerator;

public class LocationProperty extends Property {

	private String addressName = "";
	private String address = "";

	private GoogleLocationType type = GoogleLocationType.APPROXIMATE;

	private double latitude = 0.0;
	private double longitude = 0.0;
	
	private String seperator = "-sep-";

	public LocationProperty(String name) {
		super(name, PropertyType.LOCATION);
	}

	public LocationProperty(String name, String value) {
		this(name);
		this.parseString(value);
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
		mutateHistory();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = Property.normalize(address);
		mutateHistory();
	}

	/**
	 * @return the type
	 */
	public GoogleLocationType getLocationType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setLocationType(GoogleLocationType type) {
		this.type = type;
		mutateHistory();
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
		mutateHistory();
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
		mutateHistory();
	}

	@Override
	public String toString() {
		return this.addressName + seperator + this.address + seperator + this.getLocationType().toString() + seperator + this.latitude
				+ seperator + this.longitude;
	}

	@Override
	public String toInformativeString() {
		String result = "";
		String[] split = this.address.split(",");
		if (split.length >= 3) {
			result = ", " + capitalizeFirstLetters(split[1].trim());
		}
		return this.addressName + result;
	}

	@Override
	public void parseString(String str) {
		String[] split = str.split(seperator);
		if (split.length >= 5) {
			this.addressName = capitalizeFirstLetters(split[0].trim());
			this.address = split[1].trim();
			this.type = GoogleLocationType.valueOf(split[2].trim());
			this.latitude = Double.parseDouble(split[3].trim());
			this.longitude = Double.parseDouble(split[4].trim());
		}
		mutateHistory();
	}

	public static String parseHint() {
		return "name;address;city;country;latitude;longitude";
	}

	/**
	 * 
	 * @return
	 */
	public boolean isNull() {
		return latitude == 0.0 && longitude == 0.0;
	}

	/**
	 * 
	 * @param lp
	 * @return
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
	 * 
	 * @param lp
	 * @return
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

	private String capitalizeFirstLetters(String input) {
		String output = "";
		boolean firstLetter = true;

		for (int i = 0; i < input.length(); i++) {
			if (firstLetter) {
				output += Character.toUpperCase(input.charAt(i));
				firstLetter = false;
			} else {
				if (input.charAt(i) == ' ') {
					firstLetter = true;
				}
				output += Character.toLowerCase(input.charAt(i));
			}
		}
		return output;
	}
	
	public String getIcon(){
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

	public void toScript(HtmlMapContentGenerator mapContent, HashMap<String, String> params, boolean openInfoWindowOnLoad) {
		toScript(mapContent, params, 32, openInfoWindowOnLoad);
	}

	public void toScript(HtmlMapContentGenerator mapContent, HashMap<String, String> params, int mapIconSize, boolean openInfoWindowOnLoad) {
		if (isNull()) {
			return;
		}
		if (getAgentView() == null) {
			System.err.println("LocationProperty: Unable to create script, agentView is null");
			return;
		}

		String mapIcon = getAgentView().getMapMarkerImage();
		String smallIcon = getAgentView().getIcon();
		String shadowIcon = null;
		int shadowSize = 48;

		if (!getAgentView().getPropertyValue("Movement").isEmpty()) {
			shadowIcon = "angle/" + getAgentView().getPropertyValue("Movement") + ".png";
		}
		if (getName().equals("SourceLocation")) {
			mapIcon = "source.png";
			smallIcon = "source_icon.png";
			shadowIcon = null;
		}

		if (getName().equals("DestinationLocation")) {
			mapIcon = "destination.png";
			smallIcon = "destination_icon.png";
			shadowIcon = null;
		}

		int zIndex = -getAgentView().getStatus().getValue();
		boolean finished = getAgentView().getPropertyValue("Finished").equals(Boolean.toString(true));
		if (finished && getAgentView().getStatus() == AgentStatus.OK) {
			zIndex = AgentStatus.UNKNOWN.getValue();
		}
		zIndex++;
		boolean label = !getAgentView().getPropertyValue("Hidden").equals(Boolean.toString(true))
				&& (getAgentView().getStatus() == AgentStatus.WARNING || getAgentView().getStatus() == AgentStatus.ERROR);

		boolean showDetails = Settings.getProperty(Settings.SHOW_AGENT_DETAILS).equals("true");

		if (shadowIcon != null && !shadowIcon.isEmpty()) {
			mapContent.addMapMarker(latitude, longitude, getAgentView().getLabel(),
					getAgentView().getDescription(), mapIcon, mapIconSize, shadowIcon, shadowSize, smallIcon,
					(showDetails ? getAgentView().getID() : null), zIndex, label, openInfoWindowOnLoad, "?"
							+ Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + getAgentView().getID(), getAgentView().getID());
		} else {
			mapContent.addMapMarkerWithoutShadow(latitude, longitude, getAgentView()
					.getLabel(), getAgentView().getDescription(), mapIcon, mapIconSize, smallIcon,
					(showDetails ? getAgentView().getID() : null), zIndex, label, openInfoWindowOnLoad, "?"
							+ Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + getAgentView().getID(), getAgentView().getID());
		}
	}

	@Override
	public String arffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String arffData() {
		double distance = 0.0;

		try {
			LocationProperty lp = new LocationProperty("", getAgentView().getLocation());
			distance = this.distanceTo(lp);
		} catch (Exception e) {
		}
		return "" + distance;
	}

}