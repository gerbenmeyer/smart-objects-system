package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class NumberProperty extends Property {

	private double number;

	public NumberProperty(String name) {
		super(name,PropertyType.NUMBER);
	}

	/**
	 * 
	 * @param number
	 */
	public NumberProperty(String name, double value) {
		this(name);
		setNumber(value);
	}

	/**
	 * @return the number
	 */
	public double getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(double number) {
		this.number = number;
		mutateHistory();
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
		mutateHistory();
	}

	public static String parseHint() {
		return "number";
	}

	@Override
	public String arffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String arffData() {
		return Double.toString(getNumber());
	}
}