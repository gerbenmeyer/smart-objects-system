package model.agent.collection;

import java.util.List;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.execution.AgentsProcessor;
import data.agents.AgentCollectionStorage;

public class AgentCollection implements AgentCollectionMutable {

	private static AgentCollection instance;

	private AgentFactory factory;

	public AgentCollection(AgentFactory factory) {
		super();
		this.factory = factory;
		instance = this;
	}

	public static AgentCollectionViewable getInstance() {
		return instance;
	}

	public boolean containsKey(String id) {
		return AgentCollectionStorage.getInstance().containsKey(id);
	}

	public AgentViewable get(String id) {
		Agent agent = null;
		if (containsKey(id)) {
			agent = factory.createAgent(id);
		}
		return agent;
	}

	public void put(Agent agent) {
		if (Settings.getProperty(Settings.PAUSE_AGENT_EXECUTION_WHEN_PUTTING_AGENTS).equals("true")) {
			AgentsProcessor processor = AgentsProcessor.getInstance();
			if (processor != null){
				processor.pause();
			}
		}
		if (!agent.isGarbage()) {
			agent.save();
		}
	}

	public int getSize() {
		return AgentCollectionStorage.getInstance().getSize();
	}

	public List<String> getTypes() {
		return AgentCollectionStorage.getInstance().getTypes();
	}

	public List<String> getIDs() {
		return AgentCollectionStorage.getInstance().getIDs();
	}

}