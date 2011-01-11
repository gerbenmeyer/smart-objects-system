package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

public class WeatherHomeAgent extends Agent {

	public WeatherHomeAgent(String id) {
		super(id);
		// make sure that this agent is hidden, i.e. that it does not show up in the search results
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params){
		//move the map to The Netherlands
		mapContent.setCenter(52, 6.5);
		mapContent.setZoom(7);
		
		//show all agents by using the always existing search agent to search for all agents
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "");
		av.generateMapContent(mapContent, params);
	}

	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		//show a welcome message
		detailsPane.addHeader("Welcome!");
		detailsPane.addParagraph("Here, you can get an overview of weather in The Netherlands.");
		
		//show all weather stations by using the always existing search agent to search for all agents
		detailsPane.addSubHeader("Weather stations");
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "");
		av.generateDetailsContent(detailsPane, params);
	}

}
