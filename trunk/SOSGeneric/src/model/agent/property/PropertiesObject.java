package model.agent.property;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import model.agent.property.properties.DependenciesProperty;
import model.agent.property.properties.LocationProperty;
import util.enums.AgentStatus;
import util.enums.GoogleLocationType;
import util.enums.PropertyType;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * A class for holding a collection of properties and methods to access them.
 * 
 * @version 31-mrt-2009
 * 
 */
public class PropertiesObject {

	private HashMap<String, Property> properties;

	/**
	 * Constructs a new PropertiesObject instance.
	 */
	public PropertiesObject() {
		super();
		properties = new HashMap<String, Property>();
	}

	/**
	 * Constructs a new PropertiesObject instance with a certain identifier.
	 * 
	 * @param the identifier
	 */
	public PropertiesObject(String id) {
		this();
		setID(id);
		setLabel(id);

	}

	/**
	 * Adds the properties of an existing PropertyObject to this PropertyObject, and sets the corresponding AgentCollectionView and AgentView.
	 * 
	 * @param o the existing PropertyObject
	 * @param acv the AgentCollectionView
	 * @param av the AgentView
	 */
	public void putProperties(PropertiesObject o, AgentCollectionView acv, AgentView av) {
		putProperties(o, acv, av, false);
	}

	/**
	 * Adds the properties of an existing PropertyObject to this PropertyObject, and sets the corresponding AgentCollectionView, AgentView and record history.
	 * 
	 * @param o the existing PropertyObject
	 * @param acv the AgentCollectionView
	 * @param av the AgentView
	 * @param recordHistory if the history of each property should be recorded
	 */
	public void putProperties(PropertiesObject o, AgentCollectionView acv, AgentView av, boolean recordHistory) {
		for (String name : o.getPropertiesKeySet()) {
			Property p = o.getProperty(name);
			p.setAgentCollectionView(acv);
			p.setAgentView(av);
			if (recordHistory) {
				p.recordHistory();
			}
			this.putProperty(p);
		}
	}

	/**
	 * Gets the identifier of this PropertyObject.
	 * 
	 * @return the id
	 */
	public String getID() {
		return getPropertyValue("ID");
	}

	/**
	 * Sets the identifier of this PropertyObject.
	 * 
	 * @param id the identifier
	 */
	public void setID(String id) {
		putProperty(Property.createProperty(PropertyType.TEXT, "ID", id));
	}

	/**
	 * Gets the label of this PropertyObject.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return getPropertyValue("Label");
	}

	/**
	 * Sets the label of this PropertyObject.
	 * 
	 * @param label the label
	 */
	public void setLabel(String label) {
		putProperty(Property.createProperty(PropertyType.TEXT, "Label", label));
	}

	/**
	 * Gets the type of this PropertyObject.
	 * 
	 * @return the type
	 */
	public String getType() {
		return getPropertyValue("Type");
	}

	/**
	 * Sets the type of this PropertyObject.
	 * 
	 * @param type the type
	 */
	public void setType(String type) {
		putProperty(Property.createProperty(PropertyType.TEXT, "Type", type));
	}

	/**
	 * Gets the status of this PropertyObject.
	 * 
	 * @return the status
	 */
	public AgentStatus getStatus() {
		String status = getPropertyValue("Status");
		if (status.isEmpty()) {
			return AgentStatus.UNKNOWN;
		} else {
			return AgentStatus.valueOf(status);
		}
	}

	/**
	 * Sets the status of this PropertyObject.
	 * 
	 * @param status the status to be set
	 */
	public void setStatus(AgentStatus status) {
		putProperty(Property.createProperty(PropertyType.STATUS, "Status", status.toString()));
	}

	/**
	 * Gets the description of this PropertyObject.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return getPropertyValue("Description");
	}

	/**
	 * Sets the description of this PropertyObject.
	 * 
	 * @param description the description to be set
	 */
	public void setDescription(String description) {
		putProperty(Property.createProperty(PropertyType.TEXT, "Description", description));
	}

	/**
	 * Gets the location of this PropertyObject.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return getPropertyValue("Location");
	}

	/**
	 * Set the location of this PropertyObject.
	 * 
	 * @param location the location to be set
	 */
	public void setLocation(String location) {
		putProperty(Property.createProperty(PropertyType.LOCATION, "Location", location));
	}

	/**
	 * Set the location of this PropertyObject using latitude and longitude.
	 * 
	 * @param latitude the latitude of the location
	 * @param longitude the longitude of the location
	 */
	public void setLocation(double latitude, double longitude) {
		LocationProperty lp = new LocationProperty("Location");
		lp.setLatitude(latitude);
		lp.setLongitude(longitude);
		lp.setHidden(true);
		lp.setLocationType(GoogleLocationType.ROOFTOP);
		putProperty(lp);
	}

	/**
	 * True if this property should be hidden in the view.
	 * 
	 * @return true or false
	 */
	public boolean isHidden() {
		return getPropertyValue("Hidden").equals(Boolean.toString(true));
	}

	/**
	 * Sets if this PropertyObject should be hidden in the view.
	 * 
	 * @param hidden true or false
	 */
	public void setHidden(boolean hidden) {
		putProperty(Property.createProperty(PropertyType.BOOLEAN, "Hidden", Boolean.toString(hidden)));
	}

	/**
	 * Gets the properties of this PropertyObject.
	 * 
	 * @return properties the properties
	 */
	public HashMap<String, Property> getProperties() {
		return new HashMap<String, Property>(properties);
	}

	/**
	 * Adds a Property to this PropertyObject.
	 * 
	 * @param property the property to be added
	 */
	protected void addProperty(Property property) {
		properties.put(property.getName(), property);
	}

	/**
	 * Gets a property contained in this PropertyObject. 
	 * 
	 * @param name the name of the property
	 * @return the property
	 */
	public Property getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Gets the type of the property contained in this PropertyObject.
	 * 
	 * @param name the name of the property
	 * @return the type
	 */
	public PropertyType getPropertyType(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.getPropertyType();
		}
		return PropertyType.UNKNOWN;
	}

	/**
	 * Gets the property value of a property contained in this PropertyObject.
	 * 
	 * @param name the name of the property
	 * @return the value
	 */
	public String getPropertyValue(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.toString();
		}
		return "";
	}
	
	/**
	 * Get the humanly readable representation of a certain property of this PropertyObject.
	 * 
	 * @param name the name of the property
	 * @return the informative string
	 */
	public String getPropertyInformativeString(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.toInformativeString();
		}
		return "";
	}

	/**
	 * Sets the value of a certain property in this PropertyObject.
	 * 
	 * @param name the name of the property
	 * @param value the value
	 * @return success
	 */
	public boolean setPropertyValue(String name, String value) {
		Property p = getProperty(name);
		if (p != null) {
			if (value.length() > 0 && !p.toString().equals(value)) {
				try {
					p.parseString(value);
				} catch (Exception e) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Replaces a property or adds it to this PropertyObject.
	 * 
	 * @param newP the new property
	 */
	public void putProperty(Property newP) {
		Property oldP = getProperty(newP.getName());
		
		properties.put(newP.getName(), newP);
		if (oldP != null){
			newP.recordHistory(oldP.getHistory());
		}
		
//		if (p == null) {
//			// add property
//			// newP.setAgentCollectionView(acv);
//			// newP.setAgentView(this);
//			this.addProperty(newP);
//		} else {
//			// update property
//			p.parseString(newP.toString());
//			p.setHidden(newP.isHidden());
//			if (newP.recordingHistory()) {
//				p.recordHistory();
//			}
//		}
	}

//	/**
//	 * 
//	 * @param type
//	 * @param name
//	 * @param value
//	 * @return
//	 */
	// public boolean putProperty(PropertyType type, String name, String value)
	// {
	// return putProperty(type, name, value, false);
	// }
	//	
	// public boolean putProperty(PropertyType type, String name, String value,
	// boolean recordHistory) {
	// Property p = getProperty(name);
	// if (p == null) {
	// // add property
	// p = Property.createProperty(type, name, value, this,
	// propertyObjectCollectionView, recordHistory);
	// if (p == null) {
	// return false;
	// } else {
	// this.addProperty(p);
	// return true;
	// }
	// } else {
	// if (recordHistory) {
	// p.recordHistory();
	// }
	// // update property
	// return setPropertyValue(name, value);
	// }
	// }
	/**
	 * Removes a property from this PropertyObject.
	 * 
	 * @param name the name of the property
	 */
	public void removeProperty(String name) {
		properties.remove(name);
	}

	/**
	 * Add an identifier to a dependencies property of this PropertyObject.
	 * 
	 * @param name the name dependencies property
	 * @param id the id to be added
	 * @return success
	 */
	public boolean addIDToDependenciesProperty(String name, String id) {
		Property p = getProperty(name);
		if (p == null) {
			// add property
			p = Property.createProperty(PropertyType.DEPENDENCIES, name, "");
			if (p == null) {
				return false;
			} else {
				this.addProperty(p);
			}
		}

		if (p.getPropertyType() == PropertyType.DEPENDENCIES) {
			DependenciesProperty lp = (DependenciesProperty) p;
			lp.addID(id);
			return true;
		}
		return false;
	}

	/**
	 * Removes an identifier from a dependencies property of this PropertyObject.
	 * 
	 * @param name the name dependencies property
	 * @param id the id to be removed
	 * @return success
	 */
	public boolean removeIDFromDependenciesProperty(String name, String id) {
		Property p = getProperty(name);
		if (p == null) {
			// add property
			p = Property.createProperty(PropertyType.DEPENDENCIES, name, "");
			if (p == null) {
				return false;
			} else {
				this.addProperty(p);
			}
		} else if (p.getPropertyType() == PropertyType.DEPENDENCIES) {
			DependenciesProperty lp = (DependenciesProperty) p;
			lp.removeID(id);
			return true;
		}
		return false;
	}

	/**
	 * Gets a list of identifiers of a dependencies property of this PropertyObject.
	 * 
	 * @param name the name of the dependencies property
	 * @return the list
	 */
	public Vector<String> getIDsFromDependenciesProperty(String name) {
		Property p = getProperty(name);
		if (p == null) {
			return new Vector<String>();
		}
		if (p.getPropertyType() != PropertyType.DEPENDENCIES) {
			return new Vector<String>();
		}
		DependenciesProperty lp = (DependenciesProperty) p;
		return new Vector<String>(lp.getList());
	}

	/**
	 * Get a set with the keys of the properties of this PropertyObject. 
	 * 
	 * @return the set
	 */
	public Set<String> getPropertiesKeySet() {
		return properties.keySet();
	}

	/**
	 * Get the XML representation of this PropertiesObject.
	 * 
	 * @return the XML
	 */
	public String toXML() {
		String xml = "";

		for (Property p : properties.values()) {
			xml += p.toXML();
		}

		return XMLTool.addRootTag(xml, "PropertyObject");
	}

	/**
	 * Converts the xml representation of a PropertiesObject to a PropertiesObject instance.
	 * 
	 * @param xml the representation
	 * @return the Property
	 */
	public static PropertiesObject fromXML(String xml) {
		xml = XMLTool.removeRootTag(xml);
		KeyDataVector propertiesXML = XMLTool.XMLToProperties(xml);
		PropertiesObject o = new PropertiesObject();
		for (KeyData k : propertiesXML) {
			Property p = Property.fromXML(k.getValue());
			o.addProperty(p);
		}
		return o;
	}

	/**
	 * Get the Arff attributes declaration of the agent.
	 * 
	 * @return the Arff attributes data
	 */
	public String getArffAttributesString() {
		String attributes = "";
		Vector<String> propertiesKeySet = new Vector<String>(properties.keySet());
		Collections.sort(propertiesKeySet);

		for (String propertyKey : propertiesKeySet) {
			Property p = properties.get(propertyKey);
			if (p.arffAttributeDeclaration() != null) {
				if (!attributes.isEmpty()) {
					attributes += "\n";
				}
				attributes += p.arffAttributeDeclaration();
			}
		}
		return attributes;
	}

	/**
	 * Get the Arff instance of the agent.
	 * 
	 * @return the Arff instance data
	 */
	public String getArffInstanceString() {
		String instance = "";
		Vector<String> propertiesKeySet = new Vector<String>(properties.keySet());
		Collections.sort(propertiesKeySet);

		for (String propertyKey : propertiesKeySet) {
			Property p = properties.get(propertyKey);
			if (p.arffAttributeDeclaration() != null) {
				if (!instance.isEmpty()) {
					instance += ",";
				}
				instance += p.arffData();
			}
		}
		return instance;
	}
}