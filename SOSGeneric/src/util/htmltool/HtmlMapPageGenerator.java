package util.htmltool;

import java.util.HashMap;

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

		addToBodyHtml(HtmlTool.createDiv("", "map_canvas"));
		addToBodyHtml(HtmlTool.createDiv("", "menu_canvas"));

		HashMap<String, String> detailsCanvasAttributes = new HashMap<String, String>();
		detailsCanvasAttributes.put("id", "details_canvas");
		detailsCanvasAttributes.put("name", "details_canvas");
		detailsCanvasAttributes.put("class", "overview");

		addToBodyHtml(HtmlTool.createDiv("", detailsCanvasAttributes));
		addToBodyHtml(HtmlTool.createIFrame("hidden_frame", ""));
	}
}