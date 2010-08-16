package model.agent.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentView;
import model.agent.execution.AgentsProcessor;
import model.agent.index.AgentIndex;
import model.agent.index.AgentIndexView;
import model.agent.property.PropertiesObject;

/**
 * This class represents a collection of agents.
 * 
 * @author Gijs B. Roest
 */
public final class AgentCollection implements AgentCollectionView {

	private Map<String, Agent> agentsMap = Collections.synchronizedMap(new HashMap<String, Agent>());

	private AgentsProcessor processor;
	private AgentFactory factory;

	private AgentIndex indexer;

	/**
	 * Constructs a new AgentCollection object.
	 * 
	 * @param factory
	 *            an AgentFactory used for creating new agents
	 */
	public AgentCollection(AgentFactory factory) {
		this.factory = factory;
		this.indexer = new AgentIndex(this);
		this.processor = new AgentsProcessor(this, indexer);
	}

	/**
	 * Creates a new agent from a PropertiesObject and adds it to the
	 * collection.
	 * 
	 * @param po
	 *            the PropertiesObject to be added
	 */
	public synchronized void putAgentFromPropertiesObject(PropertiesObject po) {
		if (Settings.getProperty(Settings.PAUSE_AGENT_EXECUTION_WHEN_PUTTING_AGENTS).equals("true")) {
			processor.pause();
		}
		String id = po.getID();
		boolean hasKey = containsKey(id);

		Agent a;
		if (!hasKey) {
			a = factory.createAgent(po, this);
		} else {
			a = agentsMap.get(id);
		}

		a.putProperties(po, this, a, true);

		if (!a.isGarbage()) {
			if (!hasKey) {
				put(a);
			} else {
				indexer.update(a);
			}
		}
	}

	/**
	 * Adds an agent to the collection.
	 * 
	 * @param agent
	 *            the agent to be added to the collection
	 */
	public void put(Agent agent) {
		agentsMap.put(agent.getID(), agent);
		indexer.update(agent);
	}

	/**
	 * True if the collection contains a certain identifier.
	 * 
	 * @param id
	 *            the identifier of an agent
	 * @return true or false
	 */
	public boolean containsKey(String id) {
		return agentsMap.containsKey(id);
	}

	/**
	 * @param id
	 *            the identifier of an agent
	 * @return the AgentView of an agent
	 */
	public AgentView get(String id) {
		return agentsMap.get(id);
	}

	/**
	 * Removes an agent from the collection when it is in it.
	 * 
	 * @param id
	 *            the identifier of an agent
	 */
	public void remove(String id) {
		Agent a = agentsMap.remove(id);
		if (a != null) {
			indexer.remove(a);
		}
	}

	/**
	 * Removes all agents from the collection.
	 */
	protected void clear() {
		agentsMap.clear();
	}

	@Override
	public AgentIndexView getIndex() {
		return indexer;
	}
}