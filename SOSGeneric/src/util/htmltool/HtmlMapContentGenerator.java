package util.htmltool;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.property.properties.LocationProperty;
import util.enums.AgentStatus;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

/**
 * HtmlMapContentGenerator provides methods for building a map using Goolge
 * maps.
 * 
 * @author Gerben G. Meyer
 */
public class HtmlMapContentGenerator extends HtmlGenerator{

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

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void addCustomScript(String stuff) {
		buffer.append(stuff);
	}

	/**
	 * Returns the HTML code of the map.
	 * 
	 * @return the code
	 */
	public StringBuffer createHtml() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = createMapScriptHeader();
		headcontent.append("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"300\" />\n");
		headcontent.append(HtmlTool.createScript(buffer, scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody(title, null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}

	/**
	 * Adds javascript which will go to the current user location.
	 */
	public void gotoUserLocation() {
		buffer.append("parent.gotoUserLocation();\n");
	}

	/**
	 * Adds javascript which clears the map content.
	 */
	public void clearMapContent() {
		buffer.append("parent.clearMap();\n");
	}

	/**
	 * Adds javascript which clears the map data.
	 */
	public void clearMapData() {
		buffer.append("parent.clearMarkers();\n");
	}

	/**
	 * Adds javascript which draws the map.
	 */
	public void drawMap() {
		buffer.append("parent.drawMap();\n");
	}

	/**
	 * 
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
	 * @param infoWindowContent the details showed in the info window of the marker. Must be formatted as HTML.
	 * @param openInfoWindowOnLoad
	 * @param id the identifier of the marker
	 */
	public void addMapMarker(double latitude, double longitude, String title, String mapicon,
			int iconsize, int zIndex, boolean showLabel, String id) {
		title = convertToHtml(title);
		buffer.append("parent.addMarker(" + latitude + "," + longitude + ",'" + title + "','" + mapicon + "',"
				+ iconsize + "," + zIndex + ", " + showLabel + ",'" + id + "');\n");
	}
	
	/**
	 * Adds javascript which adds a marker for a certain agent to the map.
	 * 
	 * @param av the view of the agent to be located
	 * @param iconsize the size of the map icon
	 */
	public void addMapMarker(AgentViewable av, int iconsize){
		addMapMarker(av, iconsize, false);
	}
	
	/**
	 * Adds javascript which adds a marker for a certain agent to the map.
	 * 
	 * @param av the view of the agent to be located
	 * @param iconsize the size of the map icon
	 * @param panToLocation true if the map should pan to this agents location 
	 */
	public void addMapMarker(AgentViewable av, int iconsize, boolean panToLocation){
		LocationProperty lp = new LocationProperty("", av.get(Agent.LOCATION));
		if (lp.isNull()) {
			return;
		}
		String id = av.getID();
		AgentStatus status = av.getStatus();
		int zIndex = -status.getValue()+1;
		boolean showLabel = !av.get(Agent.HIDDEN).equals(Boolean.toString(true))
		&& (status == AgentStatus.WARNING || status == AgentStatus.ERROR);
		addMapMarker(lp.getLatitude(), lp.getLongitude(), av.get(Agent.LABEL), av.getMapMarkerImage(), iconsize, zIndex, showLabel, id);
		if (panToLocation) {
			panToLocation(lp.getLatitude(), lp.getLongitude());
		}
	}

	/**
	 * Adds a balloon to the code, and associates it with a an existing marker. 
	 * 
	 * @param av the view of the agent
	 * @param openBalloonOnLoad if the balloon should open on page load
	 */
	public void addMapBalloon(AgentViewable av, boolean openBalloonOnLoad) {
		HtmlMapBalloonContentGenerator balloonGen = new HtmlMapBalloonContentGenerator();
		HashMap<String, String> params = new HashMap<String, String>();
		av.generateMapBalloonContent(balloonGen, params);
		buffer.append(String.format("parent.addMarkerBalloon('%s', '%s', %b);\n", av.getID(), convertToHtml(balloonGen.getHtml().toString()), openBalloonOnLoad));
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
		buffer.append("parent.addDirection({location:'" + latitude + ", " + longitude + "', status:'" + status
				+ "', poly: " + polyLine + "});\n");
	}

	/**
	 * Adds javascript to pan to a specific location.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void panToLocation(double latitude, double longitude) {
		buffer.append("parent.panToLocation(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the centre of the map.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setCenter(double latitude, double longitude) {
		buffer.append("parent.setCenter(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the zoom level of the map.
	 * 
	 * @param zoom
	 */
	public void setZoom(int zoom) {
		buffer.append("parent.setZoom(" + zoom + ");\n");
	}

	/**
	 * Triggers an info window of a marker to pop up.
	 * 
	 * @param markerId
	 *            the identifier of the marker
	 */
	public void popupInfoWindow(String markerId) {
		buffer.append("google.maps.event.trigger(parent.markers['" + markerId + "'], 'click');\n");
	}

	/**
	 * Creates the necessary header content of this map.
	 * 
	 * @return the header content
	 */
	private static StringBuffer createMapScriptHeader() {
		StringBuffer content = new StringBuffer();
		HashMap<String, String> scriptAttributes = new HashMap<String, String>();
		scriptAttributes.put("type", "text/javascript");
		scriptAttributes.put("src", "http://maps.google.com/maps/api/js?sensor=false");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		return content;
	}

	/**
	 * Converts text to HTML and javascript compatible text, inserting HTML
	 * entities where necessary.
	 * 
	 * @param text
	 *            the input text
	 * @return the HTML compatible output
	 */
	protected static String convertToHtml(String text) {
		if (text == null) {
			return null;
		}
		text = text.replaceAll("\n", " ");
		text = HTMLEntities.htmlentities(text);
		text = text.replace("\\", "\\\\");
		text = text.replace("'", "\\'");
		return text;
	}
}