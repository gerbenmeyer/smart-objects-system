package util.htmltool;

import java.util.HashMap;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class HtmlMapContentGenerator {

	private String title;
	private StringBuffer script;

	public HtmlMapContentGenerator(String title) {
		this.title = title;
		this.script = new StringBuffer();
	}

	public void addCustomScript(String stuff) {
		script.append(stuff);
	}

	public StringBuffer createHtml() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = createMapScriptHeader();
		headcontent.append("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"300\" />\n");
		headcontent.append(HtmlTool.createScript(script, scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody(title, null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}

	public void gotoUserLocation() {
		script.append("parent.gotoUserLocation();\n");
	}

	public void clearMapContent() {
		script.append("parent.clearMap();\n");
	}

	public void clearMapData() {
		script.append("parent.clearMarkers();\n");
	}

	public void drawMap() {
		script.append("parent.drawMap();\n");
	}

	public void addMapMarkerWithoutShadow(double latitude, double longitude, String title, String details,
			String mapicon, int size, String smallicon, String url, int zIndex, boolean showlabel,
			boolean openInfoWindowOnLoad, String deeplink, String id) {
		addMapMarker(latitude, longitude, title, details, mapicon, size, "empty.png", size, smallicon, url, zIndex,
				showlabel, openInfoWindowOnLoad, deeplink, id);
	}

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

	public void addMapDirection(double latitude, double longitude, String status, boolean polyLine) {
		script.append("parent.addDirection({location:'" + latitude + ", " + longitude + "', status:'" + status
				+ "', poly: " + polyLine + "});\n");
	}

	public void panToLocation(double latitude, double longitude) {
		script.append("parent.panToLocation(" + latitude + "," + longitude + ");\n");
	}

	public void setCenter(double latitude, double longitude) {
		script.append("parent.setCenter(" + latitude + "," + longitude + ");\n");
	}

	public void setZoom(int zoom) {
		script.append("parent.setZoom(" + zoom + ");\n");
	}

	public void popupInfoWindow(String markerId) {
		script.append("google.maps.event.trigger(parent.markers['" + markerId + "'], 'click');\n");
	}

	private static StringBuffer createMapScriptHeader() {
		StringBuffer content = new StringBuffer();
		HashMap<String, String> scriptAttributes = new HashMap<String, String>();
		scriptAttributes.put("type", "text/javascript");
		scriptAttributes.put("src", "http://maps.google.com/maps/api/js?sensor=false");
		content.append(HtmlTool.createScript("", scriptAttributes));
		return content;
	}

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