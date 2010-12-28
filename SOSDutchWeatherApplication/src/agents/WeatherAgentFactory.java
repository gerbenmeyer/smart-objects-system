package agents;

import model.agent.Agent;
import model.agent.collection.AgentFactory;

public class WeatherAgentFactory extends AgentFactory {

	@Override
	protected Agent createSpecificAgent(String agentID) {
		return new WeatherAgent(agentID);
	}
}