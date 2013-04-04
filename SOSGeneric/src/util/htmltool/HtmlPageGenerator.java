package util.htmltool;

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
		this.headerHtml = new StringBuffer("");
		this.onLoadScript = new StringBuffer("");
		this.finalScript = new StringBuffer("");
	}

	/**
	 * Adds HTML code to the header.
	 * 
	 * @param content the code to be added
	 */
	public void addToHeaderHtml(String content) {
		headerHtml.append(content);
	}

	/**
	 * Adds javascript code to the onload script.
	 * 
	 * @param content the code to be added
	 */
	public void addToOnLoadScript(String content) {
		onLoadScript.append(content);
	}

	/**
	 * Adds HTML code to the body.
	 * 
	 * @param content the code to be added
	 */
	public void addToBodyHtml(String content) {
		add(content);
	}

	/**
	 * Adds javascript code to the script at the end of the HTML page.
	 * 
	 * @param content the code to be added
	 */
	public void addToFinalScript(String content) {
		finalScript.append(content);
	}
	
	public StringBuffer generatePage() {
		StringBuffer body = new StringBuffer(getBuffer());

		String bodyAttributes = "onload=\"load();"+onLoadScript.toString()+"\"";

		StringBuffer head = new StringBuffer(headerHtml);
		head.insert(0, "<meta charset=\"UTF-8\" />");
		head.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />");
		
		String analyticsKey = Settings.getProperty(Settings.GOOGLE_ANALYTICS_KEY);
		if (analyticsKey != null) {
			head.append(HtmlTool.script("var _gaq = _gaq || []; _gaq.push(['_setAccount', '"+analyticsKey+"']); _gaq.push(['_trackPageview']);"
					+ "(function() {"
					+ "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"
					+ "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"
					+ "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"
					+ "})();"));
		}
		
		body.append(HtmlTool.script(finalScript.toString()));
		
		if (title != null && !title.isEmpty()) head.insert(0, HtmlTool.title(title));
		if (css != null && !css.isEmpty()) head.insert(0, HtmlTool.linkCss(css));
		
		return HtmlTool.html(HtmlTool.head(head), HtmlTool.body(body,bodyAttributes));
	}
	
	/**
	 * Creates the HTML header needed to include a map.
	 * 
	 * @return a string containing the HTML code
	 */
	public static String createMapScriptHeader() {
		String script = "";
		
		script += HtmlTool.script("", "src=\"http://maps.google.com/maps/api/js?sensor=false\"");
		script += HtmlTool.script("", "src=\"markerclusterer.js\"");
		script += HtmlTool.script("", "src=\"ajax.js\"");
		script += HtmlTool.script("", "src=\"main.js\"");
		
		return script;
	}
}