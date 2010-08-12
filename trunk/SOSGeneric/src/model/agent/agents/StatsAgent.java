package model.agent.agents;

import java.util.HashMap;
import java.util.Vector;

import model.agent.Agent;
import model.agent.collection.AgentCollectionView;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

/**
 * Agent for generating stats.
 * 
 * @author G.G. Meyer
 *
 */
public class StatsAgent extends Agent {
	
	private int entries = 0;
	private double executionWaitTime = 0;
	
	private long lastExecutionMillis = 0;
	

	/**
	 * Constructs a new StatsAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 */
	public StatsAgent(String id, AgentCollectionView pocv) {
		super(id, pocv);
	}

	@Override
	public void act() throws Exception {
		int newEntries = 0;
		
		Vector<String> keywords = new Vector<String>(getAgentCollectionView().getIndex().getKeywords());
		for (String keyword: keywords){
			newEntries += getAgentCollectionView().getIndex().searchAgents(keyword).size();
			
		}
		entries = newEntries;
		
		if (lastExecutionMillis == 0){
			lastExecutionMillis = System.currentTimeMillis();
		}
		
		executionWaitTime = (System.currentTimeMillis() - lastExecutionMillis) / 1000.0;
		
		lastExecutionMillis = System.currentTimeMillis();
		
		
	}

	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		detailsPane.addHeader("Statistics");
		detailsPane.addSubHeader("Index");
		detailsPane.addDataHeader("", "Property");
		detailsPane.addDataRow("#", "Agent types: " + getAgentCollectionView().getIndex().getAgentTypes().size(), "");
		detailsPane.addDataRow("#", "Agent IDs: " + getAgentCollectionView().getIndex().getAgentIDs().size(), "");
		detailsPane.addDataRow("#", "Keywords: " + getAgentCollectionView().getIndex().getKeywords().size(), "");
		detailsPane.addDataRow("#", "Entries: " + entries, "");
		detailsPane.addDataRow("", "Act: " + executionWaitTime+" s", "");
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
		System.out.println("Statsagent: I am dying!!! ARGH!!!");
	}
}