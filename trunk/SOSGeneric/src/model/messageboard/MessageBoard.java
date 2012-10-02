package model.messageboard;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import model.agent.Agent;
import model.agent.collection.AgentCollection;

/**
 * A messageboard for sending and receiving messages.
 * 
 * @author Gerben G. Meyer
 * 
 */

public class MessageBoard extends HashMap<String, ConcurrentLinkedQueue<Message>> {

	private static final long serialVersionUID = 7120797025956701907L;

	private static MessageBoard instance = new MessageBoard();

	public static MessageBoard getInstance() {
		return instance;
	}

	/**
	 * Constructor
	 */
	public MessageBoard() {
		super();
	}

	/**
	 * Broadcast a message to all agents
	 * 
	 * @param message
	 */
	public void broadcastMessage(Message message) {
		for (String agentId : AgentCollection.getInstance().getIDs()) {
			receiveMessage(agentId, message);
		}
	}

	/**
	 * Send a message to an agent with a specific Id
	 * 
	 * @param agentID
	 * @param message
	 */
	public void sendMessage(String agentID, Message message) {
		receiveMessage(agentID, message);
	}

	/**
	 * Send a message to all agents of a certain type
	 * 
	 * @param agentType
	 * @param message
	 */
	public void sendMessageToAgentType(String agentType, Message message) {
		for (String agentId : AgentCollection.getInstance().getIDs()) {
			if (AgentCollection.getInstance().get(agentId).get(Agent.TYPE).equals(agentType)) {
				receiveMessage(agentId, message);
			}
		}

	}

	/**
	 * Returns the oldest unread message
	 * 
	 * @return the oldest unread message
	 */
	public Message getMessage(String agentID) {
		if (!containsKey(agentID)) {
			return null;
		}
		return get(agentID).poll();
	}

	public int getMessageCount(String agentID) {
		if (!containsKey(agentID)) {
			return 0;
		}
		return get(agentID).size();
	}

	/**
	 * Let the agent receive a new message
	 * 
	 * @param message
	 *            the message the agent has to receive
	 */
	private void receiveMessage(String agentID, Message message) {
		if (!containsKey(agentID)) {
			put(agentID, new ConcurrentLinkedQueue<Message>());
		}
		get(agentID).add(message);
	}

	/**
	 * Returns if the agent has unread messages
	 * 
	 * @return true if the agent has unread messages
	 */
	public boolean hasMessage(String agentID) {
		if (!containsKey(agentID)) {
			return false;
		}
		return !get(agentID).isEmpty();
	}

	/**
	 * Unregisters an agent from the message board
	 * 
	 * @param agent
	 */
	public void removeAgent(String agentID) {
		remove(agentID);
	}

}
