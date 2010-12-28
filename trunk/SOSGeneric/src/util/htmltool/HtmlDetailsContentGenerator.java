package util.htmltool;

import util.enums.AgentStatus;


/**
 * HtmlDetailsPaneContentGenerator generates HTML content for the details pane, making use of {@link HtmlTool}.
 * 
 * @author Gerben G. Meyer
 */
public class HtmlDetailsContentGenerator extends HtmlGenerator{

	/**
	 * Constructs a new HtmlDetailsPaneContentGenerator instance.
	 */
	public HtmlDetailsContentGenerator() {
		super();
	}

	/**
	 * Adds a header with an icon, used for data displaying, to the content.
	 * 
	 * @param icon the icon
	 * @param name the name
	 */
	public void addDataHeader(String icon, String name) {
		name = convertToHtml(name);
		addDataHeader(icon, name, null);
	}

	/**
	 * Adds a header with an icon and value, used for data displaying, to the content.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param value the value of the data
	 */
	public void addDataHeader(String icon, String name, String value) {
		name = convertToHtml(name);
		value = convertToHtml(value);

		if (icon.endsWith(".png")) {
			icon = HtmlTool.createImage(icon, icon, 16);
		}
		String header = "";
		if (value == null || value.isEmpty()) {
			header = "<div class=\"propertyheader\">" + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "</div>\n";
		} else {
			header = "<div class=\"propertyheader\">" + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "<div class=\"propertyvalue\">" + value
					+ "</div>" + "</div>\n";
		}
		buffer.append(header);
	}

	/**
	 * Adds a data row with an icon, name and value to the content.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param value the value of the data
	 */
	public void addDataRow(String icon, String name, String value) {
		addDataRow(icon, name, value, "");
	}

	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param value the value of the data
	 * @param statusIcon the status icon
	 */
	public void addDataRow(String icon, String name, String value, String statusIcon) {
		name = convertToHtml(name);
		value = convertToHtml(value);

		if (icon.endsWith(".png")) {
			icon = HtmlTool.createImage(icon, icon, 16);
		}
		if (statusIcon.endsWith(".png")) {
			statusIcon = HtmlTool.createImageRight(statusIcon, statusIcon);
		}
		String row = "";
		if (value == null || value.isEmpty()) {
			row = "<div class=\"property\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "</div>\n";
		} else {
			row = "<div class=\"property\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
			+ "<div class=\"propertyname\">" + name + "</div>" + "<div class=\"propertyvalue\">" + value
			+ "</div>" + "</div>\n";
		}
		buffer.append(row);
	}

	/**
	 * Adds a data row with an icon, name, status icon to the content.
	 * The row is clickable and will link to the specified url.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param statusIcon the status icon
	 * @param url the url to link to
	 */
	public void addDataRowLink(String icon, String name, String statusIcon, String url) {
		addDataRowLink(icon, name, null, statusIcon, url);
	}


	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * The row is clickable and will link to the specified url.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param value the value
	 * @param statusIcon the status icon
	 * @param url the url to link to
	 */
	public void addDataRowLink(String icon, String name, String value, String statusIcon, String url) {
		addDataRowLink(icon, name, value, statusIcon, url, "");
	}


	/**
	 * Adds a data row with an icon, name, value and status icon to the content.
	 * The row is clickable and will link to the specified url.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param value the value
	 * @param statusIcon the status icon
	 * @param url the url to link to
	 */
	public void addDataRowLink(String icon, String name, String value, String statusIcon, String url, String style) {
		name = convertToHtml(name);
		value = convertToHtml(value);

		if (icon.endsWith(".png")) {
			icon = HtmlTool.createImage(icon, icon, 16);
		}
		if (statusIcon.endsWith(".png")) {
			statusIcon = HtmlTool.createImageRight(statusIcon, statusIcon);
		}
		String row = "";
		if (value == null || value.isEmpty()) {
			row = "<div class=\"property linked_property\" "+style+" onclick=\"document.getElementById('hidden_frame').src = '"
					+ url + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "</div>\n";
		} else {
			row = "<div class=\"property linked_property\" "+style+" onclick=\"document.getElementById('hidden_frame').src = '"
					+ url + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "<div class=\"propertyvalue\">" + value
					+ "</div>" + "</div>\n";
		}
		buffer.append(row);
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
		trainingCode += HtmlTool.createLink(url + AgentStatus.OK.toString(),
				HtmlTool.createImage("ok.png", "ok", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url
				+ AgentStatus.WARNING.toString(), HtmlTool.createImage(
				"warning.png", "warning", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url + AgentStatus.ERROR.toString(),
				HtmlTool.createImage("error.png", "error", 16), "hidden_frame");
		addDataRow("info.png", "Provide status", trainingCode, "");
	}

}