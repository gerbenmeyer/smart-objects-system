package model.agent.property.properties;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class TextProperty extends Property {

	private String text;

	/**
	 * 
	 * @param name
	 */
	public TextProperty(String name) {
		super(name, PropertyType.TEXT);
	}

	/**
	 * @param text
	 */
	public TextProperty(String name, String text) {
		this(name);
		setText(text);
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
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