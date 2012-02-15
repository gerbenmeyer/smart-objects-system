/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.utils.BidsComparator;
import grunn.utils.Tools;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import grunn.world.GRUNNSharedKnowledge;

import java.util.Collections;
import java.util.Vector;

import model.agent.Agent;
import model.agent.property.properties.ObjectProperty;
import model.messageboard.MessageBoard;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.tac03.aw.Order;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class ComponentTypeAgent extends Agent {

	// parameters from conf file, TODO: read from conf file
	private double learningRate = 0.5;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public ComponentTypeAgent(String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param component
	 */
	public ComponentTypeAgent(String id, int component) {
		super(id);
		init(PropertyType.TEXT, Agent.TYPE, "ComponentType");
		init(PropertyType.INTEGER, "componentID", Integer.toString(component));
		init(PropertyType.INTEGER, "inventoryNextDay", Integer.toString(0));
		init(PropertyType.INTEGER, "inventoryToBeDelivered", Integer.toString(0));
		init(PropertyType.INTEGER, "inventoryToBeDeliveredSoon", Integer.toString(0));
		init(PropertyType.INTEGER, "inventoryForDays", Integer.toString(0));
		init(PropertyType.NUMBER, "marketPriceEstimation", Double.toString(0.0));
		init(PropertyType.BOOLEAN, "plan_finished", Boolean.toString(true));
		init(PropertyType.OBJECT, "receivedBids", ObjectProperty.objectToString(new Vector<GRUNNMessage>()));
	}

	@Override
	public void act() throws Exception {
		while (MessageBoard.getInstance().hasMessage(get(ID))) {
			GRUNNMessage message = (GRUNNMessage) MessageBoard.getInstance().getMessage(get(ID));

			if (message.getMessageType() == GRUNNMessageType.NEWDAY) {
				if (!get("initialized").equals(Boolean.toString(true))) {
					int index = GRUNNEnvironment.getInstance().getComponentCatalog().getIndexFor(getInt("componentID"));
					setText(DESCRIPTION,GRUNNEnvironment.getInstance().getComponentCatalog().getProductName(index));
					double marketPriceEstimation = 0.75 * GRUNNEnvironment.getInstance().getComponentCatalog().getProductBasePrice(index);
					setDouble("marketPriceEstimation", marketPriceEstimation);
					GRUNNSharedKnowledge.getInstance().setComponentMarketPrice(getInt("componentID"), getDouble("marketPriceEstimation"));

					set(PropertyType.BOOLEAN, "initialized", Boolean.toString(true));
				}

				set(PropertyType.BOOLEAN, "plan_finished", Boolean.toString(false));

			} else if (message.getMessageType() == GRUNNMessageType.COMPONENTBID) {
				@SuppressWarnings("unchecked")
				Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");
				bids.add(message);
				setObject("receivedBids",bids);
			} else if (message.getMessageType() == GRUNNMessageType.OFFERBUNDLE) {
				sendOrders(message.getOffers());
			} else {
				System.err.println("ComponentTypeAgent: Unknown message received: " + message.getMessageType());
			}
		}

		if (GRUNNEnvironment.getInstance().getTimeOfDay() > 9 && !get("plan_finished").equals(Boolean.toString(true))) {
			distributeComponents();
			set(PropertyType.BOOLEAN, "plan_finished", Boolean.toString(true));
			makeEstimations();
			sendRFQs();
		}

	}

	// ----------------------------------------------------------------------------
	// SUB BEHAVIOURS
	// ----------------------------------------------------------------------------

	private void makeEstimations() {

		set(PropertyType.INTEGER, "inventoryNextDay", Integer.toString(GRUNNEnvironment.getInstance().getInventoryForNextDay().getInventoryQuantity(Integer.parseInt(get("componentID")))));

		int inventoryToBeDelivered = 0;
		int inventoryToBeDeliveredSoon = 0;

		Order[] orders = GRUNNEnvironment.getInstance().getSupplierOrders().getActiveOrders();
		if (orders != null) {
			for (Order order : orders) {
				if (order.getProductID() == Integer.parseInt(get("componentID"))) {

					int daysAhead = order.getDueDate() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();
					if (daysAhead > 0) {
						inventoryToBeDelivered += order.getQuantity();
						if (daysAhead <= 5) {
							inventoryToBeDeliveredSoon += order.getQuantity();
						}
					}
				}

			}
		}

		set(PropertyType.INTEGER, "inventoryToBeDelivered", Integer.toString(inventoryToBeDelivered));
		set(PropertyType.INTEGER, "inventoryToBeDeliveredSoon", Integer.toString(inventoryToBeDeliveredSoon));
	}

	private void sendRFQs() {

		String info = "";

		String[] suppliers = GRUNNEnvironment.getInstance().getComponentCatalog().getSuppliersForProduct(Integer.parseInt(get("componentID")));

		int orderAmount = GRUNNSharedKnowledge.getInstance().getSalesPrognosis(Integer.parseInt(get("componentID")));

		int inventoryForDays = 0;
		if (orderAmount > 0) {
			inventoryForDays = (int) (Integer.parseInt(get("inventoryNextDay")) + Integer.parseInt(get("inventoryToBeDelivered"))) / orderAmount;
		}
		set(PropertyType.INTEGER, "inventoryForDays", Integer.toString(inventoryForDays));

		int inventoryForDaysGoal = 25;
		int amountOfRFQs = 1;
		int dayInterval = 5;

		int daysLeftInGame = GRUNNEnvironment.getInstance().getStartInfo().getNumberOfDays() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();

		boolean unlimitedRFQ = false;

		if (inventoryForDays >= daysLeftInGame - 5) {
			orderAmount = 0;
			info += "enough till end, ";

			// reduce market price estimation
			set(PropertyType.NUMBER, "marketPriceEstimation", Double.toString(Double.parseDouble(get("marketPriceEstimation")) * 0.95));
			// send price to order agents
			GRUNNSharedKnowledge.getInstance().setComponentMarketPrice(getInt("componentID"), getDouble("marketPriceEstimation"));

		} else if (inventoryForDays < inventoryForDaysGoal - 4) {
			orderAmount *= 2.0;
			info += "way too little, ";
			unlimitedRFQ = true;
		} else if (inventoryForDays < inventoryForDaysGoal - 2) {
			orderAmount *= 1.5;
			info += "too little, ";
			unlimitedRFQ = true;
		} else if (inventoryForDays > inventoryForDaysGoal + 4) {
			orderAmount *= 0.1;
			info += "way too much, ";
		} else if (inventoryForDays > inventoryForDaysGoal + 2) {
			orderAmount *= 0.5;
			info += "too much, ";
		} else {
			orderAmount *= 1.0;
			info += "enough, ";
			unlimitedRFQ = true;
		}

		int rfqs = 0;

		int daysAhead = inventoryForDays - ((amountOfRFQs - 1) * dayInterval);
		daysAhead--;
		if (daysAhead < 5) {
			daysAhead = 5;
		}

		for (int i = daysAhead; i < (daysAhead + (dayInterval * amountOfRFQs)); i += dayInterval) {
			int dueDate = GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate() + i;
			if (GRUNNEnvironment.getInstance().getStartInfo().getNumberOfDays() > dueDate && orderAmount > 0) {
				for (String supplier : suppliers) {
					GRUNNEnvironment.getInstance().addSupplierRFQ(supplier, Integer.parseInt(get("componentID")), orderAmount / suppliers.length, (int) (0.95 * Double.parseDouble(get("marketPriceEstimation"))), dueDate);
					rfqs++;
					if (unlimitedRFQ) {
						GRUNNEnvironment.getInstance().addSupplierRFQ(supplier, Integer.parseInt(get("componentID")), orderAmount / suppliers.length, 0, dueDate);
						rfqs++;
					}
				}
			}
		}

		info += "" + rfqs + " rfqs";

		MessageBoard.getInstance().sendMessage("PurchasePlanner", new GRUNNMessage(get(ID), GRUNNMessageType.FINISHED));

		System.out.println("Comp" + Tools.FormatNumber(getInt("componentID"), 3) + ": Inv.: " + Tools.FormatNumber(getInt("inventoryNextDay")) + " + " + Tools.FormatNumber(getInt("inventoryToBeDelivered")) + ", #days: " + Tools.FormatNumber(getInt("inventoryForDays"), 2) + ", price: "
				+ Tools.FormatCurrency((int) getDouble("marketPriceEstimation")) + ", " + info);
	}

	private void sendOrders(OfferBundle supplierOffers) {
		OfferBundle offers = new OfferBundle();

		// filter out offers of other components
		for (int i = 0; i < supplierOffers.size(); i++) {
			int rfqID = supplierOffers.getRFQID(i);
			int rfqProductID = GRUNNEnvironment.getInstance().getSupplierRFQs().getProductID(rfqID);

			if (rfqProductID == Integer.parseInt(get("componentID"))) {
				offers.addOffer(supplierOffers.getOfferID(i), supplierOffers.getRFQID(i), supplierOffers.getUnitPrice(i), supplierOffers.getDueDate(i), supplierOffers.getQuantity(i));
			}
		}

		// search for cheapest offer
		int bestOfferID = -1;
		int bestOfferPrice = Integer.MAX_VALUE;
		int bestOfferQuantity = 0;
		int bestOfferDueDate = Integer.MAX_VALUE;

		for (int i = 0; i < offers.size(); i++) {
			if (offers.getUnitPrice(i) < bestOfferPrice) {
				// new best offer
				bestOfferID = i;
				bestOfferPrice = offers.getUnitPrice(i);
				bestOfferQuantity = offers.getQuantity(i);
				bestOfferDueDate = offers.getDueDate(i);
			} else if (offers.getUnitPrice(i) == bestOfferPrice) {
				if (offers.getQuantity(i) > bestOfferQuantity) {
					// new best offer
					bestOfferID = i;
					bestOfferPrice = offers.getUnitPrice(i);
					bestOfferQuantity = offers.getQuantity(i);
					bestOfferDueDate = offers.getDueDate(i);
				} else if (offers.getQuantity(i) == bestOfferQuantity) {
					if (offers.getDueDate(i) < bestOfferDueDate) {
						// new best offer
						bestOfferID = i;
						bestOfferPrice = offers.getUnitPrice(i);
						bestOfferQuantity = offers.getQuantity(i);
						bestOfferDueDate = offers.getDueDate(i);
					}
				}

			}
		}

		if (bestOfferID > -1 && offers.getQuantity(bestOfferID) > 0) {

			// accept lowest price offer
			int rfqID = offers.getRFQID(bestOfferID);
			String rfqSupplier = GRUNNEnvironment.getInstance().getSupplierRFQs().getSupplier(rfqID);

			int suppliersAmount = GRUNNEnvironment.getInstance().getComponentCatalog().getSuppliersForProduct(getInt("componentID")).length;

			// learn new market price for component

			double adjustedLearningRate = this.learningRate / suppliersAmount;

			setDouble("marketPriceEstimation", getDouble("marketPriceEstimation") * (1.0 - adjustedLearningRate));
			setDouble("marketPriceEstimation", getDouble("marketPriceEstimation") + (adjustedLearningRate * offers.getUnitPrice(bestOfferID)));

			// send order
			GRUNNEnvironment.getInstance().addSupplierOrder(rfqSupplier, offers, bestOfferID);
		}

		GRUNNEnvironment.getInstance().sendSupplierOrders();

		// send price to order agents
		GRUNNSharedKnowledge.getInstance().setComponentMarketPrice(getInt("componentID"), getDouble("marketPriceEstimation"));
	}

	private void distributeComponents() {

		// receive bids
		@SuppressWarnings("unchecked")
		Vector<GRUNNMessage> bids = (Vector<GRUNNMessage>) getObject("receivedBids");

		// sort bids
		Collections.sort(bids, new BidsComparator());

		// process bids
		for (GRUNNMessage bid : bids) {
			int quantity = bid.getBidAmount();
			double totalPrice = bid.getBidUnitPrice() * quantity;

			boolean won = false;

			if (quantity < GRUNNEnvironment.getInstance().getInventoryForNextDay().getInventoryQuantity(getInt("componentID"))) {
				won = true;
				try {
					GRUNNEnvironment.getInstance().reserveInventoryForNextDay(getInt("componentID"), quantity);
				} catch (Exception e) {
					won = false;
					System.err.println("ComponentTypeAgent: Reserving inventory failed!");
					e.printStackTrace();
				}

			}

			GRUNNMessage resultOfBid = new GRUNNMessage(get(ID), GRUNNMessageType.RESULTOFCOMPONENTBID);
			resultOfBid.setComponentID(getInt("componentID"));
			resultOfBid.setWonAuction(won);
			resultOfBid.setWonAmount(won ? quantity : 0);
			resultOfBid.setBidUnitPrice(bid.getBidUnitPrice());
			resultOfBid.setTotalPrice(won ? totalPrice : 0);

			MessageBoard.getInstance().sendMessage(bid.getFromID(), resultOfBid);

		}
		
		setObject("receivedBids",new Vector<GRUNNMessage>());

	}
}
