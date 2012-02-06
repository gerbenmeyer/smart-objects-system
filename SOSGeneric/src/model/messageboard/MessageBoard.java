package model.messageboard;

import java.util.HashMap;
import java.util.Vector;

import model.agent.Agent;
import model.agent.collection.AgentCollection;

/**
 * A messageboard for sending and receiving messages.
 * 
 * @author Gerben G. Meyer
 * 
 */

public class MessageBoard extends HashMap<String, Vector<Message>> {

	private static final long serialVersionUID = 7120797025956701907L;

	private static MessageBoard instance = new MessageBoard();

	public static synchronized MessageBoard getInstance() {
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
	public void sendMessage(String agentId, Message message) {
		receiveMessage(agentId, message);
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
	public Message getMessage(String agentId) {
		if (!containsKey(agentId)) {
			return null;
		}
		return get(agentId).remove(0);
	}

	public int getMessageCount(String agentId) {
		if (!containsKey(agentId)) {
			return 0;
		}
		return get(agentId).size();
	}

	/**
	 * Let the agent receive a new message
	 * 
	 * @param message
	 *            the message the agent has to receive
	 */
	private void receiveMessage(String agentId, Message message) {
		if (!containsKey(agentId)) {
			return;
		}
		get(agentId).add(message);
	}

	/**
	 * Returns if the agent has unread messages
	 * 
	 * @return true if the agent has unread messages
	 */
	public boolean hasMessage(String agentId) {
		if (!containsKey(agentId)) {
			return false;
		}
		return !get(agentId).isEmpty();
	}

	/**
	 * Registers an agent to the message board
	 * 
	 * @param agent
	 */
	public void registerAgent(String agentId) {
		put(agentId, new Vector<Message>());
	}

	/**
	 * Unregisters an agent from the message board
	 * 
	 * @param agent
	 */
	public void unregisterAgent(String agentId) {
		remove(agentId);
	}

}
