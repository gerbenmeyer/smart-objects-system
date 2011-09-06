package util.htmltool;


/**
 * This HtmlMapGenerator extension is used to generate the main overview page for normal browsers.
 * 
 * @author G.G. Meyer
 */
public class HtmlMapPageGenerator extends HtmlPageGenerator {

	/**
	 * Constructs a new HtmlMapPageGenerator instance with a title.
	 * A CSS file may be specified, the path should be accessible form the web.
	 * 
	 * @param title the title of the page
	 * @param css the css file
	 */
	public HtmlMapPageGenerator(String title, String css) {
		super(title, css);
		addToHeaderHtml(createMapScriptHeader());
		addToBodyHtml(HtmlTool.div("", "id=\"map_canvas\" name=\"map_canvas\""));
		addToBodyHtml(HtmlTool.div("", "id=\"menu_canvas\" name=\"menu_canvas\""));
		addToBodyHtml(HtmlTool.div("", "id=\"details_canvas\" name=\"details_canvas\" class=\"overview\""));
		addToBodyHtml(HtmlTool.iframe("","id=\"hidden_frame\" name=\"hidden_frame\""));
	}
}