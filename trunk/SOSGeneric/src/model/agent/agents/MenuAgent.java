package model.agent.agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import main.Settings;
import model.agent.Agent;
import model.agent.collection.AgentCollection;
import util.Capitalize;
import util.comparators.AgentTypeComparator;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
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
	 * @param id
	 *            the identifier for the agent
	 */
	public MenuAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		List<String> types = AgentCollection.getInstance().getTypes();

		Collections.sort(types, new AgentTypeComparator());
		String menu = HtmlTool
				.h2("Filter <img src=\"hide.png\" title=\"Toggle menu\" width=\"16\" height=\"16\" style=\"float:right;\" onclick=\"if (document.getElementById('menu_content').style.display == 'none') { document.getElementById('menu_content').style.display = 'inline'; this.src = 'hide.png'; } else { document.getElementById('menu_content').style.display = 'none'; this.src = 'show.png'; }\">")
				+ "<div id=\"menu_content\">";

		boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true));

		if (Settings.getProperty(Settings.SHOW_ALL_OBJECTS).equals("true")) {
			// FIXME not generic, as all_icon_menu.png is not available in the
			// SOSGeneric project
			if (showStatus) {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.map?q='"
						+ " + document.getElementById('overview_filter').value;\"><div class=\"propertyicon\">" + HtmlTool.img("all_icon_menu.png", "none")
						+ "</div><div class=\"menu_name\">All</div></div>";
			} else {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.map?q=';\">"
						+ "<div class=\"propertyicon\">" + HtmlTool.img("all_icon_menu.png", "none") + "</div><div class=\"menu_name\">All</div></div>";
			}
		}

		for (String type : types) {
			if (showStatus) {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.map?q=type:"
						+ type.toLowerCase() + "%20' + document.getElementById('overview_filter').value;\"><div class=\"propertyicon\">"
						+ HtmlTool.img( type.toLowerCase() + "_icon_menu.png", type.toLowerCase()) + "</div><div class=\"menu_name\">"
						+ Capitalize.capitalize(type) + "</div></div>";
			} else {
				menu += "<div class=\"property linked_property\" onclick=\"document.getElementById('hidden_frame').src = 'search.map?q=type:"
						+ type.toLowerCase() + "';\"><div class=\"propertyicon\">" + HtmlTool.img( type.toLowerCase() + "_icon_menu.png", type.toLowerCase())
						+ "</div><div class=\"menu_name\">" + Capitalize.capitalize(type) + "</div></div>";
			}
		}

		boolean clustering = Settings.getProperty(Settings.DEFAULT_CLUSTERING).equals(Boolean.toString(true));
		String defaultFilter = "";// Settings.getProperty(Settings.DEFAULT_AGENT);

		if (clustering || showStatus) {
			menu += HtmlTool.h2("Options");
		}

		if (clustering) {
			menu += "<input id=\"clustering_enabled\" type=\"checkbox\" onclick=\"setClustering(this.checked)\" " + (clustering ? "checked=\"checked\"" : "")
					+ "><label for=\"clustering_enabled\">Cluster " + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "s</label></input><br/>";
		}

		if (showStatus) {
			menu += "<div class=\"filter_text\">Display:</div>"
					+ "<select id=\"overview_filter\" onchange=\"document.getElementById('hidden_frame').src = 'search.map?q=type:'+document.getElementById('hidden_frame').contentDocument.URL.match(/type:([\\w]+)/)[1]+' '+this.value;\">"
					+ " <option value=\"\">Everything</option>" + " <option value=\"status:" + AgentStatus.ERROR.toString() + "\" "
					+ (defaultFilter.contains("status:" + AgentStatus.ERROR.toString() + "") ? "selected=\"selected\"" : "") + ">Errors</option>"
					+ " <option value=\"status:" + AgentStatus.WARNING.toString() + "\" "
					+ (defaultFilter.contains("status:" + AgentStatus.WARNING.toString() + "") ? "selected=\"selected\"" : "") + ">Warnings</option>"
					+ " <option value=\"status:" + AgentStatus.UNKNOWN.toString() + "\" "
					+ (defaultFilter.contains("status:" + AgentStatus.UNKNOWN.toString() + "") ? "selected=\"selected\"" : "") + ">Unknowns</option>"
					+ "</select>";
		}

		menu += "</div>";

		detailsPane.add(menu);
	}

}