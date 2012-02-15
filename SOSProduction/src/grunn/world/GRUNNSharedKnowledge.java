package grunn.world;

import grunn.GRUNNEnvironment;

import java.util.Hashtable;

import se.sics.tasim.props.InventoryStatus;

public class GRUNNSharedKnowledge {
	
	private static GRUNNSharedKnowledge instance = null;
	
	public static synchronized GRUNNSharedKnowledge getInstance(){
		if (instance == null){
			instance = new GRUNNSharedKnowledge();
		}
		return instance;
	}

	// shared knowledge
	private InventoryStatus salesPrognosis = new InventoryStatus();
	private Hashtable<Integer, Double> componentMarketPrice = new Hashtable<Integer, Double>();
	
	private Hashtable <Integer, Double> productProfitPerCycle = new Hashtable<Integer,Double>();

	public Object salesLock = new Object();



	/**
	 * Constructor
	 */
	public GRUNNSharedKnowledge() {
		super();
	}

	/**
	 * The sales planner can use this function to update the sales prognosis of
	 * a certain product. The amount of required components will automatically
	 * be updated, for use by the purchase planner.
	 * 
	 * @param productID
	 *            The productID of which the sales prognosis will be updated
	 * @param amount
	 *            The expected amount of products of productID to sell per day
	 */
	public void setSalesPrognosis(int productID, int amount) {
		synchronized (salesPrognosis) {
			int[] components = GRUNNEnvironment.getInstance().getBOMBundle()
					.getComponentsForProductID(productID);

			// update sales prognosis of product ID
			int currentPrognosis = salesPrognosis
					.getInventoryQuantity(productID);
			salesPrognosis.addInventory(productID, -currentPrognosis);
			salesPrognosis.addInventory(productID, amount);

			// update sales prognosis for components
			// set sales prognosis for components to zero
			for (int i = 0; i < GRUNNEnvironment.getInstance().getComponentCatalog().size(); i++) {
				int componentID = GRUNNEnvironment.getInstance().getComponentCatalog().getProductID(i);
				currentPrognosis = salesPrognosis
						.getInventoryQuantity(componentID);
				salesPrognosis.addInventory(componentID, -currentPrognosis);
				// salesPrognosis.addInventory(componentID, amount);
			}
			// count amount of components needed for different products
			for (int i = 0; i < GRUNNEnvironment.getInstance().getBOMBundle().size(); i++) {
				productID = GRUNNEnvironment.getInstance().getBOMBundle().getProductID(i);
				amount = salesPrognosis.getInventoryQuantity(productID);
				components = GRUNNEnvironment.getInstance().getBOMBundle().getComponentsForProductID(
						productID);
				for (int j = 0; j < components.length; j++) {
					salesPrognosis.addInventory(components[j], amount);
				}

			}
		}
	}

	/**
	 * Returns the sales prognosis of products and the therefore required
	 * components
	 * 
	 * @param productID
	 * @return The sales prognosis of productID per day
	 */
	public int getSalesPrognosis(int productID) {
		int result;
		synchronized (salesPrognosis) {
			result = salesPrognosis.getInventoryQuantity(productID);
		}
		return result;
	}

	/**
	 * 
	 * @param componentID
	 * @return the componentMarketPrice of componentID
	 */
	public double getComponentMarketPrice(int componentID) {
		try {
			return componentMarketPrice.get(componentID);
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	/**
	 * 
	 * @param componentID
	 * @param componentPrice
	 */
	public void setComponentMarketPrice(int componentID, double componentPrice) {
		this.componentMarketPrice.put(componentID, componentPrice);
	}
	
	/**
	 * 
	 * @param productID
	 * @return the productProfitPerCycle of productID
	 */
	public double getProductProfitPerCycle(int productID) {
		try {
			return productProfitPerCycle.get(productID);
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	/**
	 * 
	 * @param productID
	 * @param productProfitPerCycle
	 */
	public void setProductProfitPerCycle(int productID, double profit) {
		this.productProfitPerCycle.put(productID, profit);
	}

}
