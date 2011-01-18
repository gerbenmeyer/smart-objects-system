package agents;

import java.util.Map;

import model.agent.Agent;
import model.agent.collection.AgentFactory;
import model.agent.property.Property;

public class BudapestAgentFactory extends AgentFactory {

	@Override
	protected Agent createSpecificAgent(Map<String, Property> properties) {
		String agentID = properties.get(Agent.ID).toString();
		String agentType = "";
		if (properties.containsKey(Agent.TYPE)){
			agentType = properties.get(Agent.TYPE).toString();
		}
		if (agentID.equals("home")) {
			return new BudapestHomeAgent(agentID);
		} else if (!agentType.isEmpty()){
			return new BudapestObjectAgent(agentID);
		}
		return null;
	}


}
