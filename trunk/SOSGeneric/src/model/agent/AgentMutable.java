package model.agent;

import java.util.HashMap;

import model.agent.property.Property;
import util.enums.PropertyType;

public interface AgentMutable extends AgentViewable {

	/**
	 * Gets a property contained in this PropertyObject. 
	 * 
	 * @param name the name of the property
	 * @return the property
	 */
	public Property getProperty(String name);
	
	/**
	 * TODO!!!
	 * @param pt
	 * @param name
	 * @param value
	 */
	public void set(PropertyType pt, String name, String value);

	/**
	 * Adds the properties of an existing PropertyObject to this PropertyObject, and sets the corresponding AgentCollectionView, AgentView and record history.
	 * 
	 * @param o the existing PropertyObject
	 * @param acv the AgentCollectionView
	 * @param av the AgentView
	 */
	public void putProperties(HashMap<String, Property> properties);

	/**
	 * Gets the properties of this PropertyObject.
	 * 
	 * @return properties the properties
	 */
	public HashMap<String, Property> getProperties();

	/**
	 * Replaces a property or adds it to this PropertyObject.
	 * 
	 * @param newP the new property
	 */
	public void putProperty(Property newP);
	/**
	 * Removes a property from this PropertyObject.
	 * 
	 * @param name the name of the property
	 */
	public void removeProperty(String name);

	/**
	 * Add an identifier to a dependencies property of this PropertyObject.
	 * 
	 * @param name the name dependencies property
	 * @param id the id to be added
	 * @return success
	 */
	public boolean addIDToDependenciesProperty(String name, String id);
	
	/**
	 * Removes an identifier from a dependencies property of this PropertyObject.
	 * 
	 * @param name the name dependencies property
	 * @param id the id to be removed
	 * @return success
	 */
	public boolean removeIDFromDependenciesProperty(String name, String id);
	
	public boolean save();
	
	public boolean delete();
}