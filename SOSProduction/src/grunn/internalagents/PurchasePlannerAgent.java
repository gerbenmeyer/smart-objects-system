/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import model.agent.Agent;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class PurchasePlannerAgent extends Agent {

	/**
	 * Constructor
	 * 
	 */
	public PurchasePlannerAgent(String id) {
		super(id);
		initText(Agent.TYPE, "Planner");
		initInt("finishedcounter", 0);
	}

	@Override
	public void act() throws Exception {
		while (messages().hasMessage()) {
			GRUNNMessage message = (GRUNNMessage) messages().getMessage();
			
			if (message.getMessageType() == GRUNNMessageType.NEWDAY) {
				// on the first day, create component type agents
				if (!get("initialized").equals(Boolean.toString(true))) {
					for (int i = 0; i < GRUNNEnvironment.getInstance().getComponentCatalog().size(); i++) {
						int productID = GRUNNEnvironment.getInstance().getComponentCatalog().getProductID(i);
						String id = "Component-" + productID;
						GRUNNEnvironment.getInstance().addAgent(new ComponentTypeAgent(id, productID));
					}
					set(PropertyType.BOOLEAN, "initialized", Boolean.toString(true));
				}
				setInt("finishedcounter",0);
			} else if (message.getMessageType() == GRUNNMessageType.OFFERBUNDLE) {
				messages().sendMessageToAgentType("ComponentType", message);
			} else if (message.getMessageType() == GRUNNMessageType.FINISHED) {
				setInt("finishedcounter",getInt("finishedcounter") + 1);
				if (getInt("finishedcounter") >= GRUNNEnvironment.getInstance().getComponentCatalog().size()) {
					GRUNNEnvironment.getInstance().sendSupplierRFQs();
				}
			} else {
				System.err.println("PurchasePlannerAgent: Unknown message received: " + message.getMessageType());
			}
		}

		

	}

}
