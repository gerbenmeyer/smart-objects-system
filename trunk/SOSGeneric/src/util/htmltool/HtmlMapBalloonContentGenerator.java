package util.htmltool;

import java.util.HashMap;

public class HtmlMapBalloonContentGenerator extends HtmlGenerator {

	/**
	 * Constructs a new HtmlMapBalloonGenerator instance.
	 */
	public HtmlMapBalloonContentGenerator() {
		super();
	}

	/**
	 * Adds a balloon content div.
	 * 
	 * @param text the text to be wrapped in the div
	 */
	public void addContentDiv(String text) {
		text = convertToHtml(text);
		HashMap<String, String> attr = new HashMap<String, String>();
		attr.put("class", "infoWindowContent");
		buffer.append(HtmlTool.createDiv(text, attr));
	}
}