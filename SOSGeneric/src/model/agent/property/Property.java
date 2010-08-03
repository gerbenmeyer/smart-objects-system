/**
 * 
 */
package model.agent.property;

import java.text.Normalizer;
import java.util.HashMap;

import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import model.agent.property.properties.BooleanProperty;
import model.agent.property.properties.DependenciesProperty;
import model.agent.property.properties.LocationProperty;
import model.agent.property.properties.NumberProperty;
import model.agent.property.properties.StatusProperty;
import model.agent.property.properties.TextProperty;
import model.agent.property.properties.TimeProperty;
import model.agent.property.properties.TimeWindowProperty;
import util.enums.PropertyType;
import util.htmltool.HtmlMapContentGenerator;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

/**
 * An abstract class for holding and creating properties of a PropertyObject.
 * All properties must extend this class.
 * 
 * @author Gerben G. Meyer
 */
public abstract class Property {

	private PropertyType propertyType = PropertyType.UNKNOWN;
	private String name;
	private AgentCollectionView agentCollectionView;
	private AgentView agentView;
	private PropertyHistory history = null;
	private boolean hidden = false;

	/**
	 * Constructs a new Property object.
	 * 
	 * @param name the property's name
	 * @param propertyType the property's type
	 */
	public Property(String name, PropertyType propertyType) {
		super();
		this.name = name;
		this.propertyType = propertyType;
	}

	/**
	 * Enables recording history of this property.
	 */
	public void recordHistory() {
		recordHistory(new PropertyHistory(this));
	}

	/**
	 * Enables recording history of this property using an existing history.
	 * 
	 * @param history the history object to be used for recording changes
	 */
	public void recordHistory(PropertyHistory history) {
		this.history = history;
		if (this.history != null) {
			this.history.mutate();
		}
	}

	/**
	 * True if history is being recorded for this property.
	 * 
	 * @return true or false
	 */
	public boolean recordingHistory() {
		return history != null;
	}

	/**
	 * Gets the AgentView of the agent to which this property belongs to.
	 * 
	 * @return the AgentView
	 */
	public AgentView getAgentView() {
		return agentView;
	}

	/**
	 * Sets the AgentCollectionView for this property.
	 * 
	 * @param acv the AgentCollectionView to be set
	 */
	public void setAgentCollectionView(AgentCollectionView acv) {
		this.agentCollectionView = acv;
	}

	/**
	 * Sets the AgentView for this property.
	 * 
	 * @param av the AgentView to be set
	 */
	public void setAgentView(AgentView av) {
		this.agentView = av;
	}

	/**
	 * Get the history of this property.
	 * 
	 * @return the history
	 */
	public PropertyHistory getHistory() {
		return history;
	}

	/**
	 * A protected method to be called when a property has been changed.
	 */
	protected void mutateHistory() {
		if (history != null) {
			history.mutate();
		}
	}

	/**
	 * Gets the AgentCollectionView of this property.
	 * 
	 * @return the AgentCollectionView
	 */
	public AgentCollectionView getAgentCollectionView() {
		return agentCollectionView;
	}

	/**
	 * Gets the property type of this property.
	 * 
	 * @return the type
	 */
	public PropertyType getPropertyType() {
		return propertyType;
	}

	/**
	 * Gets the name of this property.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This returns the string representation of this property. Can be used for
	 * serialization, in conjuction with the {@link #parseString(String str)}
	 * method.
	 * 
	 * @return the string representation of this property
	 */
	public abstract String toString();

	/**
	 * Get the humanly readable representation of this property.
	 * 
	 * @return the informative string
	 */
	public abstract String toInformativeString();

	/**
	 * Parses a string representation into this property. Can be used for
	 * serialization, in conjuction with the {@link #toString()} method.
	 * 
	 * @param str the string to be parsed
	 */
	public abstract void parseString(String str);

	/**
	 * Gives the format in which the {@link #toString()} method gives the
	 * representation of the property. This is also the format that is used by
	 * {@link #parseString(String str)}.
	 * 
	 * @return the string format
	 */
	public static String parseHint() {
		return "";
	}
	/**
	 * Get the Arff attributes declaration of this property.
	 * 
	 * @return the Arff attributes data
	 */
	public String arffAttributeDeclaration() {
		return null;
	}

	/**
	 * Get the Arff data of this property.
	 * 
	 * @return the Arff data
	 */
	public String arffData() {
		return null;
	}

	/**
	 * Get the XML representation of this property.
	 * 
	 * @return the XML
	 */
	public String toXML() {
		KeyDataVector properties = new KeyDataVector();
		properties.add(new KeyData("Type", this.getPropertyType().toString()));
		properties.add(new KeyData("Name", this.getName()));

		String value = this.toString();
		// try {
		// value = URLEncoder.encode(value,"UTF-8");
		// } catch (UnsupportedEncodingException e1) {
		// e1.printStackTrace();
		// }
		// value = HTMLEntities.htmlQuotes(value);
		value = value.replaceAll("\n", "<newline/>");
		value = HTMLEntities.htmlentities(value);
		value = HTMLEntities.htmlQuotes(value);
		value = HTMLEntities.htmlAngleBrackets(value);
		// try {
		// value = URLDecoder.decode(value,"UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		properties.add(new KeyData("Value", value));

		properties
				.add(new KeyData("Hidden", Boolean.toString(this.isHidden())));

		return XMLTool.addRootTag(XMLTool.PropertiesToXML(properties),
				"Property");
	}
	
	/**
	 * Converts the xml representation of a property to a Property instance.
	 * 
	 * @param xml the representation
	 * @return the Property
	 */
	public static Property fromXML(String xml) {
		xml = XMLTool.removeRootTag(xml);
		KeyDataVector properties = XMLTool.XMLToProperties(xml);
		String type = properties.getValue("Type");
		String name = properties.getValue("Name");
		String value = properties.getValue("Value");
		value = HTMLEntities.unhtmlAngleBrackets(value);
		value = HTMLEntities.unhtmlQuotes(value);
		value = HTMLEntities.unhtmlentities(value);
		value = value.replaceAll("<newline/>", "\n");
		boolean hidden = Boolean.parseBoolean(properties.getValue("Hidden"));
		Property p = null;
		try {
			PropertyType pt = PropertyType.valueOf(type);
			p = createProperty(pt, name, value);
			p.setHidden(hidden);
		} catch (Exception e) {
			System.err.println("Niet goed! " + type + " - " + name + " - "
					+ value);
		}
		return p;
	}
	
	/**
	 * Creates a new Property instance without a name and history recording.
	 * 
	 * @param type the type of property
	 * @param value the value of the property
	 * @return a fresh Property
	 */
	public static Property createProperty(PropertyType type, String value) {
		return createProperty(type, "", value, false);
	}
	
	/**
	 * Creates a new Property instance without history recording.
	 * 
	 * @param type the type of property
	 * @param name the name of the property
	 * @param value the value of the property
	 * @return a fresh Property
	 */
	public static Property createProperty(PropertyType type, String name,
			String value) {
		return createProperty(type, name, value, false);
	}
	
	/**
	 * Creates a new Property instance.
	 * 
	 * @param type the type of Property
	 * @param name the name of the property
	 * @param value the value of the property
	 * @param recordHistory true if history should be recorded for this property
	 * @return a fresh Property
	 */
	public static Property createProperty(PropertyType type, String name,
			String value, boolean recordHistory) {
		Property p = null;
		switch (type) {
		case BOOLEAN:
			p = new BooleanProperty(name);
			break;
		case NUMBER:
			p = new NumberProperty(name);
			break;
		case TEXT:
			p = new TextProperty(name);
			break;
		case TIME:
			p = new TimeProperty(name);
			break;
		case TIMEWINDOW:
			p = new TimeWindowProperty(name);
			break;
		case LOCATION:
			p = new LocationProperty(name);
			break;
		case STATUS:
			p = new StatusProperty(name);
			break;
		case DEPENDENCIES:
			p = new DependenciesProperty(name);
			break;
		default:
			System.err.println("Unknown property type: " + type);
			break;
		}
		if (p != null) {
			if (value.length() > 0) {
				try {
					if (recordHistory) {
						p.recordHistory();
					}
					p.parseString(value);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unknown property value: " + value);
				}
			}
		}
		return p;
	}

	/**
	 * Get the icon of the property.
	 * 
	 * @return the icon
	 */
	public String getIcon() {
		return "info.png";
	}

	/**
	 * Returns the HTML formatted string containing the history of this property.
	 * 
	 * @return the HTML string
	 */
	public String toHistoryHTML() {
		if (recordingHistory()) {
			String historyId = "history"
					+ Math.round((Math.random() * 1000000));
			if (recordingHistory()) {
				return "<div class=\"historytoggle\"><img src=\"clock.png\" title=\"Toggle history\" width=\"16\" height=\"16\" onclick=\"if (document.getElementById('"
						+ historyId
						+ "').style.display =='none') { document.getElementById('"
						+ historyId
						+ "').style.display = 'inline'; } else { document.getElementById('"
						+ historyId
						+ "').style.display = 'none'; }; \"/></div>"
						+ "<div class=\"history\" id=\""
						+ historyId
						+ "\" style=\"display: none\">"
						+ "<h3>History</h3>"
						+ "<div class=\"propertyheader\">"
						+ "<div class=\"propertyicon\"></div>"
						+ "<div class=\"propertyname\">Time</div>"
						+ "<div class=\"propertyvalue\">Name</div>"
						+ "</div>"
						+ history.toHTML() + "</div>";
			}
		}
		return "";
	}

	/**
	 * Returns the property specific javascript to be used for map generation.
	 * 
	 * @param mapContent a content generator for the map
	 * @param params the request parameters
	 */
	public void toScript(HtmlMapContentGenerator mapContent,
			HashMap<String, String> params) {
	}

	/**
	 * Normalizes a string to ASCII characters.
	 * 
	 * @param address the string to be normalized
	 * @return the normalized string
	 */
	public static String normalize(String address) {
		address = address.toLowerCase().trim();
		address = address.replaceAll("\\s+", " ");
		address = Normalizer.normalize(address, Normalizer.Form.NFKD);
		address = address.replaceAll("[^\\p{ASCII}]", "");
		return address;
	}

	/**
	 * True if this property should be hidden in the view.
	 * 
	 * @return true or false
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * set to true if this property should be hidden in the view.
	 * 
	 * @param hideHTML true or false
	 */
	public void setHidden(boolean hideHTML) {
		this.hidden = hideHTML;
	}
}