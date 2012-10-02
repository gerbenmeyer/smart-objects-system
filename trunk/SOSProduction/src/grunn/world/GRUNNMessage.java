package grunn.world;

import java.io.Serializable;

import model.messageboard.Message;
import se.sics.tasim.props.OfferBundle;

public class GRUNNMessage extends Message implements Serializable{

	private static final long serialVersionUID = 2743260518711056687L;

	private GRUNNMessageType messageType = GRUNNMessageType.UNDEFINED;

	// for offers message received from suppliers
	private OfferBundle offers;

	// for RequestForComponentBids message, and for ResultOfComponentBid messge
	private int componentID = 0;

	// for bids messages
	private int bidAmount = 0;
	private double bidUnitPrice = 0.0;
	private int productID = 0; // for production bids
	private int orderID = 0; // for shipment bids

	// for ResultOfBid messages
	private boolean wonAuction = false;
	private int wonAmount = 0;
	private double totalPrice = 0.0;

	// CONSTRUCTOR

	public GRUNNMessage(String fromID, GRUNNMessageType messageType) {
		super(fromID,"");
		this.messageType = messageType;
	}

	public String toString() {
		return "InternalMessage type: " + messageType+ " ; from: "+getFromID();
	}

	// GETTERS AND SETTERS

	public OfferBundle getOffers() {
		return offers;
	}

	public void setOffers(OfferBundle offers) {
		this.offers = offers;
	}

	public int getComponentID() {
		return componentID;
	}

	public void setComponentID(int componentID) {
		this.componentID = componentID;
	}

	public int getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(int bidAmount) {
		this.bidAmount = bidAmount;
	}

	public double getBidUnitPrice() {
		return bidUnitPrice;
	}

	public void setBidUnitPrice(double bidUnitPrice) {
		this.bidUnitPrice = bidUnitPrice;
	}

	public boolean isWonAuction() {
		return wonAuction;
	}

	public void setWonAuction(boolean wonAuction) {
		this.wonAuction = wonAuction;
	}

	public int getWonAmount() {
		return wonAmount;
	}

	public void setWonAmount(int wonAmount) {
		this.wonAmount = wonAmount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public GRUNNMessageType getMessageType() {
		return messageType;
	}

	/**
	 * @return the productID
	 */
	public int getProductID() {
		return productID;
	}

	/**
	 * @param productID
	 *            the productID to set
	 */
	public void setProductID(int productID) {
		this.productID = productID;
	}


	/**
	 * @return the orderID
	 */
	public int getOrderID() {
		return orderID;
	}

	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

}
