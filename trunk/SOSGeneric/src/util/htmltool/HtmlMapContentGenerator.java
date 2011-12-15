package util.htmltool;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.property.properties.LocationProperty;

/**
 * HtmlMapContentGenerator provides methods for building a map using Goolge
 * maps.
 * 
 * @author Gerben G. Meyer
 */
public class HtmlMapContentGenerator extends HtmlGenerator {

	private String title;

	/**
	 * Constructs a new HtmlMapContentGenerator instance with a title.
	 * 
	 * @param title
	 *            the title of the page
	 */
	public HtmlMapContentGenerator(String title) {
		super();
		this.title = title;
	}

	public StringBuffer generateMapContent() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = new StringBuffer();
		// headcontent.append("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"300\" />\n");
		insert("var p = parent;");
		
		headcontent.append(HtmlTool.script(getBuffer().toString()));
		
		headcontent.insert(0, HtmlTool.title(title));
		
		return HtmlTool.html(HtmlTool.head(headcontent), new StringBuffer());
		
	}
	
	
	/**
	 * Returns the HTML code of the map script.
	 * 
	 * @return the code
	 */
	@Deprecated
	public StringBuffer createMapContentScript() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = new StringBuffer(createMapScriptHeader());
		// headcontent.append("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"300\" />\n");
		insert("var p = parent;");
		headcontent.append(HtmlTool.createScript(getBuffer(), scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody(title, null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}

	/**
	 * Adds javascript which will go to the current user location.
	 */
	public void gotoUserLocation() {
		add("p.gotoUserLocation();");
	}

	/**
	 * Adds javascript which will go to the world overview.
	 */
	public void gotoWorldOverview() {
		add("p.gotoWorldOverview();");
	}

	/**
	 * Adds javascript which clears the map content.
	 */
	public void clearMapContent() {
		add("p.clearMap();");
	}

	/**
	 * Adds javascript which clears the map data.
	 */
	public void clearMapData() {
		add("p.clearMarkers();");
	}

	/**
	 * Adds javascript which draws the map.
	 */
	public void drawMap() {
		add("p.drawMap();");
	}

	/**
	 * Adds javascript which adds a marker to the map.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param mapicon
	 * @param showLabel
	 * @param id
	 *            the identifier of the marker
	 * @param zindex
	 *            the z-index of the marker
	 * @param type
	 *            the type of marker/agent
	 */
	public void addMapMarker(double latitude, double longitude, String title, String mapicon, String id, int zindex, String type) {
		title = escapeForJS(title);
		add("p.aM(" + latitude + "," + longitude + ",'" + title + "','" + mapicon + "','" + id + "'," + zindex + ",'" + type + "');");
	}

	/**
	 * Adds javascript which adds a marker to the map.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param mapicon
	 * @param iconsize
	 * @param zIndex
	 * @param showLabel
	 * @param id
	 *            the identifier of the marker
	 */
	public void addMapMarker(double latitude, double longitude, String title, String mapicon, int iconsize, int zIndex, String id) {
		title = escapeForJS(title);
		add("p.addMarker(" + latitude + "," + longitude + ",'" + title + "','" + mapicon + "'," + iconsize + "," + zIndex + ",'" + id + "');");
	}

	/**
	 * Adds javascript which adds a marker for a certain agent to the map.
	 * 
	 * @param av
	 *            the view of the agent to be located
	 * @param zindex
	 */
	public void addMapMarker(AgentViewable av, int zindex) {
		String loc = av.get(Agent.LOCATION);
		if (loc.isEmpty()) {
			return;
		}
		LocationProperty lp = new LocationProperty("", loc);
		if (lp.isNull()) {
			return;
		}
		addMapMarker(lp.getLatitude(), lp.getLongitude(), av.get(Agent.LABEL), av.getMapMarkerImage(), av.getID(), zindex, av.get(Agent.TYPE));
	}

	/**
	 * Adds javascript which adds a marker for a certain agent to the map.
	 * 
	 * @param av
	 *            the view of the agent to be located
	 */
	public void addMapMarker(AgentViewable av) {
		addMapMarker(av, 1);
	}

	/**
	 * Adds a point to the directions on the map.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param status
	 * @param polyLine
	 */
	public void addMapDirection(double latitude, double longitude, String status, boolean polyLine) {
		add("p.addDirection({location:'" + latitude + ", " + longitude + "', status:'" + status + "', poly: " + polyLine + "});");
	}

	/**
	 * Adds javascript to pan to a specific location.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void panToLocation(double latitude, double longitude) {
		add("p.panToLocation(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the centre of the map.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setCenter(double latitude, double longitude) {
		add("p.setCenter(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the zoom level of the map.
	 * 
	 * @param zoom
	 */
	public void setZoom(int zoom) {
		add("p.setZoom(" + zoom + ");\n");
	}

	/**
	 * Triggers an info window of a marker to pop up.
	 * 
	 * @param markerId
	 *            the identifier of the marker
	 */
	public void popupInfoWindow(String markerId) {
		add("google.maps.event.trigger(p.markers['" + markerId + "'], 'click');\n");
	}

	/**
	 * Creates the necessary header content of this map.
	 * 
	 * @return the header content
	 */
	/**
	 * Creates the HTML header needed to include a map.
	 * 
	 * @return a string containing the HTML code
	 */
	public static String createMapScriptHeader() {
		return HtmlTool.script("", "src=\"http://maps.google.com/maps/api/js?sensor=false\"");
	}

	/**
	 * Converts text JavaScript compatible text
	 * 
	 * @param text
	 *            the input text
	 * @return the JavaScript compatible output
	 */
	protected static String escapeForJS(String text) {
		if (text == null) {
			return null;
		}
		return text.replace("\\", "\\\\").replace("'", "\\'");
	}
}