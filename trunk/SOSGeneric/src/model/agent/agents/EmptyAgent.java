package model.agent.agents;

import java.util.HashMap;

import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import model.agent.Agent;
import model.agent.collection.AgentCollectionView;

/**
 * This is a useless agent, without any behaviour.
 * 
 * @author Gerben G. Meyer, Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 * 
 */
public class EmptyAgent extends Agent {

	/**
	 * Constructs a new EmptyAgent object.
	 * 
	 * @param id the identifier for the agent
	 * @param pocv the collectionView for (read) access to other agents
	 */
	public EmptyAgent(String id, AgentCollectionView pocv) {
		super(id, pocv);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
	}

	@Override
	public void generateDetailsPaneContent(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params) {
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent,
			HashMap<String, String> params) {
	}
}