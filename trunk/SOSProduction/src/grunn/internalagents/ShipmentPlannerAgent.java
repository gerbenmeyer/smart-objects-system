/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.utils.BidsComparator;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;

import java.util.Collections;
import java.util.Vector;

import model.agent.Agent;
import se.sics.tasim.tac03.aw.Order;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class ShipmentPlannerAgent extends Agent {

	/**
	 * Constructor
	 * 
	 */
	public ShipmentPlannerAgent(String id) {
		super(id);
		initText(Agent.TYPE, "Planner");
		initBool("plan_finished",true);
		initObject("receivedBids", new Vector<GRUNNMessage>());
	}


	@Override
	public void act() throws Exception {
		while (messages().hasMessage()) {
			GRUNNMessage message = (GRUNNMessage) messages().getMessage();
			
			if (message.getMessageType() ==  GRUNNMessageType.NEWDAY){
				set(PropertyType.BOOLEAN,"plan_finished",Boolean.toString(false));
			} else if (message.getMessageType() == GRUNNMessageType.SHIPMENTBID) {
				@SuppressWarnings("unchecked")
				Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");
				bids.add(message);
				setObject("receivedBids",bids);				
			} else {
				System.err.println("ShipmentPlannerAgent: Unknown message received: " + message.getMessageType());
			}
		}
			
		if (GRUNNEnvironment.getInstance().getTimeOfDay() > 9 && !get("plan_finished").equals(Boolean.toString(true))){
			System.out.println("Time for disbributing shipments!");
			distributeShipment();
			set(PropertyType.BOOLEAN,"plan_finished",Boolean.toString(true));
		}
		
	}

	// ----------------------------------------------------------------------------
	// SUB BEHAVIOURS
	// ----------------------------------------------------------------------------

	private void distributeShipment() {

		// receive bids
		@SuppressWarnings("unchecked")
		Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");

		// sort bids
		Collections.sort(bids, new BidsComparator());

		// process bids
		for (GRUNNMessage bid : bids) {
			Order order = GRUNNEnvironment.getInstance().getCustomerOrders().getOrder(bid.getOrderID()); 

			boolean won = false;

			try {
				won = GRUNNEnvironment.getInstance().addDeliveryRequest(order);
			} catch (Exception e) {
				won = false;
				System.err.println("ShipmentPlannerAgent: Reserving shipment failed!");
			}

			GRUNNMessage resultOfBid = new GRUNNMessage(get(ID), GRUNNMessageType.RESULTOFSHIPMENTBID);
			resultOfBid.setWonAuction(won);
			messages().sendMessage(bid.getFromID(), resultOfBid);

		}

		setObject("receivedBids",new Vector<GRUNNMessage>());
		
		try {
			GRUNNEnvironment.getInstance().sendDeliverySchedule();
		} catch (Exception e) {
			System.err.println("ShipmentPlannerAgent: Sending delivery schedule failed!");
			e.printStackTrace();
		}

	}

}
