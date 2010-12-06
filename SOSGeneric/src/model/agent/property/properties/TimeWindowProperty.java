package model.agent.property.properties;

import model.agent.AgentViewable;
import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation holding a time window.
 * This is used for defining a period of time.
 * 
 * @author Gerben G. Meyer
 */
public class TimeWindowProperty extends Property {

	private TimeProperty startTime = new TimeProperty();
	private TimeProperty endTime = new TimeProperty();

	/**
	 * Constructs a named TimeWindowProperty.
	 * 
	 * @param name the name
	 */
	public TimeWindowProperty(String name) {
		super(name, PropertyType.TIMEWINDOW);
	}

	/**
	 * Constructs a named TimeWindowProperty with a value.
	 * 
	 * @param name the name
	 * @param value the value
	 */
	public TimeWindowProperty(String name, String value) {
		this(name);
		parseString(value);
	}

	/**
	 * Constructs a named TimeWindowProperty using two TimeProperties.
	 * 
	 * @param name the name
	 * @param startValue the start TimeProperty
	 * @param endValue the end TimeProperty
	 */
	public TimeWindowProperty(String name, TimeProperty startValue, TimeProperty endValue) {
		this(name);
		setStartTime(startValue);
		setEndTime(endValue);
	}

	/**
	 * Gets the starting time of this TimeWindowProperty.
	 * 
	 * @return the time
	 */
	public TimeProperty getStartTime() {
		return startTime;
	}

	/**
	 * Sets the starting time of this TimeWindowProperty.
	 * 
	 * @param startTime the start time
	 */
	public void setStartTime(TimeProperty startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the end time of this TimeWindowProperty.
	 * 
	 * @return the time
	 */
	public TimeProperty getEndTime() {
		return endTime;
	}

	/**
	 * Sets the ending time of this TimeWindowProperty.
	 * 
	 * @param endTime the end time
	 */
	public void setEndTime(TimeProperty endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return startTime.toString() + " - " + endTime.toString();
	}

	@Override
	public void parseString(String str) {
		String[] split = str.split(" - ");
		if (split.length >= 2) {
			startTime = new TimeProperty("",split[0].trim());
			endTime = new TimeProperty("",split[1].trim());
		}
	}

	public static String parseHint() {
		return TimeProperty.parseHint() + " - " + TimeProperty.parseHint();
	}
	
	public String getIcon(){
		return "datetime.png";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + "End NUMERIC";
	}

	@Override
	public String getArffData(AgentViewable av) {
		return endTime.getArffData(av);
	}

	@Override
	public String toInformativeString() {
		String startString = (startTime == null ? "" : startTime.toInformativeString());
		String endString = (endTime == null ? "" : endTime.toInformativeString());
		if (startString.equals(endString)) {
			return startString;
		}
		return startString + " - " + endString;
	}
}