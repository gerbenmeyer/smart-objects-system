package util.htmltool;

import java.util.HashMap;

import main.Settings;

/**
 * HtmlPageGenerator generates an HTML page, including header, body and scripts.
 * 
 * @author G.G. Meyer
 */
public class HtmlPageGenerator extends HtmlGenerator {

	protected String title;
	protected String css;
	protected StringBuffer headerHtml;
	protected StringBuffer onLoadScript;
	protected StringBuffer finalScript;

	/**
	 * Constructs a new HtmlPageGenerator instance with a title.
	 * A CSS file may be specified, the path should be accessible form the web.
	 * 
	 * @param title the title of the page
	 * @param css the css file
	 */
	public HtmlPageGenerator(String title, String css) {
		super();
		this.title = title;
		this.css = css;
		this.headerHtml = new StringBuffer("\n");
		this.onLoadScript = new StringBuffer("");
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
		buffer.append(stuff);
		buffer.append("\n");
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
	@Override
	public StringBuffer getHtml() {
		StringBuffer body = new StringBuffer(buffer);

		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		body.append(HtmlTool.createScript(finalScript, scriptAttr));

		HashMap<String, String> bodyAttributes = new HashMap<String, String>();
		bodyAttributes.put("onload", "load();" + onLoadScript.toString());
		
		StringBuffer header = new StringBuffer(headerHtml);
		String analyticsKey = Settings.getProperty(Settings.GOOGLE_ANALYTICS_KEY);
		if (analyticsKey != null) {
			StringBuffer analyticsScript = new StringBuffer("var _gaq = _gaq || []; _gaq.push(['_setAccount', '"+analyticsKey+"']); _gaq.push(['_trackPageview']);"
					+ "(function() {"
					+ "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"
					+ "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"
					+ "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"
					+ "})();");
			header.append(HtmlTool.createScript(analyticsScript, scriptAttr));
		}
		
		return HtmlTool.createHeadBody(title, css, body, header, bodyAttributes);
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
		scriptAttributes.put("src", "ajax.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		scriptAttributes.put("src", "main.js");
		content.append(HtmlTool.createEmptyScript(scriptAttributes));
		return content.toString();
	}
}