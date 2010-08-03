package model.agent.agents;

import java.util.HashMap;
import java.util.Vector;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import model.agent.property.properties.LocationProperty;
import util.Capitalize;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

/**
 * Agent for searching and generating search data.
 * 
 * @author G.G. Meyer
 *
 */
public class SearchAgent extends Agent {

	/**
	 * Constructs a new SearchAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 */
	public SearchAgent(String id, AgentCollectionView pocv) {
		super(id, pocv);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}
		String title = search.replace('+', ' ');
		title = title.replace("type:", "");
		title = title.replace("status:", "");

		title = "Search: " + title;

		detailsPane.addHeader(title);

		Vector<String> ids = getAgentCollectionView().getIndex().searchAgents(search.replace('+', ' '));
		
		if (ids.size() > 10000){
			detailsPane.addParagraph(HtmlTool.createImage("warning.png", "Warning")+" Too many agents, only showing first 10,000.");
		}
		while (ids.size() > 10000){
			ids.remove(ids.size()-1);
		}

		detailsPane.addTableHeader("", Capitalize.capitalize(Settings.getProperty(Settings.KEYWORD_DEEPLINK)));

		boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(
				Boolean.toString(true));

		for (String id : ids) {
			AgentView pov = getAgentCollectionView().get(id);
			if (pov == null) {
				//TODO: This is just here for debugging purposes
				detailsPane.addTableRow("unknown.png", "Agent not found!", "");
				continue;
			}
			if (pov.getType().equals("")) {
				continue;
			}
			boolean hidden = pov.isHidden();
			if (hidden) {
				continue;
			}

			String statusIcon = showStatus ? pov.getStatus().toString().toLowerCase() + ".png" : "";

			detailsPane.addTableRowLink(pov.getIcon(), pov.getLabel(), statusIcon, pov.getID()
					+ ".html");
		}
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}

		mapContent.clearMapContent();
		mapContent.clearMapData();

		Vector<String> ids = getAgentCollectionView().getIndex().searchAgents(search.replace('+', ' '));
		while (ids.size() > 10000){
			ids.remove(ids.size()-1);
		}
		for (String id : ids) {
			AgentView pov = getAgentCollectionView().get(id);
			if (pov == null) {
				continue;
			}
			if (pov.getType().equals("")) {
				continue;
			}
			boolean hidden = pov.getPropertyValue("Hidden").equals(Boolean.toString(true));
			if (hidden) {
				continue;
			}

			String location = pov.getLocation();
			if (!location.isEmpty()) {
				LocationProperty lp = new LocationProperty("", location);
				lp.setAgentCollectionView(getAgentCollectionView());
				lp.setAgentView(pov);
				lp.toScript(mapContent, params);
			}
		}

		mapContent.drawMap();
	}

	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
		System.out.println("Overviewagent: I am dying!!! ARGH!!!");
	}
}