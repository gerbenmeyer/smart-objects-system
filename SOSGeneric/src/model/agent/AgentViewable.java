package model.agent;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import model.agent.property.properties.LocationProperty;

import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

/**
 * This interface must be implemented by every agent to provide a number of getters 
 */

public interface AgentViewable {
	/**
	 * Get the type of a certain property of this agent.
	 * 
	 * @param name the name of the property
	 * @return the PropertyType
	 */
	public PropertyType getPropertyType(String name);
	/**
	 * @return the needsDetailsPane
	 */
	public boolean needsDetailsPane();
	/**
	 * @return the identifier
	 */
	public String getID();
	/**
	 * @return the status of the agent
	 */
	public AgentStatus getStatus();
	/**
	 * @return the location of the agent
	 */
	public LocationProperty getLocation();
	/**
	 * Get the value of a certain property of this agent.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public String get(String name);
	/**
	 * Get the value of a certain property of this agent parsed to an Integer value.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public int getInt(String name);
	/**
	 * Get the value of a certain property of this agent parsed to a Double value.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public double getNumber(String name);
	/**
	 * Get the value of a certain property of this agent parsed to a Boolean value.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public boolean getBool(String name);		
	/**
	 * Get the value of a certain property of this agent parsed to a String.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public String getText(String name);		
	/**
	 * Get the value of a certain property of this agent parsed to a time.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public GregorianCalendar getTime(String name);
	/**
	 * Get the value of a certain property of this agent parsed to an object.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public Object getObject(String name);		
	/**
	 * Get the humanly readable representation of a certain property of this agent.
	 * 
	 * @param name the name of the property
	 * @return the informative string
	 */
	public String getPropertyInformativeString(String name);
	/**
	 * Get a set containing the keys of all properties of this agent.
	 * 
	 * @return the set
	 */
	public Set<String> getPropertiesKeySet();

	/**
	 * Get a vector containing the IDs of all Agents of the requested dependency property.
	 * 
	 * @param name the name of the property
	 * @return a vector
	 */
	public Vector<String> getDependencyIDs(String name);	
	/**
	 * Get the icon of the agent.
	 * 
	 * @return the icon
	 */
	public String getIcon();
	/**
	 * Get the image to be used for a map marker of the agent.
	 * 
	 * @return the image
	 */
	public String getMapMarkerImage();
	/**
	 * Get the XML representation of this agent.
	 * 
	 * @return the XML
	 */
	public String toXML();
	/**
	 * Get the Arff attributes declaration of the agent.
	 * 
	 * @return the Arff attributes data
	 */
	public String getArffAttributesString();
	/**
	 * Get the Arff instance of the agent.
	 * 
	 * @return the Arff instance data
	 */
	public String getArffInstanceString();
	/**
	 * Adds the agent specific HTML to a details pane generator. Request parameters may be passed through.
	 * 
	 * @param detailsPane the generator
	 * @param params the request parameters
	 */
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params);
	/**
	 * Adds the agent specific map content to the map content generator. Request parameters may be passed through.
	 * 
	 * @param mapContent the generator
	 * @param params the request parameters
	 */
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params);
	/**
	 * Adds the content of a information balloon associated with a map marker,
	 * to be shown on the map when the maker is clicked.
	 * This method should be overridden by agents requiring their own custom balloon.
	 * 
	 * @param balloonContent the generator
	 * @param params the request parameters
	 */
	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String,String> params);
	/**
	 * Learn the agent a new status, based on http request params.
	 * 
	 * @param content the generator which can be used for a result
	 * @param params the request parameters
	 */
	public void teachStatus(HtmlMapContentGenerator content, HashMap<String,String> params);	
}