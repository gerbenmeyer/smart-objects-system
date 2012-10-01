package model.agent;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import data.agents.AgentCollectionStorage;

/**
 * This interface ensures that an Agent has the methods to be mutated.
 * 
 * @author W.H. Mook
 */
public interface AgentMutable extends AgentViewable {

	/**
	 * Set the readBuffer of this Agent
	 * 
	 * @param properties
	 *            the properties to be set
	 */
	public void setReadBuffer(Map<String, Property> properties);
	/**
	 * Set the location of this Agent
	 * 
	 * @param location
	 *            the location to be set
	 */
	public void setLocation(LocationProperty location);
	/**
	 * Set the status of this Agent
	 * 
	 * @param status
	 *            the status to be set
	 */
	public void setStatus(AgentStatus status);
	/**
	 * Gets a property of this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @return the property
	 */
	public Property getProperty(String name);

	/**
	 * Sets the property for this Agent.
	 * 
	 * @param pt
	 *            the type of property
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void set(PropertyType pt, String name, String value);

	/**
	 * Sets a text property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setText(String name, String value);

	/**
	 * Sets an integer property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setInt(String name, int value);

	/**
	 * Sets a boolean property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setBool(String name, boolean value);

	/**
	 * Sets an object property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setObject(String name, Object value);

	/**
	 * Sets a number property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setNumber(String name, double value);

	/**
	 * Sets a time property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setTime(String name, GregorianCalendar value);
	/**
	 * Sets a time property for this Agent to the current time.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void setTimeNow(String name);	
	/**
	 * Initializes the property for this Agent.
	 * 
	 * @param pt
	 *            the type of property
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void init(PropertyType pt, String name, String value);
	/**
	 * Initializes an integer property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void initInt(String name, int value);
	/**
	 * Initializes a boolean property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void initBool(String name, boolean value);	
	/**
	 * Initializes a number property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void initNumber(String name, double value);
	/**
	 * Initializes a text property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void initText(String name, String value);
	/**
	 * Initializes an object property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void initObject(String name, Object value);
	/**
	 * Initializes an time property for this Agent with the current time.
	 * 
	 * @param name
	 *            the name of the property
	 */
	public void initTimeNow(String name);
	/**
	 * Initializes an time property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */	
	public void initTime(String name, GregorianCalendar value);
	/**
	 * Initializes an dependency property for this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 */
	public void initDependency(String name);	

	/**
	 * Adds properties to this Agent.
	 * 
	 * @param properties
	 *            the properties to be added
	 */
	public void putProperties(Map<String, Property> properties);
	/**
	 * Gets the properties of this Agent.
	 * 
	 * @return properties the properties
	 */
	public HashMap<String, Property> getProperties();

	/**
	 * Replaces a property or adds it to this Agent.
	 * 
	 * @param newP
	 *            the new property
	 */
	public void putProperty(Property newP);

	/**
	 * Removes a property from this Agent.
	 * 
	 * @param name
	 *            the name of the property
	 */
	public void removeProperty(String name);

	/**
	 * Add an identifier to a dependencies property of this Agent.
	 * 
	 * @param name
	 *            the name dependencies property
	 * @param id
	 *            the id to be added
	 * @return success
	 */
	public boolean addDependencyID(String name, String id);

	/**
	 * Removes an identifier from a dependencies property of this Agent.
	 * 
	 * @param name
	 *            the name dependencies property
	 * @param id
	 *            the id to be removed
	 * @return success
	 */
	public boolean removeDependencyID(String name, String id);

	/**
	 * Saves the agent in the {@link AgentCollectionStorage}
	 */
	public void save();

	/**
	 * Deletes the agent. It is removed from the collection and its properties
	 * are deleted.
	 */
	public void delete();
}