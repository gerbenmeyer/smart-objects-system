package model.agent.agents;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import util.clientconnection.HTTPListener;
import util.enums.PropertyType;
import util.htmltool.HtmlMapPageGenerator;
import util.htmltool.HtmlTool;

/**
 * This agent provides methods for generating an index page for normal (desktop) devices.
 * 
 * @author Gerben G., Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 * 
 */
public class IndexAgent extends Agent {

	/**
	 * Constructs a new NormalIndexAgent object.
	 * 
	 * @param id the identifier for the agent
	 */
	public IndexAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	public StringBuffer generatePage(HashMap<String, String> params) {
		HtmlMapPageGenerator htmlPage = new HtmlMapPageGenerator(Settings.getProperty(Settings.APPLICATION_NAME),
				"main.css");

		String header = HtmlTool.createLink(Settings.getProperty(Settings.DEFAULT_AGENT)+".map", HtmlTool.createImage(Settings.getProperty(Settings.APPLICATION_ICON), Settings.getProperty(Settings.APPLICATION_NAME)), "hidden_frame");

		String searchForm = "";
		searchForm += "<form style=\"margin:8px 0px;\" action=\"search.map\" method=\"get\" target=\"hidden_frame\">";
		searchForm += "<input type=\"text\" name=\"q\" />";
		searchForm += "&nbsp;";
		searchForm += "<input type=\"image\" src=\"search.png\" alt=\"Search\" style=\"vertical-align:middle\"/>";
		searchForm += "</form>";
		
		String versionParagraph = HtmlTool.createParagraph(Settings.getProperty(Settings.APPLICATION_VERSION)); 
			
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("style", "float:right;text-align:right;");
		
		String right = HtmlTool.createDiv(searchForm + versionParagraph, attributes );

		htmlPage.addToBodyHtml(HtmlTool.createDiv(right + header, "header_canvas"));

		htmlPage.addToFinalScript("ajaxpage('menu.details','menu_canvas');");

		// default source
		String source = Settings.getProperty(Settings.DEFAULT_AGENT)+".map?"+HTTPListener.encodeQuery(params);

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