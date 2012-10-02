package model.messageboard;


/**
 * An interface for an agent to the messageboard
 * 
 * @author Gerben G. Meyer
 * 
 */
public class MessageBoardInterface {

	private String agentID;

	/**
	 * Constructor
	 * 
	 * @param agentID
	 */
	public MessageBoardInterface(String agentID) {
		this.agentID = agentID;
	}
	
	/**
	 * Broadcast a message to all agents
	 * 
	 * @param message
	 */
	public void broadcastMessage(Message message) {
		MessageBoard.getInstance().sendMessage(agentID, message);
	}

	/**
	 * Send a message to an agent with a specific Id
	 * 
	 * @param agentID
	 * @param message
	 */
	public void sendMessage(String agentID, Message message) {
		MessageBoard.getInstance().sendMessage(agentID, message);
	}

	/**
	 * Send a message to all agents of a certain type
	 * 
	 * @param agentType
	 * @param message
	 */
	public void sendMessageToAgentType(String agentType, Message message) {
		MessageBoard.getInstance().sendMessageToAgentType(agentType, message);
	}

	/**
	 * Returns the oldest unread message
	 * 
	 * @return the oldest unread message
	 */
	public Message getMessage() {
		return MessageBoard.getInstance().getMessage(agentID);
	}

	public int getMessageCount() {
		return MessageBoard.getInstance().getMessageCount(agentID);
	}

	/**
	 * Returns if the agent has unread messages
	 * 
	 * @return true if the agent has unread messages
	 */
	public boolean hasMessage() {
		return MessageBoard.getInstance().hasMessage(agentID);
	}



}
