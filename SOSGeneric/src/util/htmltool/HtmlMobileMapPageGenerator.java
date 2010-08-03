package util.htmltool;

import java.util.HashMap;

public class HtmlMobileMapPageGenerator extends HtmlPageGenerator {

	public HtmlMobileMapPageGenerator(String title, String css) {
		super(title, css);

		addToHeaderHtml(createMapScriptHeader());
		
		addToBodyHtml(HtmlTool.createDiv("", "map_canvas"));

		addToBodyHtml(HtmlTool.createIFrame("hidden_frame", ""));

	}

	public static String createMapScriptHeader() {
		StringBuffer content = new StringBuffer();
		HashMap<String, String> scriptAttributes = new HashMap<String, String>();
		scriptAttributes.put("type", "text/javascript");
		scriptAttributes.put("src", "http://maps.google.com/maps/api/js?sensor=false");
		content.append(HtmlTool.createScript("", scriptAttributes));
		scriptAttributes.put("src", "markerclusterer.js");
		content.append(HtmlTool.createScript("", scriptAttributes));
		scriptAttributes.put("src", "markerwithlabel.js");
		content.append(HtmlTool.createScript("", scriptAttributes));
		scriptAttributes.put("src", "ajax.js");
		content.append(HtmlTool.createScript("", scriptAttributes));
		scriptAttributes.put("src", "main.js");
		content.append(HtmlTool.createScript("", scriptAttributes));
		return content.toString();
	}
}