package model.agent;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

/**
 * This interface must be implemented by every agent to provide a number of getters 
 */

public interface AgentView {
	/**
	 * Get the identifier of this agent.
	 * 
	 * @return the ID
	 */
	public String getID();
	/**
	 * Get the label of this agent.
	 * 
	 * @return the label
	 */
	public String getLabel();
	/**
	 * Get the status of this agent.
	 * 
	 * @return the AgentStatus
	 */
	public AgentStatus getStatus();
	/**
	 * Get the type of this agent.
	 * 
	 * @return the type
	 */
	public String getType();
	/**
	 * Get the description of this agent.
	 * 
	 * @return the description
	 */
	public String getDescription();
	/**
	 * Get the location of this agent.
	 * 
	 * @return the location
	 */
	public String getLocation();
	/**
	 * True if this agent is set to be hidden in the view.
	 * 
	 * @return the boolean
	 */
	public boolean isHidden();
	/**
	 * Get the type of a certain property of this agent.
	 * 
	 * @param name the name of the property
	 * @return the PropertyType
	 */
	public PropertyType getPropertyType(String name);
	/**
	 * Get the value of a certain property of this agent.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public String getPropertyValue(String name);
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
	 * Get a vector containing the IDs of all dependant Agents of the requested property.
	 * 
	 * @param name the name of the property
	 * @return a vector
	 */
	public Vector<String> getIDsFromDependenciesProperty(String name);
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
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params);
	/**
	 * Adds the agent specific map content to the map content generator. Request parameters may be passed through.
	 * 
	 * @param mapContent the generator
	 * @param params the request parameters
	 */
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String,String> params);
}