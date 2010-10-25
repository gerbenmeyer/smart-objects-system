package agents;

import model.agent.Agent;
import model.agent.collection.AgentFactory;

public class BudapestAgentFactory extends AgentFactory {

	@Override
	public Agent createSpecificAgent(String agentID) {
		if (agentID.equals("home")) {
			return new BudapestHomeAgent(agentID);
		} else {
			return new BudapestObjectAgent(agentID);
		}
	}

}
