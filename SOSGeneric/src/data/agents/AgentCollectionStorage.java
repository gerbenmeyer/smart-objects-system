package data.agents;

import java.util.List;

public abstract class AgentCollectionStorage {
	
	private static AgentCollectionStorage instance;
	
	public abstract boolean containsKey(String id);
	
	public abstract int getSize();

	public abstract List<String> getTypes();
	
	public abstract List<String> getIDs();

	public static AgentCollectionStorage getInstance(){
		return instance;
	}
	
	public static void setInstance(AgentCollectionStorage agentCollectionStorage){
		instance = agentCollectionStorage;
	}
	
}