package grunn;

import grunn.world.GRUNNMessage;
import grunn.world.GRUNNMessageType;
import model.agent.Agent;
import model.messageboard.MessageBoard;
import se.sics.tasim.props.BOMBundle;
import se.sics.tasim.props.BankStatus;
import se.sics.tasim.props.ComponentCatalog;
import se.sics.tasim.props.FactoryStatus;
import se.sics.tasim.props.InventoryStatus;
import se.sics.tasim.props.MarketReport;
import se.sics.tasim.props.OfferBundle;
import se.sics.tasim.props.PriceReport;
import se.sics.tasim.props.RFQBundle;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import se.sics.tasim.tac03.aw.Order;
import se.sics.tasim.tac03.aw.OrderStore;
import se.sics.tasim.tac03.aw.RFQStore;
import se.sics.tasim.tac03.aw.SCMAgent;

/**
 * @author Gerben G. Meyer
 * 
 */
public class GRUNNEnvironment extends SCMAgent {

	private static GRUNNEnvironment instance = null;

	public static synchronized GRUNNEnvironment getInstance() {
		return instance;
	}

	private boolean productionScheduleReady = false;
	private boolean deliveryScheduleReady = false;
	
	private long newDayStartTime = System.currentTimeMillis();

	// CENTRAL DATABASE

	// daily info
	private SimulationStatus simulationStatus = new SimulationStatus();
	private FactoryStatus factoryStatus = new FactoryStatus();
	private BankStatus bankStatus = new BankStatus();

	// periodic info
	private PriceReport priceReport = new PriceReport();
	private MarketReport marketReport = new MarketReport();

	private boolean showDayInfo = true;

	/**
	 * Constructor
	 */
	public GRUNNEnvironment() {
		super();
		instance = this;
		System.out.println("Start of System.out for agent GRUNN");
		System.err.println("Start of System.err for agent GRUNN");
		SOSInternalAgents.getInstance().cleanup();
		
	}

	@Override
	protected void simulationSetup() {
		System.out.println("Simulation setup");

		productionScheduleReady = false;
		deliveryScheduleReady = false;

		simulationStatus = new SimulationStatus();
		factoryStatus = new FactoryStatus();
		bankStatus = new BankStatus();

		priceReport = new PriceReport();
		marketReport = new MarketReport();

		SOSInternalAgents.getInstance().initialize();
	}

	@Override
	protected void simulationStarted() {
		System.out.println("**********************************************************************");
		System.out.println("                    START OF SIMULATION " + getStartInfo().getSimulationID());
		System.out.println("**********************************************************************");
	}

	@Override
	protected void simulationEnded() {
		System.out.println("**********************************************************************");
		System.out.println("                    END OF SIMULATION " + getStartInfo().getSimulationID());
		newDayStarted();
		System.out.println("**********************************************************************");
		// stop all agents
		SOSInternalAgents.getInstance().cleanup();
	}

	// ----------------------------------------------------------------------------
	// GET INFORMATION OF EXTERNAL WORLD
	// ----------------------------------------------------------------------------
	
	/**
	 * 
	 * @return
	 */
	public double getTimeOfDay(){
		long now = System.currentTimeMillis();
		return (now-newDayStartTime) / 1000.0;
	}
	
	/**
	 * 
	 * @param a
	 */
	public void addAgent(Agent a){
		SOSInternalAgents.getInstance().getAgentCollection().put(a);		
	}

	/**
	 * @return the start info for current game/simulation.
	 */
	@Override
	public StartInfo getStartInfo() {
		return super.getStartInfo();
	}

	/**
	 * @return the BOM bundle for current game/simulation
	 */
	@Override
	public BOMBundle getBOMBundle() {
		return super.getBOMBundle();
	}

	/**
	 * @return the component catalog for current game/simulation
	 */
	@Override
	public ComponentCatalog getComponentCatalog() {
		return super.getComponentCatalog();
	}

	/**
	 * @return the customer order store
	 */
	@Override
	public OrderStore getCustomerOrders() {
		return super.getCustomerOrders();
	}

	/**
	 * Returns the last received RFQs from the customers.
	 */
	@Override
	public RFQBundle getCustomerRFQs() {
		return super.getCustomerRFQs();
	}

	/**
	 * @return the supplier RFQ store
	 */
	@Override
	public RFQStore getSupplierRFQs() {
		return super.getSupplierRFQs();
	}

	/**
	 * @return the supplier order store
	 */
	@Override
	public OrderStore getSupplierOrders() {
		return super.getSupplierOrders();
	}

	/**
	 * Returns the simulation status
	 * 
	 * @return the simulationStatus
	 */
	public SimulationStatus getSimulationStatus() {
		return simulationStatus;
	}

	/**
	 * Returns the factory status
	 * 
	 * @return the factoryStatus.
	 */
	public FactoryStatus getFactoryStatus() {
		return factoryStatus;
	}

	/**
	 * Returns the bank status
	 * 
	 * @return the bankStatus.
	 */
	public BankStatus getBankStatus() {
		return bankStatus;
	}

	/**
	 * Returns the latest price report
	 * 
	 * @return the priceReport.
	 */
	public PriceReport getPriceReport() {
		return priceReport;
	}

	/**
	 * Returns the latest market report
	 * 
	 * @return the marketReport.
	 */
	public MarketReport getMarketReport() {
		return marketReport;
	}

	/**
	 * Returns the number of days a delivery can be late before the customer
	 * cancels the order. In TAC SCM only customers can cancel orders.
	 */
	public int getDaysBeforeVoid() {
		return super.getDaysBeforeVoid();
	}

	public int getFactoryCapacity() {
		return super.getFactoryCapacity();
	}

	// ----------------------------------------------------------------------------
	// HANDLE THE FACTORY
	// ----------------------------------------------------------------------------

	/**
	 * @return the last received inventory
	 */
	@Override
	public InventoryStatus getCurrentInventory() {
		return super.getCurrentInventory();
	}

	/**
	 * @return the calculated inventory for next day
	 */
	@Override
	public InventoryStatus getInventoryForNextDay() {
		return super.getInventoryForNextDay();
	}

	@Override
	public void reserveInventoryForNextDay(int productID, int quantity) {
		super.reserveInventoryForNextDay(productID, quantity);
	}

	/**
	 * @return the calculated free factory capacity after removing the capacity
	 *         needed for the next day's production
	 */
	@Override
	public int getFreeFactoryCapacityForNextDay() {
		return super.getFreeFactoryCapacityForNextDay();
	}

	// ----------------------------------------------------------------------------
	// HANDLE RECEIVING OF MESSAGES
	// ----------------------------------------------------------------------------

	private synchronized void newDayStarted() {
		if (showDayInfo) {
			newDayStartTime = System.currentTimeMillis();
			showDayInfo = false;
			System.out.println("------------------------- START OF DAY " + (getSimulationStatus().getCurrentDate() + 1) + " -------------------------");

			// calculate requested factory capacity (based on amount of active
			// orders)

			Order[] orders = getCustomerOrders().getActiveOrders();

			int requiredCyclesForActiveOrders = 0;
			int activeOrders = 0;
			if (orders != null) {
				activeOrders = orders.length;
				for (Order order : orders) {
					int pID = order.getProductID();
					int pIndex = getBOMBundle().getIndexFor(pID);
					double cycles = getBOMBundle().getAssemblyCyclesRequired(pIndex);
					requiredCyclesForActiveOrders += order.getQuantity() * cycles;
				}
			}

			int factoryLoad = 0;
			if (getFactoryCapacity() != 0) {
				factoryLoad = 100 * requiredCyclesForActiveOrders / getFactoryCapacity();
			}
			long bankAccount = getBankStatus().getAccountStatus();
			System.out.println("Active orders: " + activeOrders + ". FactoryLoad: " + factoryLoad + "%. Bank account: $ " + bankAccount + ".");

		}
	}

	@Override
	protected void handleCustomerRFQs(RFQBundle rfqBundle) {
		newDayStarted();
	}

	@Override
	protected void handleCustomerOrders(Order[] newOrders) {
		newDayStarted();
	}

	@Override
	protected void handleSupplierOffers(String supplierAddress, OfferBundle offers) {
		newDayStarted();

		// --->> to PurchasePlanner
		GRUNNMessage message = new GRUNNMessage("root", GRUNNMessageType.OFFERBUNDLE);
		message.setOffers(offers);
		MessageBoard.getInstance().sendMessage("PurchasePlanner", message);

	}

	@Override
	protected void handleSimulationStatus(SimulationStatus status) {
		this.simulationStatus = status;
		showDayInfo = true;
		productionScheduleReady = false;
		deliveryScheduleReady = false;
		// --->> new day event
		GRUNNMessage message = new GRUNNMessage("root", GRUNNMessageType.NEWDAY);
		MessageBoard.getInstance().broadcastMessage(message);
	}

	@Override
	protected void handleFactoryStatus(FactoryStatus status) {
		this.factoryStatus = status;
	}

	@Override
	protected void handleBankStatus(BankStatus status) {
		this.bankStatus = status;
	}

	@Override
	protected void handlePriceReport(PriceReport report) {
		this.priceReport = report;
	}

	@Override
	protected void handleMarketReport(MarketReport report) {
		this.marketReport = report;
	}

	// ----------------------------------------------------------------------------
	// HANDLE SENDING OF MESAGES
	// ----------------------------------------------------------------------------

	@Override
	public int addCustomerOffer(RFQBundle rfqBundle, int rfqIndex, int offeredUnitPrice) {
		return super.addCustomerOffer(rfqBundle, rfqIndex, offeredUnitPrice);
	}

	@Override
	public int addSupplierRFQ(String supplierAddress, int productID, int quantity, int reservePricePerUnit, int dueDate) {
		return super.addSupplierRFQ(supplierAddress, productID, quantity, reservePricePerUnit, dueDate);
	}

	@Override
	public Order addSupplierOrder(String supplierAddress, OfferBundle offers, int offerIndex) {
		return super.addSupplierOrder(supplierAddress, offers, offerIndex);
	}

	@Override
	public boolean addProductionRequest(int productID, int quantity) {
		return super.addProductionRequest(productID, quantity);
	}

	@Override
	public boolean addDeliveryRequest(Order order) {
		return super.addDeliveryRequest(order);
	}

	@Override
	public void sendCustomerOffers() {
		super.sendCustomerOffers();
	}

	@Override
	public void sendSupplierRFQs() {
		super.sendSupplierRFQs();
	}

	@Override
	public void sendSupplierOrders() {
		super.sendSupplierOrders();
	}

	/**
	 * Sends the delivery and production schedules to the manufacturer's
	 * factory, after both sendProductionSchedule and sendDeliverySchedule have
	 * been called.
	 */
	public void sendProductionSchedule() {
		productionScheduleReady = true;
		if (productionScheduleReady && deliveryScheduleReady) {
			super.sendFactorySchedules();
		}

	}

	/**
	 * Sends the delivery and production schedules to the manufacturer's
	 * factory, after both sendProductionSchedule and sendDeliverySchedule have
	 * been called.
	 */
	public void sendDeliverySchedule() {
		deliveryScheduleReady = true;
		if (productionScheduleReady && deliveryScheduleReady) {
			super.sendFactorySchedules();
		}

	}

}