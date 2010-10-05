package model.agent.agents;

import java.util.HashMap;
import java.util.Vector;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import model.agent.property.properties.LocationProperty;
import util.BenchMarker;
import util.Capitalize;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;
import data.index.AgentIndex;

/**
 * Agent for searching and generating search data.
 * 
 * @author Gerben G. Meyer
 *
 */
public class SearchAgent extends Agent {

	/**
	 * Constructs a new SearchAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 * @param agentStorage the storage to be used for this Agent
	 */
	public SearchAgent(String id) {
		super(id);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		BenchMarker bm = new BenchMarker("SearchAgent PaneContent");
		
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}
		String title = search.replace('+', ' ');
		title = title.replace("type:", "");
		title = title.replace("status:", "");

		title = "Search: " + title;

		detailsPane.addHeader(title);
		
		bm.start();

		Vector<String> ids = AgentIndex.getInstance().searchAgents(search.replace('+', ' '));
		
		bm.taskFinished("Fetching agent IDs");
		
		if (ids.size() > 10000){
			detailsPane.addParagraph(HtmlTool.createImage("warning.png", "Warning")+" Too many agents, only showing first 10,000.");
		}
		while (ids.size() > 10000){
			ids.remove(ids.size()-1);
		}

		detailsPane.addDataHeader("", Capitalize.capitalize(Settings.getProperty(Settings.KEYWORD_DEEPLINK)));

		boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(
				Boolean.toString(true));
		
		for (String id : ids) {
			AgentViewable pov = AgentCollection.getInstance().get(id);
			if (pov == null) {
				//TODO: This is just here for debugging purposes
				detailsPane.addDataRow("unknown.png", "Agent not found!", "");
				continue;
			}

			String statusIcon = showStatus ? pov.getStatus().toString().toLowerCase() + ".png" : "";

			detailsPane.addDataRowLink(pov.getIcon(), pov.get(Agent.LABEL), statusIcon, pov.getID()
					+ ".html");
		}
		bm.taskFinished("Iterating agents");
		bm.stop();
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		BenchMarker bm = new BenchMarker("SearchAgent MapContent");
		
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}

		mapContent.clearMapContent();
		mapContent.clearMapData();
		
		bm.start();
		
		Vector<String> ids = AgentIndex.getInstance().searchAgents(search.replace('+', ' '));
		
		bm.taskFinished("Fetching agent IDs");
		
		for (int i = 0; i < Math.min(ids.size(), 10000); i++) {
			AgentViewable av = AgentCollection.getInstance().get(ids.get(i));
			if (av == null) {
				continue;
			}

			String location = av.get(Agent.LOCATION);
			if (!location.isEmpty()) {
				LocationProperty lp = new LocationProperty("", location);
				lp.setAgentView(av);
				lp.toScript(mapContent, params);
			}
		}
		bm.taskFinished("Iterating agents");
		bm.stop();
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