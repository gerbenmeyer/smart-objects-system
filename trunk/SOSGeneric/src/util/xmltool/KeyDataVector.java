/*
 * Created on 24-aug-2006
 *
 * Copyright 2005 The Agent Laboratory, Rijksuniversiteit Groningen
 */
package util.xmltool;

import java.util.Vector;

/**
 * An extension of Vector with the purpose of holding KeyData objects.
 * Provides methods for easy management of the KeyData.
 * 
 * @author Gerben G. Meyer
 */
public class KeyDataVector extends Vector<KeyData> {

	private static final long serialVersionUID = 1L;

	/**
	 * Get all keys used in this KeyDataVector.
	 * 
	 * @return the keys
	 */
	public Vector<String> getKeys() {
		Vector<String> keys = new Vector<String>();
		for (KeyData kd : this) {
			keys.add(kd.getKey());
		}
		return keys;
	}

	/**
	 * Get the value for a certain key.
	 * 
	 * @param key the key
	 * @return the value
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
	 * exists, otherwise it will be added.
	 * 
	 * @param key the key
	 * @param value the value
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
	 * Removes the KeyData object with a certain key from the collection.
	 * 
	 * @param key the key to be removed
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
	
	/**
	 * The text representation of this KeyDataVector.
	 * 
	 * @return the text
	 */
	public String toString(){
		String result = "";
		for (KeyData item: this){
			result += item.toString();
			result += "\n";
		}
		return result;
	}
}