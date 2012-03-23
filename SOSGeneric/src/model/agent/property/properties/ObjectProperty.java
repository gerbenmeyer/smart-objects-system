package model.agent.property.properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import main.SOSServer;
import model.agent.AgentViewable;
import model.agent.property.Property;
import util.Base64Coder;
import util.enums.PropertyType;

/**
 * A Property implementation holding an object.
 * 
 * @author Gerben G. Meyer
 */
public class ObjectProperty extends Property {

	private String text;

	/**
	 * Constructs a named ObjectProperty.
	 * 
	 * @param name
	 *            the name
	 */
	public ObjectProperty(String name) {
		super(name, PropertyType.OBJECT);
	}

	/**
	 * Constructs a named ObjectProperty with an object.
	 * 
	 * @param name
	 *            the name
	 * @param object
	 *            the object
	 */
	public ObjectProperty(String name, Serializable object) {
		this(name);
		setObject(object);
	}

	/**
	 * Gets the object of this ObjectProperty.
	 * 
	 * @return the object
	 */
	public Object getObject() {
		return objectFromString(text);
	}

	/**
	 * Sets the object of this ObjectProperty.
	 * 
	 * @param object
	 *            the object
	 */
	public void setObject(Object object) {
		this.text = objectToString(object);
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
	}

	public static String parseHint() {
		return "object as string";
	}

	public String getIcon() {
		return "info.png";
	}

	@Override
	public String toInformativeString() {
		return "Object";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return null;
	}

	@Override
	public String getArffData(AgentViewable av) {
		return null;
	}

	/** Read the object from Base64 string. */
	public static Object objectFromString(String s) {
		Object o = null;
		try {
			byte[] data = Base64Coder.decode(s);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			o = ois.readObject();
			ois.close();
		} catch (Exception e) {
			SOSServer.getDevLogger().warning("Unable to convert string to object: " + e.getMessage());
		}
		return o;
	}

	/** Write the object to a Base64 string. */
	public static String objectToString(Object o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return new String(Base64Coder.encode(baos.toByteArray()));
		} catch (Exception e) {
			SOSServer.getDevLogger().warning("Unable to convert object to string: " + e.getMessage());
		}
		return null;
	}
}