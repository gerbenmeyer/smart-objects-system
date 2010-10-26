package agents;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

public class BudapestHomeAgent extends Agent {

	public BudapestHomeAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	public void act() throws Exception {
		//no acting needed for this object
	}

	public void lastWish() {
		//no last wish needed for this object
	}

	@Override
	public boolean isGarbage() {
		//this agent is never garbage
		return false;
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params){
		//move the map to Budapest
		mapContent.setCenter(47.5, 19.07);
		mapContent.setZoom(13);
		//show all agents by using the always existing search agent to search for all agents
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "");
		av.generateMapContent(mapContent, params);
	}


	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		//show a welcome message
		detailsPane.addHeader("Welcome to the "+Settings.getProperty(Settings.APPLICATION_NAME));
		detailsPane.addParagraph("Here, you can get an overview of all the major attractions in Budapest. Furthermore, trainstations, the airport, and great places to stay can also be found here.");
		
		//show all attracctions by using the always existing search agent to search for all agents
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "type:attraction");
		av.generateDetailsPaneContent(detailsPane, params);
	}


}
