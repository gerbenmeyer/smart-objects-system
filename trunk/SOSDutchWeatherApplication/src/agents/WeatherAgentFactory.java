package agents;

import model.agent.Agent;
import model.agent.collection.AgentFactory;

public class WeatherAgentFactory extends AgentFactory {

	@Override
	protected Agent createSpecificAgent(String agentID) {
		if (agentID.equals("home")) {
			return new WeatherHomeAgent(agentID);
		} else {
			return new WeatherAgent(agentID);
		}
	}
}