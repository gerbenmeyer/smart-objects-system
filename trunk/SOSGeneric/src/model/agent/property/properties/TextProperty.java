package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation holding text.
 * 
 * @author Gerben G. Meyer
 */
public class TextProperty extends Property {

	private String text;

	/**
	 * Constructs a named TextProperty.
	 * 
	 * @param name the name
	 */
	public TextProperty(String name) {
		super(name, PropertyType.TEXT);
	}

	/**
	 * Constructs a named TextProperty with a value.
	 * 
	 * @param name the name
	 * @param text the text value
	 */
	public TextProperty(String name, String text) {
		this(name);
		setText(text);
	}

	/**
	 * Gets the text of this TextProperty.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of this TextProperty.
	 * 
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
		mutateHistory();
	}

	@Override
	public String toString() {
		if (text == null) {
			return "";
		} else {
			return this.text;
		}
	}
	
	@Override
	public void parseString(String str) {
		this.text = str;
		mutateHistory();
	}

	public static String parseHint() {
		return "text";
	}
	
	public String getIcon(){
		return "text.png";
	}

	@Override
	public String toInformativeString() {
		return toString();
	}
}