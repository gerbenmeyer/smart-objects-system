/**
 * 
 */
package grunn.internalagents;

import grunn.GRUNNEnvironment;
import grunn.utils.Tools;
import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import grunn.world.GRUNNSharedKnowledge;
import model.agent.Agent;
import model.agent.property.properties.DependenciesProperty;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.tac03.aw.Order;
import util.enums.PropertyType;

/**
 * @author Gerben G. Meyer
 * 
 */
public class ProductTypeAgent extends Agent {

	// parameters from conf file, TODO: read from conf file
	private double learningRate = 0.1;
	private double randomRate = 0.1;

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public ProductTypeAgent(String id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param product
	 */
	public ProductTypeAgent(String id, int product) {
		super(id);

		initText(Agent.TYPE, "ProductType");
		initInt("productID", product);
		initInt("assemblyCycles", 0);
		initInt("position", 0);
		initNumber("marketPriceEstimation", 0.0);
		initNumber("componentPriceEstimation", 0.0);
		initNumber("salesEstimation", 35.0);
		initNumber("offerAcceptanceRateEstimation", 0.50);
		initBool("plan_finished", true);
		initDependency("createdOrderAgents");
		initObject("offersLastDay",new OfferBundle());

	}

	@Override
	public void act() throws Exception {
		while (messages().hasMessage()) {
			GRUNNMessage message = (GRUNNMessage) messages().getMessage();

			if (message.getMessageType() == GRUNNMessageType.NEWDAY) {
				if (!get("initialized").equals(Boolean.toString(true))) {
					int index = GRUNNEnvironment.getInstance().getBOMBundle().getIndexFor(getInt("productID"));
					setText(DESCRIPTION,GRUNNEnvironment.getInstance().getBOMBundle().getProductName(index));
					setInt("marketPriceEstimation", GRUNNEnvironment.getInstance().getBOMBundle().getProductBasePrice(index));
					setInt("assemblyCycles", GRUNNEnvironment.getInstance().getBOMBundle().getAssemblyCyclesRequired(index));
					GRUNNSharedKnowledge.getInstance().setSalesPrognosis(getInt("productID"), (int) getNumber("salesEstimation"));

					set(PropertyType.BOOLEAN, "initialized", Boolean.toString(true));
				}
				
				createOrderAgents();
				makeEstimations();
				
				set(PropertyType.BOOLEAN, "plan_finished", Boolean.toString(false));
			} else {
				System.err.println("ProductTypeAgent: Unknown message received: " + message.getMessageType());
			}
		}

		if (GRUNNEnvironment.getInstance().getTimeOfDay() > (12 + (0.1 * (16 - getInt("position")))) && !get("plan_finished").equals(Boolean.toString(true))) {
			procureOrders();
			set(PropertyType.BOOLEAN, "plan_finished", Boolean.toString(true));
		}

	}

	// ----------------------------------------------------------------------------
	// SUB BEHAVIOURS
	// ----------------------------------------------------------------------------

	private void createOrderAgents() {

		Order[] orders = GRUNNEnvironment.getInstance().getCustomerOrders().getActiveOrders();

		if (orders == null) {
			return;
		}

		DependenciesProperty dp = (DependenciesProperty) getProperty("createdOrderAgents");

		for (Order order : orders) {
			String id = "Order-" + order.getOrderID();
			if ((order.getProductID() == getInt("productID")) && (!dp.containsID(id))) {
				GRUNNEnvironment.getInstance().addAgent(new OrderAgent(id, order.getOrderID()));
				messages().sendMessage(id, new GRUNNMessage(get(ID), GRUNNMessageType.NEWDAY));
				dp.addID(id);
			}
		}

	}

	private void makeEstimations() {
		int offeredQuantity = 0;
		int acceptedQuantity = 0;

		OfferBundle offersLastDay = (OfferBundle) getObject("offersLastDay");

		for (int i = 0; i < offersLastDay.size(); i++) {

			offeredQuantity += offersLastDay.getQuantity(i);

			// check if offer is accepted
			Order[] orders = GRUNNEnvironment.getInstance().getCustomerOrders().getActiveOrders();
			boolean accepted = false;
			if (orders != null) {
				for (int j = 0; j < orders.length; j++) {
					if ((orders[j].getProductID() == getInt("productID")) && (offersLastDay.getOfferID(i) == orders[j].getOfferID())) {
						acceptedQuantity += offersLastDay.getQuantity(i);
						accepted = true;
						break;
					}
				}
			}

			// estimate new market price
			int unitPrice = offersLastDay.getUnitPrice(i);
			if (((unitPrice < getNumber("marketPriceEstimation")) && !accepted) || ((unitPrice > getNumber("marketPriceEstimation")) && accepted)) {
				setNumber("marketPriceEstimation", getNumber("marketPriceEstimation") * (1.0 - this.learningRate));
				setNumber("marketPriceEstimation", getNumber("marketPriceEstimation") + (this.learningRate * unitPrice));

			}

		}

		// offer acceptance rate estimation
		if (offeredQuantity > 0) {
			setNumber("offerAcceptanceRateEstimation", getNumber("offerAcceptanceRateEstimation") * (1.0 - this.learningRate));
			setNumber("offerAcceptanceRateEstimation", getNumber("offerAcceptanceRateEstimation") + (this.learningRate * (1.0 * acceptedQuantity / offeredQuantity)));
		}

		// component price
		setNumber("componentPriceEstimation", 0.0);
		int[] components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(getInt("productID"));
		if (components != null) {
			for (int componentID : components) {
				setNumber("componentPriceEstimation", getNumber("componentPriceEstimation") + GRUNNSharedKnowledge.getInstance().getComponentMarketPrice(componentID));
			}
		}

		// profit per cycle estimation
		double profitPerCycle = (getNumber("marketPriceEstimation") - getNumber("componentPriceEstimation")) / getInt("assemblyCycles");
		GRUNNSharedKnowledge.getInstance().setProductProfitPerCycle(getInt("productID"), profitPerCycle);

		setObject("offersLastDay", new OfferBundle());

		// calculate factory capacity for this product
		double thisProfit = GRUNNSharedKnowledge.getInstance().getProductProfitPerCycle(getInt("productID"));
		setInt("position", 0);
		for (int i = 0; i < GRUNNEnvironment.getInstance().getBOMBundle().size(); i++) {
			int pID = GRUNNEnvironment.getInstance().getBOMBundle().getProductID(i);
			double pProfit = GRUNNSharedKnowledge.getInstance().getProductProfitPerCycle(pID);
			if (thisProfit > pProfit) {
				setInt("position", getInt("position") + 1);
			}
		}
	}

	private void procureOrders() {
		RFQBundle rfqBundle = GRUNNEnvironment.getInstance().getCustomerRFQs();

		OfferBundle offersLastDay = new OfferBundle();

		if (rfqBundle != null) {

			String info = "";

			int factoryCapacityAllProducts = GRUNNEnvironment.getInstance().getFactoryCapacity();
			double factoryCapacityThisProduct = 1.0 * factoryCapacityAllProducts / GRUNNEnvironment.getInstance().getBOMBundle().size();

			double positionNorm = 0.0;
			int total = GRUNNEnvironment.getInstance().getBOMBundle().size();
			if (total != 0) {
				positionNorm = (1.0 * getInt("position") / (1.0 * total / 2)) - 1;
			}

			double extraCapacity = positionNorm * 0.5;
			factoryCapacityThisProduct *= 1 + extraCapacity;
			if (extraCapacity >= 0.0) {
				info += "FC+" + Tools.FormatNumber((int) (100 * extraCapacity), 2) + "%, ";
			} else {
				info += "FC-" + Tools.FormatNumber((int) (100 * Math.abs(extraCapacity)), 2) + "%, ";
			}

			// calculate requested factory capacity (based on amount of active
			// orders)
			int requiredCyclesForActiveOrdersThisProduct = 0;
			Order[] orders = GRUNNEnvironment.getInstance().getCustomerOrders().getActiveOrders();

			if (orders != null) {
				for (Order order : orders) {
					int pID = order.getProductID();
					int pIndex = GRUNNEnvironment.getInstance().getBOMBundle().getIndexFor(pID);
					int cycles = GRUNNEnvironment.getInstance().getBOMBundle().getAssemblyCyclesRequired(pIndex);
					if (pID == getInt("productID")) {
						requiredCyclesForActiveOrdersThisProduct += order.getQuantity() * cycles;
					}
				}
			}

			// sales estimation
			setNumber("salesEstimation", getNumber("salesEstimation") * (1.0 - this.learningRate));
			setNumber("salesEstimation", getNumber("salesEstimation") + (this.learningRate * (factoryCapacityThisProduct / getInt("assemblyCycles"))));
			GRUNNSharedKnowledge.getInstance().setSalesPrognosis(getInt("productID"), (int) getNumber("salesEstimation"));

			double factoryLoadThisProduct = 1.0 * requiredCyclesForActiveOrdersThisProduct / factoryCapacityThisProduct;

			// adjust price to factory overload
			double loadFactor = 1 + (factoryLoadThisProduct - 2.5) * 0.05;
			loadFactor = Math.max(0.95, loadFactor);

			info += "FL=" + Tools.FormatNumber((int) (100 * factoryLoadThisProduct), 3) + "%, ";

			info += "MP=" + Tools.FormatNumber((int) (100 * loadFactor), 3) + "%, ";

			boolean showNoCompMSG = true;
			boolean showNoProfitMSG = true;
			boolean showNoTimeMSG = true;

			int daysLeftInGame = GRUNNEnvironment.getInstance().getStartInfo().getNumberOfDays() - GRUNNEnvironment.getInstance().getSimulationStatus().getCurrentDate();

			// send offers
			for (int i = 0; i < rfqBundle.size(); i++) {

				if (rfqBundle.getProductID(i) == getInt("productID")) {

					boolean respond = true;

					if (factoryLoadThisProduct + 4 > daysLeftInGame) {
						respond = false;
						if (showNoTimeMSG) {
							info += "no time, ";
							showNoTimeMSG = false;
						}
					}

					// try to reserve components

					int requiredComponents = (int) Math.ceil(1.5 * getNumber("offerAcceptanceRateEstimation") * rfqBundle.getQuantity(i));

					for (int componentID : GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(getInt("productID"))) {
						int availableComponents = GRUNNEnvironment.getInstance().getInventoryForNextDay().getInventoryQuantity(componentID);

						if (availableComponents < requiredComponents) {
							respond = false;
							if (showNoCompMSG) {
								info += "no comps, ";
								showNoCompMSG = false;
							}
						}
					}

					if (getNumber("marketPriceEstimation") < getNumber("componentPriceEstimation")) {
						if (showNoProfitMSG) {
							info += "no profit, ";
							showNoProfitMSG = false;
						}
						respond = respond && (Math.random() < 0.25);
					}

					if (respond) {
						for (int componentID : GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(getInt("productID"))) {
							try {
								GRUNNEnvironment.getInstance().reserveInventoryForNextDay(componentID, requiredComponents);
							} catch (IllegalArgumentException e) {
								System.err.println("ProductTypeAgent: Reserving inventory failed!");
							}
						}
						double randomFactor = 1.0 - this.randomRate;
						randomFactor += Math.random() * 2 * this.randomRate;

						double unitPrice = getNumber("marketPriceEstimation");

						unitPrice *= loadFactor;

						unitPrice *= randomFactor;

						int offerID = GRUNNEnvironment.getInstance().addCustomerOffer(rfqBundle, i, (int) unitPrice);

						offersLastDay.addOffer(offerID, rfqBundle, i, (int) unitPrice);
					}
				}
			}

			if (offersLastDay.size() == 0) {
				info += "no offers, ";
			}

			System.out.println("Prod " + Tools.FormatNumber(getInt("productID"), 2) + ": " + Tools.FormatCurrency((int) getNumber("marketPriceEstimation")) + " - " + Tools.FormatCurrency((int) getNumber("componentPriceEstimation")) + ", " + info);
		}

		setObject("offersLastDay", offersLastDay);

		messages().sendMessage("SalesPlanner", new GRUNNMessage(get(ID), GRUNNMessageType.FINISHED));

	}

}
