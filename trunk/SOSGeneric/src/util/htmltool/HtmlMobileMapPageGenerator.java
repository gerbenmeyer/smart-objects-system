package util.htmltool;

/**
 * This HtmlMapGenerator extension is used to generate the main overview page for mobile browsers.
 * 
 * @author G.G. Meyer
 */
public class HtmlMobileMapPageGenerator extends HtmlPageGenerator {

	/**
	 * Constructs a new HtmlMapPageGenerator instance with a title.
	 * A CSS file may be specified, the path should be accessible form the web.
	 * 
	 * @param title the title of the page
	 * @param css the css file
	 */
	public HtmlMobileMapPageGenerator(String title, String css) {
		super(title, css);
		addToHeaderHtml(createMapScriptHeader());
		addToBodyHtml(HtmlTool.div("", "id=\"map_canvas\" name=\"map_canvas\""));
		addToBodyHtml(HtmlTool.iframe("","id=\"hidden_frame\" name=\"hidden_frame\""));
	}
}