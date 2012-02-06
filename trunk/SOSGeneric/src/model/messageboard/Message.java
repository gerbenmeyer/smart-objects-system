package model.messageboard;

import java.io.Serializable;

/**
 * A simple message.
 * 
 * @author Gerben G. Meyer
 * 
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6484702493580807045L;
	private String fromID;
	private String content;

	/**
	 * Constructor
	 * 
	 * @param fromID
	 * @param content
	 */
	public Message(String fromID, String content) {
		super();
		this.fromID = fromID;
		this.content = content;
	}

	public String toString() {
		return "Message: " + content + " ; from: " + fromID;
	}

	/**
	 * @return the fromID
	 */
	public String getFromID() {
		return fromID;
	}

	/**
	 * @param fromID
	 *            the fromID to set
	 */
	public void setFromID(String fromID) {
		this.fromID = fromID;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
