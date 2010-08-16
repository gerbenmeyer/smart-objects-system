/*
 * Created on 14-mrt-2006
 *
 * Copyright 2005 The Agent Laboratory, Rijksuniversiteit Groningen
 */
package util.xmltool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Data structure holding a key value pair used in XML.
 * 
 * @author Gerben Meyer
 */
public class KeyData {

	private String key;
	private String value;

	/**
	 * Constructs a new KeyData instance.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public KeyData( String key , String value ) {
		super();
		setKey(key);
		setValue(value);
	}

	/**
	 * Same as {@link #getKey()}.
	 * 
	 * @return the key
	 */
	public String getTag() {
		return getKey();
	}

	/**
	 * Same as {@link #setKey(String)}.
	 * 
	 * @param tag the key to set
	 */
	public void setTag( String tag ) {
		setKey( tag );
	}

	/**
	 * Get the key of this KeyData.
	 * 
	 * @return the key.
	 */
	public String getKey() {
		try {
			return URLDecoder.decode(key,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the key of this KeyData.
	 * 
	 * @param key the key to set
	 */
	public void setKey( String key ) {
		try {
			this.key = URLEncoder.encode(key,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the value of this KeyData.
	 * 
	 * @return the value
	 */
	public String getValue() {
		try {
			return URLDecoder.decode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the value of this KeyData.
	 * 
	 * @param value the value to set
	 */
	public void setValue( String value ) {
		try {
			this.value = URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The text representation of this KeyData.
	 * 
	 * @return the key and value
	 */
	public String toString(){
		return getKey()+":"+getValue();
	}
	
//	public boolean equals( KeyData< Object ,  Object > kd){
//		return kd.getKey().equals(this.getKey()) && kd.getValue().equals(this.getValue());
//	}
}