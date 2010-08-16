package model.agent.agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import main.Settings;
import model.agent.Agent;
import model.agent.collection.AgentCollectionView;
import util.Capitalize;
import util.comparators.AgentTypeComparator;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

/**
 * Agent for generating the menu.
 * 
 * @author Gerben G. Meyer
 *
 */
public class MenuAgent extends Agent {

	/**
	 * Constructs a new MenuAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 */
	public MenuAgent(String id, AgentCollectionView pocv) {
		super(id, pocv);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		Vector<String> types = getAgentCollectionView().getIndex().getAgentTypes();

		Collections.sort(types, new AgentTypeComparator());
		String menu = HtmlTool
				.createHeader2("Menu <img src=\"hide.png\" title=\"Toggle menu\" width=\"16\" height=\"16\" style=\"float:right;\" onclick=\"if (document.getElementById('menu_content').style.display == 'none') { document.getElementById('menu_content').style.display = 'inline'; this.src = 'hide.png'; } else { document.getElementById('menu_content').style.display = 'none'; this.src = 'show.png'; }\">")
				+ "<div id=\"menu_content\">"
				+ "<div class=\"propertyheader\"><div class=\"propertyicon\"></div><div class=\"propertyname\">"
				+ Capitalize.capitalize(Settings.getProperty(Settings.KEYWORD_DEEPLINK)) + " types</div></div>";

		boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(
				Boolean.toString(true));

		for (String type : types) {
			if (showStatus) {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.html?q=type:"
						+ type.toLowerCase()
						+ " ' + document.getElementById('overview_filter').value;\"><div class=\"propertyicon\">"
						+ HtmlTool.createImage(type.toLowerCase() + "_icon_menu.png", type.toLowerCase(), 16)
						+ "</div><div class=\"menu_name\">" + Capitalize.capitalize(type) + "</div></div>";
			} else {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.html?q=type:"
						+ type.toLowerCase()
						+ "';\"><div class=\"propertyicon\">"
						+ HtmlTool.createImage(type.toLowerCase() + "_icon_menu.png", type.toLowerCase(), 16)
						+ "</div><div class=\"menu_name\">" + Capitalize.capitalize(type) + "</div></div>";
			}
		}
		
		String defaultClustering = Settings.getProperty(Settings.DEFAULT_CLUSTERING);
		String defaultScript = Settings.getProperty(Settings.DEFAULT_SCRIPT);

		menu += HtmlTool.createHeader2("Options");
		menu += "<input id=\"clustering_enabled\" type=\"checkbox\" onclick=\"setClustering(this.checked)\" "
				+ (defaultClustering.equals("true") ? "checked=\"checked\"" : "") + ">Cluster "
				+ Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "s</input><br/>";

		if (showStatus) {
			menu += "<div class=\"filter_text\">Display:</div>"
					+ "<select id=\"overview_filter\" onchange=\"document.getElementById('hidden_frame').src = 'search.html?q=type:'+document.getElementById('hidden_frame').contentDocument.URL.match(/type:([\\w]+)/)[1]+' '+this.value;\">"
					+ " <option value=\"\">Everything</option>" + " <option value=\"status:error\" "
					+ (defaultScript.contains("status:error") ? "selected=\"selected\"" : "") + ">Errors</option>"
					+ " <option value=\"status:warning\" "
					+ (defaultScript.contains("status:warning") ? "selected=\"selected\"" : "") + ">Warnings</option>"
					+ " <option value=\"status:unknown\" "
					+ (defaultScript.contains("status:unknown") ? "selected=\"selected\"" : "") + ">Unknowns</option>"
					+ "</select>";
		}
		
		menu += "<div class=\"filter_text\">"+HtmlTool.createLink("stats.html", "Stats", "hidden_frame")+"</div>";

		menu += "</div>";

		detailsPane.addCustomHtml(menu);
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
	}

	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
		System.out.println("Menuagent: I am dying!!! ARGH!!!");
	}
}