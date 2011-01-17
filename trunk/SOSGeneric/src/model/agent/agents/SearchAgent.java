package model.agent.agents;

import java.util.HashMap;
import java.util.List;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import util.BenchMarker;
import util.db.MySQLConnection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

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
	 */
	
	public final static int MAX_AGENTS = Integer.MAX_VALUE-1;
	
	public SearchAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}
	
	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		BenchMarker bm = new BenchMarker("SearchAgent PaneContent", false);
		MySQLConnection.getInstance().resetCounter();
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}
		
		if (detailsPane.isEmpty()){
			String title = search.replace('+', ' ');
			title = title.replace("type:", "");
			title = title.replace("status:", "");
			if (title.trim().isEmpty()){
				title = "everyting";
			}
			title = "Search: " + title.toLowerCase();
			detailsPane.addHeader(title);
		}
		
		bm.start();

		List<AgentViewable> agents = AgentCollection.getInstance().searchAgents(search.replace('+', ' '));
		
		if (agents.size() > MAX_AGENTS){
			detailsPane.addParagraph(HtmlTool.createImage("warning.png", "Warning")+" Too many "+Settings.getProperty(Settings.KEYWORD_DEEPLINK)+"s, only showing first "+MAX_AGENTS+".");
		}

		boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(
				Boolean.toString(true));

		bm.taskFinished("Fetching agents ("+MySQLConnection.getInstance().getCounter()+" queries)" );
		boolean smallDetailsPane = Settings.getProperty(Settings.SHOW_SMALL_DETAILS_PANE).equals("true");
		for (int i = 0; i < Math.min(agents.size(), MAX_AGENTS); i++) {
			AgentViewable av = agents.get(i);
			String statusIcon = showStatus ? av.getStatus().toString().toLowerCase() + ".png" : "";
			if (smallDetailsPane){
				detailsPane.addDataRowLink(av.getIcon(), av.get(Agent.LABEL), statusIcon, av.getID()
					+ ".map");
			} else {
				detailsPane.addDataRowLink(av.getIcon(), av.get(Agent.LABEL), av.get(Agent.DESCRIPTION), 
					statusIcon, av.getID() + ".map");
			}
		}
		bm.taskFinished("Iterating agents ("+MySQLConnection.getInstance().getCounter()+" queries)" );
		bm.stop();
	}
	
	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		BenchMarker bm = new BenchMarker("SearchAgent MapContent", false);
		MySQLConnection.getInstance().resetCounter();
		String search = "";
		if (params.containsKey("q")) {
			search = params.get("q");
		}

		mapContent.clearMapContent();
		mapContent.clearMapData();
		
		bm.start();
		
		List<AgentViewable> agents = AgentCollection.getInstance().searchAgents(search.replace('+', ' '));
		bm.taskFinished("Fetching agents ("+MySQLConnection.getInstance().getCounter()+" queries)" );
		
		for (int i = 0; i < Math.min(agents.size(), MAX_AGENTS); i++) {
			AgentViewable av = agents.get(i);
			mapContent.addMapMarker(av);
//			mapContent.addMapBalloon(av, false);
		}
		
		bm.taskFinished("Iterating agents ("+MySQLConnection.getInstance().getCounter()+" queries)" );
		bm.stop();
		mapContent.drawMap();
	}

}