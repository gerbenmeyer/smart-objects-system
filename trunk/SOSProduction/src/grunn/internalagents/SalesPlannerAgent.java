/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import model.agent.Agent;
import model.messageboard.MessageBoard;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class SalesPlannerAgent extends Agent {

	/**
	 * Constructor
	 * 
	 */
	public SalesPlannerAgent(String id) {
		super(id);
		initText(Agent.TYPE, "Planner");
		initInt("finishedcounter", 0);
	}

	@Override
	public void act() throws Exception {
		while (MessageBoard.getInstance().hasMessage(get(ID))) {
			GRUNNMessage message = (GRUNNMessage) MessageBoard.getInstance().getMessage(get(ID));

			if (message.getMessageType() == GRUNNMessageType.NEWDAY) {
				// on the first day, create product type agents
				if (!get("initialized").equals(Boolean.toString(true))) {
					for (int i = 0; i < GRUNNEnvironment.getInstance().getBOMBundle().size(); i++) {
						int productID = GRUNNEnvironment.getInstance().getBOMBundle().getProductID(i);
						String id = "Product-" + productID;
						GRUNNEnvironment.getInstance().addAgent(new ProductTypeAgent(id, productID));
						MessageBoard.getInstance().registerAgent(id);
						MessageBoard.getInstance().sendMessage(id, new GRUNNMessage(get(ID), GRUNNMessageType.NEWDAY));
					}
					set(PropertyType.BOOLEAN, "initialized", Boolean.toString(true));
				}
				setInt("finishedcounter", 0);
			} else if (message.getMessageType() == GRUNNMessageType.FINISHED) {
				setInt("finishedcounter", getInt("finishedcounter") + 1);
				if (getInt("finishedcounter") >= GRUNNEnvironment.getInstance().getBOMBundle().size()) {
					GRUNNEnvironment.getInstance().sendCustomerOffers();
				}
			} else {
				System.err.println("SalesPlannerAgent: Unknown message received: " + message.getMessageType());
			}
		}

	}

}
