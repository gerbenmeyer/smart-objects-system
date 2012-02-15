package grunn.world;

import grunn.internalagents.ComponentTypeAgent;
import grunn.internalagents.HomeAgent;
import grunn.internalagents.OrderAgent;
import grunn.internalagents.ProductTypeAgent;
import grunn.internalagents.ProductionPlannerAgent;
import grunn.internalagents.PurchasePlannerAgent;
import grunn.internalagents.SalesPlannerAgent;
import grunn.internalagents.ShipmentPlannerAgent;

import java.util.Map;

import model.agent.Agent;
import model.agent.collection.AgentFactory;
import model.agent.property.Property;

public class GRUNNAgentFactory extends AgentFactory {

	@Override
	protected Agent createSpecificAgent(Map<String, Property> properties) {
		String agentID = properties.get(Agent.ID).toString();
		String agentType = "";
		if (properties.containsKey(Agent.TYPE)){
			agentType = properties.get(Agent.TYPE).toString();
		}
		if (agentID.equals("home")) {
			return new HomeAgent(agentID);
		} 
		if (agentID.equals("ShipmentPlanner")){
			return new ShipmentPlannerAgent(agentID);
		}
		if (agentID.equals("ProductionPlanner")){
			return new ProductionPlannerAgent(agentID);
		}		
		if (agentID.equals("SalesPlanner")){
			return new SalesPlannerAgent(agentID);
		}			
		if (agentID.equals("PurchasePlanner")){
			return new PurchasePlannerAgent(agentID);
		}	
		if (agentType.equals("ComponentType")){
			return new ComponentTypeAgent(agentID);
		}	
		if (agentType.equals("ProductType")){
			return new ProductTypeAgent(agentID);
		}		
		if (agentType.equals("Order")){
			return new OrderAgent(agentID);
		}			
		return null;
	}


}
