package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation for the boolean datastructure. 
 * 
 * @author Gerben G. Meyer
 */
public class BooleanProperty extends Property {

	private boolean value;

	/**
	 * Constructs a new named BooleanProperty instance.
	 * 
	 * @param name the name
	 */
	public BooleanProperty(String name) {
		super(name, PropertyType.BOOLEAN);
	}

	/**
	 * Constructs a new named BooleanProperty instance with a value.
	 * 
	 * @param name the name
	 * @param value the value
	 */
	public BooleanProperty(String name, boolean value) {
		this(name);
		setValue(value);
	}

	/**
	 * Gets the value of this BooleanProperty.
	 * 
	 * @return the value
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Sets the value of this BooleanProperty and mutates its history.
	 * 
	 * @param value the boolean value
	 */
	public void setValue(boolean value) {
		this.value = value;
		mutateHistory();
	}

	@Override
	public String toString() {
		return Boolean.toString(this.value);
	}

	@Override
	public String toInformativeString() {
		return this.value ? "yes" : "no";
	}

	@Override
	public void parseString(String str) {
		this.value = Boolean.parseBoolean(str);
		mutateHistory();
	}

	public static String parseHint() {
		return "value";
	}

	@Override
	public String arffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " {" + Boolean.toString(true) + ","
				+ Boolean.toString(false) + "}";
	}

	@Override
	public String arffData() {
		return Boolean.toString(getValue());
	}
}