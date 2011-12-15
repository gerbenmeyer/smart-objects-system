package util.htmltool;

import util.enums.AgentStatus;

/**
 * HtmlDetailsPaneContentGenerator generates HTML content for the details pane,
 * making use of {@link HtmlTool}.
 * 
 * @author Gerben G. Meyer
 */
public class HtmlDetailsContentGenerator extends HtmlGenerator {

	/**
	 * Constructs a new HtmlDetailsPaneContentGenerator instance.
	 */
	public HtmlDetailsContentGenerator() {
		super();
	}

	/**
	 * Adds a header with an icon, used for data displaying, to the content.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name
	 */
	public void addDataHeader(String icon, String name) {
		addDataHeader(icon, name, null);
	}

	/**
	 * Adds a header with an icon and value, used for data displaying, to the
	 * content.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param value
	 *            the value of the data
	 */
	public void addDataHeader(String icon, String name, String value) {
		name = HtmlTool.encodeHtmlComponent(name);
		value = HtmlTool.encodeHtmlComponent(value);
		if (icon.endsWith(".png")) {
			icon = HtmlTool.img(icon, icon);
		}
		String header = "";
		if (value == null || value.isEmpty()) {
			header = HtmlTool.div(HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\""), "class=\"propertyheader\"");
		} else {
			header = HtmlTool.div(HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\"") + HtmlTool.div(value, "class=\"propertyvalue\""), "class=\"propertyheader\"");
		}
		add(header);
	}

	/**
	 * Adds a data row with an icon, name and value to the content.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param value
	 *            the value of the data
	 */
	public void addDataRow(String icon, String name, String value) {
		addDataRow(icon, name, value, "");
	}

	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param value
	 *            the value of the data
	 * @param statusIcon
	 *            the status icon
	 */
	public void addDataRow(String icon, String name, String value, String statusIcon) {
		name = HtmlTool.encodeHtmlComponent(name);
		value = HtmlTool.encodeHtmlComponent(value);		
		if (icon.endsWith(".png")) {
			icon = HtmlTool.img(icon, icon);
		}
		if (statusIcon.endsWith(".png")) {
			statusIcon = HtmlTool.img(statusIcon, statusIcon, "align=\"right\"");
		}
		String row = "";
		if (value == null || value.isEmpty()) {
			row = HtmlTool.div(statusIcon + HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\""), "class=\"property\"");
		} else {
			row = HtmlTool.div(statusIcon + HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\"") + HtmlTool.div(value, "class=\"propertyvalue\""), "class=\"property\"");
		}
		add(row);
	}

	/**
	 * Adds a data row with an icon, name, status icon to the content. The row
	 * is clickable and will link to the specified url.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param statusIcon
	 *            the status icon
	 * @param url
	 *            the url to link to
	 */
	public void addDataRowLink(String icon, String name, String statusIcon, String url) {
		addDataRowLink(icon, name, null, statusIcon, url);
	}

	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * The row is clickable and will link to the specified url.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param value
	 *            the value
	 * @param statusIcon
	 *            the status icon
	 * @param url
	 *            the url to link to
	 */
	public void addDataRowLink(String icon, String name, String value, String statusIcon, String url) {
		addDataRowLink(icon, name, value, statusIcon, url, "");
	}

	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * The row is clickable and will link to the specified url.
	 * 
	 * @param icon
	 *            the icon
	 * @param name
	 *            the name of the data
	 * @param value
	 *            the value
	 * @param statusIcon
	 *            the status icon
	 * @param url
	 *            the url to link to
	 */
	public void addDataRowLink(String icon, String name, String value, String statusIcon, String url, String style) {
		name = HtmlTool.encodeHtmlComponent(name);
		value = HtmlTool.encodeHtmlComponent(value);		
		if (icon.endsWith(".png")) {
			icon = HtmlTool.img(icon, icon);
		}
		if (statusIcon.endsWith(".png")) {
			statusIcon = HtmlTool.img(statusIcon, statusIcon, "align=\"right\"");
		}
		String row = "";
		if (value == null || value.isEmpty()) {
			row = HtmlTool.div(statusIcon + HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\""), "class=\"property linked_property\" "+ style + " onclick=\"document.getElementById('hidden_frame').src = '" + url + "';\"");
		} else {
			row = HtmlTool.div(statusIcon + HtmlTool.div(icon, "class=\"propertyicon\"") + HtmlTool.div(name, "class=\"propertyname\"") + HtmlTool.div(value, "class=\"propertyvalue\""), "class=\"property linked_property\" "+ style + " onclick=\"document.getElementById('hidden_frame').src = '" + url + "';\"");
		}
		add(row);
	}

	/**
	 * This function generates the code required for training agents as part of
	 * the detailsPane. Has to be used in combination with
	 * generateMapContentTrainingCode
	 * 
	 * @param agentCode
	 */
	public void addDataRowTrainingButtons(String agentCode) {
		String url = agentCode + ".train?learnstatus=";
		String trainingCode = "";
		trainingCode += HtmlTool.aLink(HtmlTool.img("ok.png", "ok"), url + AgentStatus.OK.toString(), "target=\"hidden_frame\"");
		trainingCode += " ";
		trainingCode += HtmlTool.aLink(HtmlTool.img("warning.png", "warning"), url + AgentStatus.WARNING.toString(), "target=\"hidden_frame\"");
		trainingCode += " ";
		trainingCode += HtmlTool.aLink(HtmlTool.img("error.png", "error"), url + AgentStatus.ERROR.toString(), "target=\"hidden_frame\"");
		String row = HtmlTool.div(HtmlTool.div(HtmlTool.img("info.png", "info"), "class=\"propertyicon\"") + HtmlTool.div("Provide status", "class=\"propertyname\"") + HtmlTool.div(trainingCode, "class=\"propertyvalue\""), "class=\"property\"");
		add(row);
	}

	public StringBuffer generateDetailsContent() {
		return getBuffer();
	}

}