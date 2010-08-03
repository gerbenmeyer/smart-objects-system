package model.agent.agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.collection.AgentCollectionView;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

/**
 * This abstract agent must be extended by agents which generate an index page.
 * 
 * @author Gerben G., Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 * 
 */
public abstract class IndexAgent extends Agent {

	/**
	 * Constructs a new IndexAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 */
	public IndexAgent(String id, AgentCollectionView pocv) {
		super(id, pocv);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params){
		
	}

	/**
	 * Generates a HTML page.
	 * 
	 * @param params the parameters for the request
	 * @return the HTML page
	 */
	public abstract StringBuffer generatePage(HashMap<String, String> params);

	
	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
		System.out.println("Indexagent: I am dying!!! ARGH!!!");
	}
}