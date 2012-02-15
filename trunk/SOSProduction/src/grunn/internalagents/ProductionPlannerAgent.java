package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.utils.BidsComparator;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;

import java.util.Collections;
import java.util.Vector;

import model.agent.Agent;
import model.agent.property.properties.ObjectProperty;
import model.messageboard.MessageBoard;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class ProductionPlannerAgent extends Agent {

	/**
	 * Constructor
	 * 
	 */
	public ProductionPlannerAgent(String id) {
		super(id);
		init(PropertyType.TEXT, Agent.TYPE, "Planner");
		init(PropertyType.BOOLEAN,"plan_finished",Boolean.toString(true));
		init(PropertyType.OBJECT, "receivedBids", ObjectProperty.objectToString(new Vector<GRUNNMessage>()));
	}

	@Override
	public void act() throws Exception {
		while (MessageBoard.getInstance().hasMessage(get(ID))) {
			GRUNNMessage message = (GRUNNMessage) MessageBoard.getInstance().getMessage(get(ID));
			
			if (message.getMessageType() == GRUNNMessageType.NEWDAY){
				set(PropertyType.BOOLEAN,"plan_finished",Boolean.toString(false));
			} else if (message.getMessageType() == GRUNNMessageType.PRODUCTIONBID) {
				@SuppressWarnings("unchecked")
				Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");
				bids.add(message);
				setObject("receivedBids",bids);
			} else {
				System.err.println("ProductTypeAgent: Unknown message received: " + message.getMessageType());
			}
			
		}

		if (GRUNNEnvironment.getInstance().getTimeOfDay() > 9 && !get("plan_finished").equals(Boolean.toString(true))){
			System.out.println("Time for disbributing production capacity!");
			distributeProduction();
			set(PropertyType.BOOLEAN,"plan_finished",Boolean.toString(true));
		}
		
	}

	// ----------------------------------------------------------------------------
	// SUB BEHAVIOURS
	// ----------------------------------------------------------------------------

	private void distributeProduction() {


		// receive bids
		@SuppressWarnings("unchecked")
		Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");

		// sort bids
		Collections.sort(bids, new BidsComparator());

		// process bids
		for (GRUNNMessage bid : bids) {
			int quantity = bid.getBidAmount();
			int productID = bid.getProductID();
			double totalPrice = bid.getBidUnitPrice() * quantity;

			int cycles = GRUNNEnvironment.getInstance().getBOMBundle().getAssemblyCyclesRequired(GRUNNEnvironment.getInstance().getBOMBundle().getIndexFor(productID));

			boolean won = false;
			if ((quantity * cycles) <= GRUNNEnvironment.getInstance().getFreeFactoryCapacityForNextDay()) {

				try {
					won = GRUNNEnvironment.getInstance().addProductionRequest(productID, quantity);
				} catch (Exception e) {
					won = false;
					System.err.println("ProductionPlannerAgent: Reserving production failed!");
					e.printStackTrace();
				}

			}

			GRUNNMessage resultOfBid = new GRUNNMessage(get(ID), GRUNNMessageType.RESULTOFPRODUCTIONBID);
			resultOfBid.setWonAuction(won);
			resultOfBid.setWonAmount(won ? quantity : 0);
			resultOfBid.setBidUnitPrice(bid.getBidUnitPrice());
			resultOfBid.setTotalPrice(won ? totalPrice : 0);

			MessageBoard.getInstance().sendMessage(bid.getFromID(), resultOfBid);

		}
		
		setObject("receivedBids",new Vector<GRUNNMessage>());

		try {
			GRUNNEnvironment.getInstance().sendProductionSchedule();
		} catch (Exception e) {
			System.err.println("ProductionPlannerAgent: Sending production schedule failed!");
			e.printStackTrace();
		}

	}
}
