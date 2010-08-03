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
 * 
 * @author Gerben Meyer
 * 
 */
public class KeyData {

	private String key;
	private String value;

	/**
	 * Constructor
	 * 
	 * @param tag
	 * @param value
	 */
	public KeyData( String key , String value ) {
		super();
		setKey(key);
		setValue(value);
	}

	/**
	 * Same as getKey
	 * 
	 * @return Returns the key.
	 */
	public String getTag() {
		return getKey();
	}

	/**
	 * Same as setKey
	 * 
	 * @param tag
	 *            The key to set.
	 */
	public void setTag( String tag ) {
		setKey( tag );
	}

	/**
	 * @return Returns the key.
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
	 * @param key
	 *            The key to set.
	 */
	public void setKey( String key ) {
		try {
			this.key = URLEncoder.encode(key,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the value.
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
	 * @param value
	 *            The value to set.
	 */
	public void setValue( String value ) {
		try {
			this.value = URLEncoder.encode(value,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return getKey()+":"+getValue();
	}
	
//	public boolean equals( KeyData< Object ,  Object > kd){
//		return kd.getKey().equals(this.getKey()) && kd.getValue().equals(this.getValue());
//	}

}
