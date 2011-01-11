package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

public class BudapestHomeAgent extends Agent {

	public BudapestHomeAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
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
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		//show a welcome message
		detailsPane.addHeader("Welcome!");
		detailsPane.addParagraph("Here, you can get an overview of all the major attractions in Budapest. Furthermore, trainstations, the airport, and even places to stay can be found here.");
		
		//show all attractions by using the always existing search agent to search for all agents
		detailsPane.addSubHeader("The major attractions");
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "type:attraction");
		av.generateDetailsContent(detailsPane, params);
	}

}
