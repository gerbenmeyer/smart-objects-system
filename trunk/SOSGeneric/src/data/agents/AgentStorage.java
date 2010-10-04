package data.agents;

import java.util.HashMap;
import java.util.Set;

import model.agent.property.Property;

public abstract class AgentStorage {
	
	private static AgentStorage instance;
	
	public abstract Property getProperty(String id, String name);
	
	public abstract String getPropertyValue(String id, String name);
	
	public abstract HashMap<String, Property> getProperties(String id);
	
	public abstract Set<String> getPropertiesKeySet(String id);
	
	public abstract void putProperty(String id, Property p);
	
	public abstract void putProperties(String id, HashMap<String, Property> properties);
	
	public abstract void removeProperty(String id, String name);
	
	public abstract boolean delete(String id);
	
	public static AgentStorage getInstance(){
		return instance;
	}
	
	public static void setInstance(AgentStorage agentStorage){
		instance = agentStorage;
	}

}