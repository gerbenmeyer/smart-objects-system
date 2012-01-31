package model.agent.property.properties;

import model.agent.AgentViewable;
import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation holding a number. 
 * 
 * @author Gerben G. Meyer
 */
public class IntegerProperty extends Property {

	private int number;

	/**
	 * Constructs a named IntegerProperty instance.
	 * 
	 * @param name the name
	 */
	public IntegerProperty(String name) {
		super(name,PropertyType.NUMBER);
	}

	/**
	 * Constructs a named IntegerProperty instance with a value 
	 * 
	 * @param name the name
	 * @param value the value
	 */
	public IntegerProperty(String name, int value) {
		this(name);
		setNumber(value);
	}

	/**
	 * Returns the integer value of this IntegerProperty.
	 * 
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the integer value of this IntegerProperty.
	 * 
	 * @param number the number to be set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "" + this.number;
	}

	@Override
	public String toInformativeString() {
		return ""+this.number;
	}

	@Override
	public void parseString(String str) {
		this.number = Integer.parseInt(str);
	}

	public static String parseHint() {
		return "integer";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String getArffData(AgentViewable av) {
		return Integer.toString(getNumber());
	}
}