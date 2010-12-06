package model.agent.agents;

import model.agent.Agent;

/**
 * This is a useless agent, without any behaviour.
 * 
 * @author Gerben G. Meyer, Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 */
public class EmptyAgent extends Agent {

	/**
	 * Constructs a new EmptyAgent object.
	 * 
	 * @param id the identifier for the agent
	 */
	public EmptyAgent(String id) {
		super(id);
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

}