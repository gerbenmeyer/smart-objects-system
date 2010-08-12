package util.htmltool;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

/**
 * HtmlDetailsPaneContentGenerator generates HTML content for the details pane, making use of {@link #HtmlTool}.
 * 
 * @author G.G. Meyer
 */
public class HtmlDetailsPaneContentGenerator {

	private StringBuffer html;

	/**
	 * Constructs a new HtmlDetailsPaneContentGenerator instance.
	 */
	public HtmlDetailsPaneContentGenerator() {
		this.html = new StringBuffer();
	}

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff the custom HTML
	 */
	public void addCustomHtml(String stuff) {
		html.append(stuff);
	}

	/**
	 * Adds a H2 header to the content.
	 * 
	 * @param header the header text
	 */
	public void addHeader(String header) {
		header = convertToHtml(header);
		html.append(HtmlTool.createHeader2(header));
	}

	/**
	 * Adds a H3 header to the content.
	 * 
	 * @param header the header text
	 */
	public void addSubHeader(String header) {
		header = convertToHtml(header);
		html.append(HtmlTool.createHeader3(header));
	}

	/**
	 * Adds a paragraph with text to the content.
	 * 
	 * @param text the text to be wrapped in the paragraph
	 */
	public void addParagraph(String text) {
		text = convertToHtml(text);
		html.append(HtmlTool.createParagraph(text, null));
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
		html.append(header);
	}

	/**
	 * Adds a data row with an icon, name and status icon to the content.
	 * 
	 * @param icon the icon
	 * @param name the name of the data
	 * @param statusIcon the status icon
	 */
	public void addDataRow(String icon, String name, String statusIcon) {
		addDataRow(icon, name, null, statusIcon);
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
			statusIcon = HtmlTool.createImageRight(statusIcon, statusIcon, 16);
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
		html.append(row);
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
		name = convertToHtml(name);
		value = convertToHtml(value);

		if (icon.endsWith(".png")) {
			icon = HtmlTool.createImage(icon, icon, 16);
		}
		if (statusIcon.endsWith(".png")) {
			statusIcon = HtmlTool.createImageRight(statusIcon, statusIcon, 16);
		}
		String row = "";
		if (value == null || value.isEmpty()) {
			row = "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = '"
					+ url + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "</div>\n";
		} else {
			row = "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = '"
					+ url + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "<div class=\"propertyvalue\">" + value
					+ "</div>" + "</div>\n";
		}
		html.append(row);
	}

	/**
	 * Returns the HTML code of the details pane.
	 * 
	 * @return the code
	 */
	public StringBuffer createHtml() {
		return new StringBuffer(html);
	}
	
	/**
	 * Converts text to HTML compatible text, inserting HTML entities where necessary.
	 * 
	 * @param text the input text
	 * @return the HTML compatible output
	 */
	private static String convertToHtml(String text){
		if (text == null){
			return null;
		}
		text = text.replaceAll("\n", " ");
		text = HTMLEntities.htmlentities(text);
		return text;
	}
}