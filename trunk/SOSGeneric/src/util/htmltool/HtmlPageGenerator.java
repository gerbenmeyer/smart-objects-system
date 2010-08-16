package util.htmltool;

import java.util.HashMap;

/**
 * HtmlPageGenerator generates an HTML page, including header, body and scripts.
 * 
 * @author G.G. Meyer
 */
public class HtmlPageGenerator {

	protected String title;
	protected String css;
	protected StringBuffer headerHtml;
	protected StringBuffer onLoadScript;
	protected StringBuffer bodyHtml;
	protected StringBuffer finalScript;

	/**
	 * Constructs a new HtmlPageGenerator instance with a title.
	 * A CSS file may be specified, the path should be accessible form the web.
	 * 
	 * @param title the title of the page
	 * @param css the css file
	 */
	public HtmlPageGenerator(String title, String css) {
		this.title = title;
		this.css = css;
		this.headerHtml = new StringBuffer("\n");
		this.onLoadScript = new StringBuffer("");
		this.bodyHtml = new StringBuffer("\n");
		this.finalScript = new StringBuffer("\n");
	}

	/**
	 * Adds HTML code to the header.
	 * 
	 * @param stuff the code to be added
	 */
	public void addToHeaderHtml(String stuff) {
		headerHtml.append(stuff);
		headerHtml.append("\n");
	}

	/**
	 * Adds javascript code to the onload script.
	 * 
	 * @param stuff the code to be added
	 */
	public void addToOnLoadScript(String stuff) {
		onLoadScript.append(stuff);
	}

	/**
	 * Adds HTML code to the body.
	 * 
	 * @param stuff the code to be added
	 */
	public void addToBodyHtml(String stuff) {
		bodyHtml.append(stuff);
		bodyHtml.append("\n");
	}

	/**
	 * Adds javascript code to the script at the end of the HTML page.
	 * 
	 * @param stuff the code to be added
	 */
	public void addToFinalScript(String stuff) {
		finalScript.append(stuff);
		finalScript.append("\n");
	}

	/**
	 * Creates the HTML page.
	 * 
	 * @return the HTML code
	 */
	public StringBuffer createHtml() {
		StringBuffer body = new StringBuffer(bodyHtml);

		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		body.append(HtmlTool.createScript(finalScript, scriptAttr));

		HashMap<String, String> bodyAttributes = new HashMap<String, String>();
		bodyAttributes.put("onresize", "setWindowSize();");
		bodyAttributes.put("onload", "load();" + onLoadScript.toString());

		return HtmlTool.createHeadBody(title, css, body, headerHtml, bodyAttributes);
	}
	
	/**
	 * Creates the HTML header needed to include a map.
	 * 
	 * @return a string containing the HTML code
	 */
	public static String createMapScriptHeader() {
		StringBuffer content = new StringBuffer();
		HashMap<String, String> scriptAttributes = new HashMap<String, String>();
		scriptAttributes.put("type", "text/javascript");
		scriptAttributes.put("src", "http://maps.google.com/maps/api/js?sensor=false");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		scriptAttributes.put("src", "markerclusterer.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		scriptAttributes.put("src", "markerwithlabel.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		scriptAttributes.put("src", "ajax.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		scriptAttributes.put("src", "main.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		return content.toString();
	}
}