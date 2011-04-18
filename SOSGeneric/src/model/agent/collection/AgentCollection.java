package model.agent.collection;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.agents.SearchAgent;
import model.agent.execution.AgentsProcessor;
import model.agent.property.Property;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import data.agents.AgentCollectionStorage;

/**
 * This class represents all agents available to the application.
 * 
 * @author W.H. Mook
 */
public class AgentCollection implements AgentCollectionMutable {

	/**
	 * The instance.
	 */
	private static AgentCollection instance;

	private AgentFactory factory;

	/**
	 * Constructs a new AgentCollection object.
	 * An AgentFactory is needed to reconstruct agents from the storage. 
	 * 
	 * @param factory the factory
	 */
	public AgentCollection(AgentFactory factory) {
		super();
		this.factory = factory;
		instance = this;
	}

	/**
	 * Get the instance of AgentCollectionViewable for this application.
	 * 
	 * @return the instance
	 */
	public static AgentCollectionViewable getInstance() {
		return instance;
	}
	
	public boolean containsKey(String id) {
		return AgentCollectionStorage.getInstance().containsKey(id);
	}

	public AgentViewable get(String id) {
		Agent agent = null;
		Map<String,Property> properties = AgentCollectionStorage.getInstance().get(id);
		if (!properties.isEmpty()) {
			agent = factory.createAgent(properties);
		}
		return agent;
	}

	public List<AgentViewable> get(List<String> ids) {
		List<AgentViewable> agents = new Vector<AgentViewable>();
		List<Map<String,Property>> list = AgentCollectionStorage.getInstance().get(ids);
		
		for(Map<String,Property> properties : list) {
			if (!properties.isEmpty()) {
				Agent agent = factory.createAgent(properties);
				agents.add(agent);
			}
		}
		return agents;
	}

	public void put(Agent agent) {
		if (Settings.getProperty(Settings.PAUSE_AGENT_EXECUTION_WHEN_PUTTING_AGENTS).equals("true")) {
			AgentsProcessor processor = AgentsProcessor.getInstance();
			if (processor != null){
				processor.pause();
			}
		}
		if (!agent.isGarbage()) {
			boolean showStatus = Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(
					Boolean.toString(true));
			if (showStatus){
				agent.set(PropertyType.STATUS, Agent.STATUS, AgentStatus.UNKNOWN.toString());
			}
			try {
				agent.actAndSave();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
	
	public List<AgentViewable> searchAgents(String search) {
		return searchAgents(search, "ORDER BY id", SearchAgent.MAX_AGENTS);
	}
	
	public List<AgentViewable> searchAgents(String search, String sort, int limit) {
		List<AgentViewable> agents = new Vector<AgentViewable>();
		List<Map<String,Property>> list = AgentCollectionStorage.getInstance().searchAgents(search,sort,limit);
		
		for(Map<String,Property> properties : list) {
			if (!properties.isEmpty()) {
				Agent agent = factory.createAgent(properties);
				agents.add(agent);
			}
		}
		return agents;
	}

	public boolean delete(Agent agent) {
		return AgentCollectionStorage.getInstance().delete(agent.getID());
	}
}