package model.agent.agents.index;

import java.util.HashMap;

import main.Settings;
import model.agent.agents.IndexAgent;
import util.htmltool.HtmlMapPageGenerator;
import util.htmltool.HtmlTool;

/**
 * This agent provides methods for generating an index page for normal (desktop) devices.
 * 
 * @author Gerben G., Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 * 
 */
public class NormalIndexAgent extends IndexAgent {

	/**
	 * Constructs a new NormalIndexAgent object.
	 * 
	 * @param id the identifier for the agent
	 */
	public NormalIndexAgent(String id) {
		super(id);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
		System.out.println("Indexagent: I am dying!!! ARGH!!!");
	}

	@Override
	public StringBuffer generatePage(HashMap<String, String> params) {
		HtmlMapPageGenerator htmlPage = new HtmlMapPageGenerator(Settings.getProperty(Settings.APPLICATION_NAME),
				"main.css");

		String header = HtmlTool.createImageLeft(Settings.getProperty(Settings.APPLICATION_ICON), "logo");
		header += HtmlTool.createHeader1(HtmlTool.createLink(Settings.getProperty(Settings.DEFAULT_SCRIPT), Settings.getProperty(Settings.APPLICATION_NAME), "hidden_frame"));
		header += HtmlTool.createHeader2(Settings.getProperty(Settings.APPLICATION_VERSION));

		String search = "";
		search += "<form style=\"float:right;\" action=\"search.html\" method=\"get\" target=\"hidden_frame\">";
		search += "<input type=\"text\" name=\"q\" />";
		search += "&nbsp;";
		search += "<input type=\"image\" src=\"search.png\" alt=\"Search\" style=\"vertical-align:middle\"/>";
		search += "</form>";

		htmlPage.addToBodyHtml(HtmlTool.createDiv(search + header, "header_canvas"));

		htmlPage.addToFinalScript("ajaxpage('menu_details.html','menu_canvas');");

		// default source
		String source = Settings.getProperty(Settings.DEFAULT_SCRIPT);

		// deeplink to id.
		if (params != null && params.containsKey(Settings.getProperty(Settings.KEYWORD_DEEPLINK))
				&& params.get(Settings.getProperty(Settings.KEYWORD_DEEPLINK)) != null) {
			source = params.get(Settings.getProperty(Settings.KEYWORD_DEEPLINK)) + ".html?deeplink=true";
		}
		htmlPage.addToOnLoadScript("document.getElementById('hidden_frame').src='" + source + "';");

		// clustering enabled / disabled.
		if (Settings.getProperty(Settings.DEFAULT_CLUSTERING).equals(Boolean.toString(true))) {
			htmlPage.addToOnLoadScript("setClustering(true);");
		}

		// show details enabled / disabled.
		htmlPage.addToOnLoadScript(("parent.setDetailsSize(" + Settings.getProperty(Settings.SHOW_SMALL_DETAILS_PANE).equals("true")) + ");\n");

		// HtmlTool.outputHTML(Settings.HTML_DATA_DIR + "index.html", html);
		return HtmlTool.createHTML(htmlPage.getHtml());
	}
}