package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class TimeWindowProperty extends Property {

	private TimeProperty startTime = new TimeProperty();
	private TimeProperty endTime = new TimeProperty();

	/**
	 * 
	 * @param name
	 * @param pov
	 * @param pocv
	 */
	public TimeWindowProperty(String name) {
		super(name, PropertyType.TIMEWINDOW);
	}

	/**
	 * 
	 * @param value
	 */
	public TimeWindowProperty(String name, String value) {
		this(name);
		parseString(value);
	}

	public TimeWindowProperty(String name, TimeProperty startValue, TimeProperty endValue) {
		this(name);
		setStartTime(startValue);
		setEndTime(endValue);
	}

	/**
	 * @return the startTime
	 */
	public TimeProperty getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(TimeProperty startTime) {
		this.startTime = startTime;
		mutateHistory();
	}

	/**
	 * @return the endTime
	 */
	public TimeProperty getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(TimeProperty endTime) {
		this.endTime = endTime;
		mutateHistory();
	}

	@Override
	/*
	 * *
	 */
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
		mutateHistory();
	}

	public static String parseHint() {
		return TimeProperty.parseHint() + " - " + TimeProperty.parseHint();
	}
	
	public String getIcon(){
		return "datetime.png";
	}

	@Override
	public String arffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + "End NUMERIC";
	}

	@Override
	public String arffData() {
		return endTime.arffData();
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
