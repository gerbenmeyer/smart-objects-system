package agents;

import model.agent.Agent;
import model.agent.collection.AgentCollectionView;
import model.agent.collection.AgentFactory;
import model.agent.property.PropertiesObject;

public class BudapestAgentFactory extends AgentFactory {

	@Override
	public Agent createAgent(PropertiesObject p, AgentCollectionView acv) {
		return new BudapestObjectAgent(p.getID(),acv);
	}

}
