package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation holding a number. 
 * 
 * @author Gerben G. Meyer
 */
public class NumberProperty extends Property {

	private double number;

	/**
	 * Constructs a named NumberProperty instance.
	 * 
	 * @param name the name
	 */
	public NumberProperty(String name) {
		super(name,PropertyType.NUMBER);
	}

	/**
	 * Constructs a named NumberProperty instance with a value 
	 * 
	 * @param name the name
	 * @param value the value
	 */
	public NumberProperty(String name, double value) {
		this(name);
		setNumber(value);
	}

	/**
	 * Returns the double value of this NumberProperty.
	 * 
	 * @return the number
	 */
	public double getNumber() {
		return number;
	}

	/**
	 * Sets the double value of this NumberProperty.
	 * 
	 * @param number the number to be set
	 */
	public void setNumber(double number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "" + this.number;
	}

	@Override
	public String toInformativeString() {
		return ""+Math.round(this.number * 100.0) / 100.0;
	}

	@Override
	public void parseString(String str) {
		this.number = Double.parseDouble(str);
	}

	public static String parseHint() {
		return "number";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String getArffData() {
		return Double.toString(getNumber());
	}
}