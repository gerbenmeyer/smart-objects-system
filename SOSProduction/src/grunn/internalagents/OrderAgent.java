/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import grunn.world.GRUNNSharedKnowledge;
import model.agent.Agent;
import se.sics.tasim.tac03.aw.Order;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class OrderAgent extends Agent {

	// parameters from conf file, TODO: read from conf file
	private double randomRate = 0.10;
	private double timeRate = 0.5;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public OrderAgent(String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param orderID
	 */
	public OrderAgent(String id, int orderID) {
		super(id);
		initText(Agent.TYPE, "Order");
		initInt("orderID", orderID);
		initInt("acquiredProduction", 0);
		initInt("acquiredProducts", 0);
	}

	private Order getOrder() {
		return GRUNNEnvironment.getInstance().getCustomerOrders().getOrder(getInt("orderID"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * grunn.world.InternalAgent#agentBehaviour(grunn.world.InternalMessage)
	 */
	@Override
	protected void act() {
		while (messages().hasMessage()) {
			GRUNNMessage message = (GRUNNMessage) messages().getMessage();

			if (message.getMessageType() == GRUNNMessageType.NEWDAY) {

				if (!get("initialized").equals(Boolean.toString(true))) {
					int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
					for (int component : components) {
						init(PropertyType.INTEGER, "acquiredComponent" + component, Integer.toString(0));
					}
					set(PropertyType.BOOLEAN, "initialized", Boolean.toString(true));
				}
				if (getOrder().isActive()) {
					setInt("acquiredProducts", getInt("acquiredProducts") + getInt("acquiredProduction"));
					setInt("acquiredProduction", 0);
					reserveAcquiredProducts();
					reserveAcquiredComponents();

					acquireComponents();
					acquireProduction();
					acquireShipment();
				}
				if (getOrder().isDelivered()) {
					handleFinished();
				}
				if (getOrder().isCanceled()) {
					handleCanceled();
				}
			} else if (message.getMessageType() == GRUNNMessageType.RESULTOFCOMPONENTBID) {
				handleResultOfComponentBid(message);
			} else if (message.getMessageType() == GRUNNMessageType.RESULTOFPRODUCTIONBID) {
				handleResultOfProductionBid(message);
			} else if (message.getMessageType() == GRUNNMessageType.RESULTOFSHIPMENTBID) {
				handleResultOfShipmentBid(message);
			} else {
				System.err.println("Orderagent: Unknown message received: " + message.getMessageType());
			}

		}

		// update status
		String status = "T-" + (getOrder().getDueDate() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate());
		int requiredComponents = 4 * (getOrder().getQuantity() - getInt("acquiredProducts"));
		if (requiredComponents <= 0) {
			status += ", comp: 100%";
		} else {
			int acquiredComponents = 0;
			int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
			for (int component : components) {
				acquiredComponents += getInt("acquiredComponent" + component);
			}

			status += ", comp: " + (100 * acquiredComponents / requiredComponents) + "%";
		}

		status += ", prod: " + (100 * getInt("acquiredProducts") / getOrder().getQuantity()) + "%";
		
		setText(DESCRIPTION, status);

	}

	// ----------------------------------------------------------------------------
	// SUB BEHAVIOURS
	// ----------------------------------------------------------------------------

	private void reserveAcquiredComponents() {
		int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
		for (int component : components) {
			if (getInt("acquiredComponent" + component) > getOrder().getQuantity()) {
				setInt("acquiredComponent", getOrder().getQuantity());
			}
			if (getInt("acquiredComponent" + component) > 0) {
				try {
					GRUNNEnvironment.getInstance().reserveInventoryForNextDay(component, getInt("acquiredComponent" + component));
				} catch (Exception e) {
					System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to reserve acquired components!");
				}
			}

		}
	}

	private void reserveAcquiredProducts() {
		if (getInt("acquiredProducts") > 0) {
			try {
				GRUNNEnvironment.getInstance().reserveInventoryForNextDay(getOrder().getProductID(), getInt("acquiredProducts"));
			} catch (Exception e) {
				// setInt("acquiredProducts", 0);
				System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to reserve acquired products!");
			}
		}
	}

	private boolean prepareComponentsForProduction(int amount) {
		int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
		for (int component : components) {
			if (amount <= getInt("acquiredComponent" + component)) {
				try {
					GRUNNEnvironment.getInstance().getInventoryForNextDay().addInventory(component, amount);
					setInt("acquiredComponent" + component, getInt("acquiredComponent" + component) - amount);
				} catch (Exception e) {
					System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to prepare components for production (1)!");
					return false;
				}
			} else {
				System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to prepare components for production (2)!");
				return false;
			}
		}
		return true;

	}

	private boolean prepareProductsForShipment(int amount) {
		if (amount <= getInt("acquiredProducts")) {
			try {
				GRUNNEnvironment.getInstance().getInventoryForNextDay().addInventory(getOrder().getProductID(), amount);
				setInt("acquiredProducts", getInt("acquiredProducts") - amount);
			} catch (Exception e) {
				System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to prepare products products for shipment (1)!");
				return false;
			}
		} else {
			System.err.println("OrderAgent " + getOrder().getOrderID() + ": Unable to prepare products products for shipment (2)!");
			return false;
		}
		return true;
	}

	private void acquireComponents() {
		int daysTillDueDate = getOrder().getDueDate() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();

		// anyway too late to make it
		if ((daysTillDueDate + GRUNNEnvironment.getInstance().getDaysBeforeVoid()) < 4) {
			getOrder().setCanceled();
			return;
		}

		int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
		for (int componentID : components) {
			int quantityNeeded = getOrder().getQuantity() - (getInt("acquiredProducts") + getInt("acquiredProduction") + getInt("acquiredComponent" + componentID));

			if (quantityNeeded <= 0) {
				continue;
			}

			double componentPrice = GRUNNSharedKnowledge.getInstance().getComponentMarketPrice(componentID);

			double randomFactor = (1.0 - randomRate) + (Math.random() * randomRate * 2.0);

			double timeFactor = timeFactor(6 - daysTillDueDate);
			double bidUnitPrice = componentPrice * randomFactor * timeFactor;

			GRUNNMessage bid = new GRUNNMessage(get(ID), GRUNNMessageType.COMPONENTBID);
			bid.setComponentID(componentID);
			bid.setBidAmount(quantityNeeded);
			bid.setBidUnitPrice(bidUnitPrice);
			messages().sendMessage("Component-" + componentID, bid);
		}
	}

	private void handleResultOfComponentBid(GRUNNMessage message) {
		int componentID = message.getComponentID();
		boolean won = message.isWonAuction();

		// update acquired components
		if (won) {
			setInt("acquiredComponent" + componentID, getInt("acquiredComponent" + componentID) + message.getWonAmount());
		}
	}

	private void acquireProduction() {

		int daysTillDueDate = getOrder().getDueDate() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();

		// anyway too late to make it
		if ((daysTillDueDate + GRUNNEnvironment.getInstance().getDaysBeforeVoid()) < 3) {
			getOrder().setCanceled();
			return;
		}

		int quantityNeeded = 0;
		quantityNeeded = getOrder().getQuantity() - (getInt("acquiredProducts") + getInt("acquiredProduction"));

		int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(this.getOrder().getProductID());
		for (int component : components) {
			int amount = getInt("acquiredComponent" + component);
			if (amount < quantityNeeded)
				quantityNeeded = amount;
		}

		if (quantityNeeded <= 0)
			return;

		if (!prepareComponentsForProduction(quantityNeeded))
			return;

		double unitPrice = 100.0;
		double randomFactor = (1.0 - randomRate) + (Math.random() * randomRate * 2.0);
		double timeFactor = timeFactor(5 - daysTillDueDate);
		double bidUnitPrice = unitPrice * randomFactor * timeFactor;

		GRUNNMessage bid = new GRUNNMessage(get(ID), GRUNNMessageType.PRODUCTIONBID);
		bid.setProductID(getOrder().getProductID());
		bid.setBidAmount(getOrder().getQuantity());
		bid.setBidUnitPrice(bidUnitPrice);
		messages().sendMessage("ProductionPlanner", bid);

	}

	private void handleResultOfProductionBid(GRUNNMessage message) {
		boolean won = message.isWonAuction();

		if (won) {
			setInt("acquiredProduction", getInt("acquiredProduction") + message.getWonAmount());
		}
	}

	private void acquireShipment() {
		int daysTillDueDate = getOrder().getDueDate() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();

		// anyway too late to make it
		if ((daysTillDueDate + GRUNNEnvironment.getInstance().getDaysBeforeVoid()) < 2) {
			getOrder().setCanceled();
			return;
		}

		if (getInt("acquiredProducts") < getOrder().getQuantity())
			return;

		if (!prepareProductsForShipment(getOrder().getQuantity()))
			return;

		GRUNNMessage bid = new GRUNNMessage(get(ID), GRUNNMessageType.SHIPMENTBID);
		bid.setOrderID(getInt("orderID"));
		messages().sendMessage("ShipmentPlanner", bid);
	}

	private void handleResultOfShipmentBid(GRUNNMessage message) {
	}

	private void handleFinished() {
		this.delete();
	}

	private void handleCanceled() {
		System.err.println("OrderAgent: Order canceled!");
		this.delete();
	}

	// ----------------------------------------------------------------------------
	// GETTERS / SETTERS
	// ----------------------------------------------------------------------------

	private double timeFactor(double refDay) {
		return 1 + (timeRate * Math.tanh(0.5 * refDay));
	}
}
