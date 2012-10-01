package grunn.internalagents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

public class HomeAgent extends Agent{

	public HomeAgent(String id) {
		super(id);
		initBool(Agent.HIDDEN, true);
	}
	
	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params){
		//move the map to Groningen
		mapContent.setCenter(53.2198, 6.5965);
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
		detailsPane.addParagraph("This to the web interface of the TAC SCM Agent GRUNN!");
		
		//show all agents by using the always existing search agent to search for all agents
		AgentViewable av = AgentCollection.getInstance().get("search");
		
		detailsPane.addSubHeader("Planners");
		params.put("q", "type:planner");
		av.generateDetailsContent(detailsPane, params);
		
		detailsPane.addSubHeader("Product Types");
		params.put("q", "type:producttype");
		av.generateDetailsContent(detailsPane, params);

		detailsPane.addSubHeader("Component Types");
		params.put("q", "type:componenttype");
		av.generateDetailsContent(detailsPane, params);
		
		detailsPane.addSubHeader("Orders");
		params.put("q", "type:order");
		av.generateDetailsContent(detailsPane, params);
	}

	
}
