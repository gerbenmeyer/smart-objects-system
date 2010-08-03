package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

public class BudapestHomeAgent extends Agent {

	public BudapestHomeAgent(String id, AgentCollectionView agentCollectionView) {
		super(id, agentCollectionView);
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
		AgentView av = getAgentCollectionView().get("search");
		params.put("q", "type:all");
		av.generateMapContent(mapContent, params);
	}


	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		//show all agents by using the always existing search agent to search for all agents
		AgentView av = getAgentCollectionView().get("search");
		params.put("q", "type:all");
		av.generateDetailsPaneContent(detailsPane, params);
	}


}
