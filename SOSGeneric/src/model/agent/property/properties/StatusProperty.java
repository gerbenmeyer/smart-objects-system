package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.AgentStatus;
import util.enums.PropertyType;

/**
 * A Property implementation holding a status. 
 * 
 * @author G.G. Meyer
 */
public class StatusProperty extends Property {

	private AgentStatus status = AgentStatus.UNKNOWN;

	/**
	 * Constucts a named StatusProperty instance.
	 * 
	 * @param name the name
	 */
	public StatusProperty(String name) {
		super(name, PropertyType.STATUS);
	}

	/**
	 * Constucts a named StatusProperty instance with a status.
	 * 
	 * @param name the name
	 * @param status the status
	 */
	public StatusProperty(String name, AgentStatus status) {
		this(name);
		setStatus(status);
	}

	/**
	 * Returns the current status of this StatusProperty.
	 * 
	 * @return the status
	 */
	public AgentStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status of this StatusProperty.
	 * 
	 * @param status the status to be set
	 */
	public void setStatus(AgentStatus status) {
		this.status = status;
		mutateHistory();
	}

	@Override
	public String toString() {
		return status.toString();
	}

	@Override
	public void parseString(String str) {
		this.status = AgentStatus.valueOf(str);
		mutateHistory();
	}

	public static String parseHint() {
		return "status";
	}
	
	public String getIcon(){
		return status.toString().toLowerCase() + ".png";
	}

	@Override
	public String toInformativeString() {
		return "Status: " + status.toString();
	}
}