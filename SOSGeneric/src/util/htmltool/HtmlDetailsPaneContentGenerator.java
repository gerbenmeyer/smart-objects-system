package util.htmltool;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class HtmlDetailsPaneContentGenerator {

	private StringBuffer html;

	public HtmlDetailsPaneContentGenerator() {
		this.html = new StringBuffer();
	}

	public void addCustomHtml(String stuff) {
		html.append(stuff);
	}

	public void addHeader(String header) {
		header = convertToHtml(header);
		html.append(HtmlTool.createHeader2(header));
	}

	public void addSubHeader(String header) {
		header = convertToHtml(header);
		html.append(HtmlTool.createHeader3(header));
	}

	public void addParagraph(String text) {
		text = convertToHtml(text);
		html.append(HtmlTool.createParagraph(text, null));
	}

	public void addTableHeader(String icon, String name) {
		name = convertToHtml(name);
		addTableHeader(icon, name, null);
	}

	public void addTableHeader(String icon, String name, String value) {
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

	public void addTableRow(String icon, String name, String statusIcon) {
		addTableRow(icon, name, null, statusIcon);
	}

	public void addTableRow(String icon, String name, String value, String statusIcon) {
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

	public void addTableRowLink(String icon, String name, String statusIcon, String link) {
		addTableRowLink(icon, name, null, statusIcon, link);
	}

	public void addTableRowLink(String icon, String name, String value, String statusIcon, String link) {
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
					+ link + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "</div>\n";
		} else {
			row = "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = '"
					+ link + "';\">" + statusIcon + "<div class=\"propertyicon\">" + icon + "</div>"
					+ "<div class=\"propertyname\">" + name + "</div>" + "<div class=\"propertyvalue\">" + value
					+ "</div>" + "</div>\n";
		}

		html.append(row);
	}

	public StringBuffer createHtml() {
		return new StringBuffer(html);
	}
	
	private static String convertToHtml(String text){
		if (text == null){
			return null;
		}
		text = text.replaceAll("\n", " ");
		text = HTMLEntities.htmlentities(text);
		return text;
	}

}