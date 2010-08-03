/*
 * Created on 24-aug-2006
 *
 * Copyright 2005 The Agent Laboratory, Rijksuniversiteit Groningen
 */
package util.xmltool;

import java.util.Vector;

/**
 * 
 * @author Gerben Meyer
 * 
 * @param <A>
 * @param <B>
 */
public class KeyDataVector extends Vector<KeyData> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Gets all keys used in this vector
	 * 
	 * @return Vector of keys
	 */
	public Vector<String> getKeys() {
		Vector<String> keys = new Vector<String>();
		for (KeyData kd : this) {
			keys.add(kd.getKey());
		}
		return keys;
	}

	/**
	 * Get the value for a certain key
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		for (KeyData kd : this) {
			if (kd.getKey().equals(key)) {
				return kd.getValue();
			}
		}
		return null;
	}

	/**
	 * Sets the value for a certain key, replaces the value if the key already
	 * exists, otherwise it will add it
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		for (KeyData kd : this) {
			if (kd.getKey().equals(key)) {
				kd.setValue(value);
				return;
			}
		}
		this.add(new KeyData(key, value));
	}

	/**
	 * Removes the entries from the problemdata with key key
	 * 
	 * @param key
	 */
	public void deleteKey(String key) {
		Vector<KeyData> toDelete = new Vector<KeyData>();
		for (KeyData kd : this) {
			if (kd.getKey().equals(key)) {
				toDelete.add(kd);
			}
		}
		this.removeAll(toDelete);
	}
	
	public String toString(){
		String result = "";
		for (KeyData item: this){
			result += item.toString();
			result += "\n";
		}
		return result;
	}

}
