package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.AgentStatus;
import util.enums.PropertyType;

public class StatusProperty extends Property {

	private AgentStatus status = AgentStatus.UNKNOWN;

	/**
	 * @param name
	 * @param propertyType
	 */
	public StatusProperty(String name) {
		super(name, PropertyType.STATUS);
	}

	/**
	 * 
	 * @param number
	 */
	public StatusProperty(String name, AgentStatus status) {
		this(name);
		setStatus(status);
	}

	/**
	 * @return the status
	 */
	public AgentStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
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