package model.agent.agents;

import java.util.HashMap;
import java.util.Vector;

import model.agent.Agent;
import model.agent.collection.AgentCollection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import data.index.AgentIndex;

/**
 * Agent for generating stats.
 * 
 * @author Gerben G. Meyer
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
	 * @param agentStorage the storage to be used for this Agent 
	 */
	public StatsAgent(String id) {
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
		int newEntries = 0;
		
		Vector<String> keywords = new Vector<String>(AgentIndex.getInstance().getKeywords());
		for (String keyword: keywords){
			newEntries += AgentIndex.getInstance().searchAgents(keyword).size();
			
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
		detailsPane.addDataRow("#", "Agent types: " + AgentCollection.getInstance().getTypes().size(), "");
		detailsPane.addDataRow("#", "Agent IDs: " + AgentCollection.getInstance().getSize(), "");
		detailsPane.addDataRow("#", "Keywords: " + AgentIndex.getInstance().getKeywords().size(), "");
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