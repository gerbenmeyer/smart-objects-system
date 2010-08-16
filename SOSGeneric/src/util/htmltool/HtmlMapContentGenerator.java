package util.htmltool;

import java.util.HashMap;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

/**
 * HtmlMapContentGenerator provides methods for building a map using Goolge maps.
 * 
 * @author G.G. Meyer
 */
public class HtmlMapContentGenerator {

	private String title;
	private StringBuffer script;

	/**
	 * Constructs a new HtmlMapContentGenerator instance with a title.
	 * 
	 * @param title the title of the page
	 */
	public HtmlMapContentGenerator(String title) {
		this.title = title;
		this.script = new StringBuffer();
	}

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff the custom HTML
	 */
	public void addCustomScript(String stuff) {
		script.append(stuff);
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
		headcontent.append(HtmlTool.createScript(script, scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody(title, null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}

	/**
	 * Adds javascript which will go to the current user location.
	 */
	public void gotoUserLocation() {
		script.append("parent.gotoUserLocation();\n");
	}

	/**
	 * Adds javascript which clears the map content.
	 */
	public void clearMapContent() {
		script.append("parent.clearMap();\n");
	}

	/**
	 * Adds javascript which clears the map data.
	 */
	public void clearMapData() {
		script.append("parent.clearMarkers();\n");
	}

	/**
	 * Adds javascript which draws the map.
	 */
	public void drawMap() {
		script.append("parent.drawMap();\n");
	}

	/**
	 * Adds javascript which adds a marker with an empty shadow to the map.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param details the details showed in the info window of the marker. Must be formatted as HTML.
	 * @param mapicon
	 * @param size
	 * @param smallicon
	 * @param url
	 * @param zIndex
	 * @param showlabel
	 * @param openInfoWindowOnLoad
	 * @param deeplink
	 * @param id the identifier of the marker
	 */
	public void addMapMarkerWithoutShadow(double latitude, double longitude, String title, String details,
			String mapicon, int size, String smallicon, String url, int zIndex, boolean showlabel,
			boolean openInfoWindowOnLoad, String deeplink, String id) {
		addMapMarker(latitude, longitude, title, details, mapicon, size, "empty.png", size, smallicon, url, zIndex,
				showlabel, openInfoWindowOnLoad, deeplink, id);
	}

	/**
	 * Adds javascript which adds a marker with a shadow to the map.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param details the details showed in the info window of the marker. Must be formatted as HTML. 
	 * @param mapicon
	 * @param iconsize
	 * @param shadowicon
	 * @param shadowsize
	 * @param smallicon
	 * @param url
	 * @param zIndex
	 * @param showlabel
	 * @param openInfoWindowOnLoad
	 * @param deeplink
	 * @param id the identifier of the marker
	 */
	public void addMapMarker(double latitude, double longitude, String title, String details, String mapicon,
			int iconsize, String shadowicon, int shadowsize, String smallicon, String url, int zIndex,
			boolean showlabel, boolean openInfoWindowOnLoad, String deeplink, String id) {

		title = convertToHtml(title);
		details = convertToHtml(details);

		script.append("parent.addMarker(" + latitude + "," + longitude + ",'" + title + "','" + mapicon + "',"
				+ iconsize + ",'" + shadowicon + "'," + shadowsize + ",'" + smallicon + "','"
				+ (url != null && !url.isEmpty() ? url : "") + "','" + details + "'," + zIndex + ", " + showlabel
				+ ", " + openInfoWindowOnLoad + (deeplink != null && !deeplink.isEmpty() ? ", '" + deeplink + "'" : "")
				+ ",'" + id + "');\n");
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
		script.append("parent.addDirection({location:'" + latitude + ", " + longitude + "', status:'" + status
				+ "', poly: " + polyLine + "});\n");
	}

	/**
	 * Adds javascript to pan to a specific location.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void panToLocation(double latitude, double longitude) {
		script.append("parent.panToLocation(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the centre of the map.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setCenter(double latitude, double longitude) {
		script.append("parent.setCenter(" + latitude + "," + longitude + ");\n");
	}

	/**
	 * Sets the zoom level of the map.
	 * 
	 * @param zoom
	 */
	public void setZoom(int zoom) {
		script.append("parent.setZoom(" + zoom + ");\n");
	}

	/**
	 * Triggers an info window of a marker to pop up.
	 * 
	 * @param markerId the identifier of the marker
	 */
	public void popupInfoWindow(String markerId) {
		script.append("google.maps.event.trigger(parent.markers['" + markerId + "'], 'click');\n");
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
	 * Converts text to HTML and javascript compatible text, inserting HTML entities where necessary.
	 * 
	 * @param text the input text
	 * @return the HTML compatible output
	 */
	private static String convertToHtml(String text) {
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