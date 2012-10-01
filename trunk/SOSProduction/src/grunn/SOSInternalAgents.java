package grunn;

import grunn.internalagents.HomeAgent;
import grunn.internalagents.ProductionPlannerAgent;
import grunn.internalagents.PurchasePlannerAgent;
import grunn.internalagents.SalesPlannerAgent;
import grunn.internalagents.ShipmentPlannerAgent;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import main.SOSServer;
import model.messageboard.MessageBoard;

public class SOSInternalAgents extends SOSServer implements Runnable {
	
	private static SOSInternalAgents instance = null;
	
	public static synchronized SOSInternalAgents getInstance() {
		if (instance == null) {
			instance = createSOSServer();
			(new Thread(instance)).start();
		}
		return instance;
	}

	/**
	 * 
	 * @param settings
	 */
	public SOSInternalAgents(Properties settings) {
		// call constructor of SOSGeneric
		super(settings, new HashMap<String, String>());

		// remove all existing agents
		cleanup();

		// add the home agent to the agent collection
		getAgentCollection().put(new HomeAgent("home"));

	}

	/**
	 * @param args
	 */
	public static SOSInternalAgents createSOSServer() {
		// read settings from the config file
		File file = new File("config.ini");
		Properties settings = new Properties();
		try {
			FileInputStream fis = new FileInputStream(file);
			settings.load(fis);
			fis.close();
		} catch (Exception e) {
		}

		// create new SOS server
		return new SOSInternalAgents(settings);

	}

	public void initialize() {
		// add the planner agents to the agent collection
		getAgentCollection().put(new ShipmentPlannerAgent("ShipmentPlanner"));
		MessageBoard.getInstance().registerAgent("ShipmentPlanner");

		getAgentCollection().put(new ProductionPlannerAgent("ProductionPlanner"));
		MessageBoard.getInstance().registerAgent("ProductionPlanner");

		getAgentCollection().put(new SalesPlannerAgent("SalesPlanner"));
		MessageBoard.getInstance().registerAgent("SalesPlanner");

		getAgentCollection().put(new PurchasePlannerAgent("PurchasePlanner"));
		MessageBoard.getInstance().registerAgent("PurchasePlanner");

	}

	public void cleanup() {
		getAgentCollection().clear();
	}

	@Override
	public void run() {
		// run the server
		runServer();
	}
}
