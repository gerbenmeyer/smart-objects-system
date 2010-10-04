package model.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import main.Settings;
import model.agent.classification.Classifier;
import model.agent.classification.ClassifierCollection;
import model.agent.property.Property;
import model.agent.property.properties.DependenciesProperty;
import model.agent.property.properties.HistoryProperty;
import model.agent.property.properties.LocationProperty;
import util.enums.AgentStatus;
import util.enums.GoogleLocationType;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;
import util.xmltool.XMLTool;
import data.agents.AgentStorage;

/**
 * This abstract class implements the AgentView interface, and must be extended
 * by every agent implementation.
 * 
 * @author Gijs B. Roest
 */
public abstract class Agent implements AgentMutable {

	//some default property names
	public final static String TYPE = "Type";
	public final static String LABEL = "Label";
	public final static String ID = "ID";
	public final static String LOCATION = "Location";
	public final static String DESCRIPTION = "Description";
	public final static String STATUS = "Status";
	public final static String HIDDEN = "Hidden";
	public final static String HISTORY = "History";

	private String id;

	private HashMap<String, Property> buffer = new HashMap<String, Property>();

	/**
	 * Constructs a new Agent object.
	 * 
	 * @param id
	 *            the identifier for the agent
	 */
	public Agent(String id) {
		this.id = id;
	}

	/**
	 * Prepare a freshman agent.
	 */
	public void initialize() {
		if (get(Agent.ID).isEmpty()) { 
			set(PropertyType.TEXT, Agent.ID, this.id);
		}
		if (get(Agent.LABEL).isEmpty()) {
			set(PropertyType.TEXT, Agent.LABEL, this.id);
		}
	}
	
	public void recordHistory(){
		if (get(Agent.HISTORY).isEmpty()) {
			set(PropertyType.HISTORY, Agent.HISTORY, "");
		}
	}
	
	/**
	 * Get the identifier of this agent.
	 * 
	 * @return the identifier
	 */
	public String getID() {
		return id;
	}

	/**
	 * Set the identifier of this agent.
	 * 
	 * @param id the identifier
	 */
	public void setID(String id) {
		this.id = id;
		set(PropertyType.TEXT, Agent.ID, id);
	}
	
	private HistoryProperty getHistory() {
		return (HistoryProperty) getProperty(Agent.HISTORY);
	}
	
	private void mutateHistory(Property p, String oldValue) {
		if (p.toString().equals(oldValue)) return;
		HistoryProperty hp = getHistory();
		if (hp != null) {
			hp.mutate(p);
		}
	}

	/**
	 * Executes the agent. For external use.
	 * 
	 * @throws InterruptedException
	 */
	final public void execute() throws InterruptedException {
		try {
			act();
		} catch (InterruptedException ie) {
			throw ie;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Executes the agent. For internal use only.
	 * 
	 * @throws Exception
	 */
	protected abstract void act() throws Exception;

	/**
	 * Learn the agent a new status.
	 * 
	 * @param status
	 *            the status to be teached
	 */
	public void teachStatus(AgentStatus status) {
		try {
			Classifier r = ClassifierCollection.getInstance().getRelation(
					get(Agent.TYPE), getArffAttributesString());
			r.addInstance(getArffInstanceString(), status);
		} catch (Exception e) {
		}
	}

	/**
	 * Get the current learned status.
	 * 
	 * @return the status
	 */
	public AgentStatus getLearnedStatus() {
		AgentStatus status = AgentStatus.UNKNOWN;
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED)
				.equals(Boolean.toString(true))) {
			try {
				Classifier r = ClassifierCollection.getInstance().getRelation(
						get(Agent.TYPE), getArffAttributesString());
				status = r.getStatus(getArffInstanceString());
			} catch (Exception e) {
			}
		}
		return status;
	}

	/**
	 * This method is executed just before the agent is removed from its
	 * collection.
	 */
	public abstract void lastWish();

	/**
	 * True if this agent is garbage and may be disposed.
	 * 
	 * @return a boolean
	 */
	public abstract boolean isGarbage();

	public String getIcon() {
		return get(Agent.TYPE).toLowerCase() + "_icon.png";
	}

	public String getMapMarkerImage() {
		return get(Agent.TYPE).toLowerCase() + ".png";
	}

	public abstract void generateMapContent(HtmlMapContentGenerator mapContent,
			HashMap<String, String> params);

	public abstract void generateDetailsPaneContent(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params);

	/**
	 * This function generates the code required for training agents as part of
	 * the mapContent. Has to be used in combination with
	 * generateDetailsPaneTrainingCode
	 * 
	 * @param mapContent
	 * @param params
	 */
	public void generateMapContentTrainingCode(
			HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		if (!Settings.getProperty(Settings.AGENT_PROBLEM_LEARNING_ENABLED)
				.equals(Boolean.toString(true))) {
			return;
		}
		for (String key : params.keySet()) {
			if (key.equals("learnstatus")) {
				try {
					AgentStatus newStatus = AgentStatus
							.valueOf(params.get(key));
					teachStatus(newStatus);
					mapContent.addCustomScript("alert('New status learned!');");
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * This function generates the code required for training agents as part of
	 * the detailsPane. Has to be used in combination with
	 * generateMapContentTrainingCode
	 * 
	 * @param detailsPane
	 * @param params
	 */
	public void generateDetailsPaneTrainingCode(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params) {
		if (!Settings.getProperty(Settings.AGENT_PROBLEM_LEARNING_ENABLED)
				.equals(Boolean.toString(true))) {
			return;
		}

		detailsPane.addSubHeader("Training");
		detailsPane.addDataHeader("", "Training", "Status");
		String url = getID() + ".html?learnstatus=";
		String trainingCode = "";
		trainingCode += HtmlTool.createLink(url + AgentStatus.OK.toString(),
				HtmlTool.createImage("ok.png", "ok", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url
				+ AgentStatus.WARNING.toString(), HtmlTool.createImage(
				"warning.png", "warning", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url + AgentStatus.ERROR.toString(),
				HtmlTool.createImage("error.png", "error", 16), "hidden_frame");
		detailsPane.addDataRow("info.png", "Provide status", trainingCode, "");
	}

	public boolean addIDToDependenciesProperty(String name, String id) {
		Property p = getProperty(name);
		if (p == null) {
			p = Property.createProperty(PropertyType.DEPENDENCIES, name, "");
			if (p == null) {
				return false;
			}
		}

		if (p.getPropertyType() == PropertyType.DEPENDENCIES) {
			DependenciesProperty dp = (DependenciesProperty) p;
			dp.addID(id);
			putProperty(dp);
			return true;
		}
		return false;
	}

	public Property getProperty(String name) {
		Property p = buffer.get(name);
		if (p == null && AgentStorage.getInstance() != null) {
			p = AgentStorage.getInstance().getProperty(getID(), name);
		}
		if (p != null){
			p.setAgentView(this);
		}
		return p;
	}

	public HashMap<String, Property> getProperties() {
		HashMap<String, Property> properties = new HashMap<String, Property>();
		properties.putAll(buffer);
		if (AgentStorage.getInstance() != null) {
			HashMap<String, Property> dbProperties = AgentStorage.getInstance()
					.getProperties(getID());
			for (String key : dbProperties.keySet()) {
				if (!properties.containsKey(key)) {
					Property newP = dbProperties.get(key);
					newP.setAgentView(this);
					properties.put(key, newP);
				}
			}
		}
		return properties;
	}

	public Set<String> getPropertiesKeySet() {
		Set<String> set = new HashSet<String>();
		set.addAll(buffer.keySet());
		if (AgentStorage.getInstance() != null) {
			set.addAll(AgentStorage.getInstance().getPropertiesKeySet(getID()));
		}
		return set;
	}

	public void putProperties(HashMap<String, Property> properties) {
		for (Property p : properties.values()) {
			putProperty(p);
		}
	}

	public void putProperty(Property p) {
		p.setAgentView(this);
		String oldValue = get(p.getName());
		buffer.put(p.getName(), p);
		mutateHistory(p, oldValue);
	}

	public boolean removeIDFromDependenciesProperty(String name, String id) {
		Property p = getProperty(name);
		if (p == null) {
			p = Property.createProperty(PropertyType.DEPENDENCIES, name, "");
			if (p == null) {
				return false;
			}
		}
		if (p.getPropertyType() == PropertyType.DEPENDENCIES) {
			DependenciesProperty dp = (DependenciesProperty) p;
			dp.removeID(id);
			putProperty(dp);
			return true;
		}
		return false;
	}

	public void removeProperty(String name) {
		// TODO remove from both, or either one?
		buffer.remove(name);
		if (AgentStorage.getInstance() != null) {
			AgentStorage.getInstance().removeProperty(getID(), name);
		}
	}

	public boolean save() {
		if (AgentStorage.getInstance() != null) {
			AgentStorage.getInstance().putProperties(getID(), buffer);
			buffer.clear();
			return true;
		}
		return false;
	}

	public boolean delete() {
		// lastWish();
		if (AgentStorage.getInstance() != null) {
			return AgentStorage.getInstance().delete(getID());
		}
		return true;
	}

	// public void setDescription(String description) {
	// putProperty(Property.createProperty(PropertyType.TEXT, "Description",
	// description));
	// }
	//
	// public void setHidden(boolean hidden) {
	// putProperty(Property.createProperty(PropertyType.BOOLEAN, "Hidden",
	// Boolean.toString(hidden)));
	// }
	//
	// public void setID(String id) {
	// getID() = id;
	// putProperty(Property.createProperty(PropertyType.TEXT, "ID", id));
	// }
	//
	// public void setLabel(String label) {
	// putProperty(Property.createProperty(PropertyType.TEXT, "Label", label));
	// }
	//
	// public void setLocation(String location) {
	// putProperty(Property.createProperty(PropertyType.LOCATION, "Location",
	// location));
	// }

	public void setLocation(double latitude, double longitude) {
		LocationProperty lp = new LocationProperty("Location");
		lp.setLatitude(latitude);
		lp.setLongitude(longitude);
		lp.setHidden(true);
		lp.setLocationType(GoogleLocationType.ROOFTOP);
		putProperty(lp);
	}

	// public boolean setPropertyValue(String name, String value) {
	// Property p = getProperty(name);
	// if (p != null) {
	// if (value.length() > 0 && !p.toString().equals(value)) {
	// try {
	// p.parseString(value);
	// putProperty(p);
	// } catch (Exception e) {
	// return false;
	// }
	// }
	// return true;
	// }
	// return false;
	// }

	// public void setStatus(AgentStatus status) {
	// putProperty(Property.createProperty(PropertyType.STATUS, "Status",
	// status.toString()));
	// }

	// public void setType(String type) {
	// putProperty(Property.createProperty(PropertyType.TEXT, "Type", type));
	// }

	public String getArffAttributesString() {
		String attributes = "";
		Vector<String> propertiesKeySet = new Vector<String>(
				getPropertiesKeySet());
		Collections.sort(propertiesKeySet);

		for (String propertyKey : propertiesKeySet) {
			Property p = getProperty(propertyKey);
			if (p.getArffAttributeDeclaration() != null) {
				if (!attributes.isEmpty()) {
					attributes += "\n";
				}
				attributes += p.getArffAttributeDeclaration();
			}
		}
		return attributes;
	}

	public String getArffInstanceString() {
		String instance = "";
		Vector<String> propertiesKeySet = new Vector<String>(
				getPropertiesKeySet());
		Collections.sort(propertiesKeySet);

		for (String propertyKey : propertiesKeySet) {
			Property p = getProperty(propertyKey);
			if (p.getArffAttributeDeclaration() != null) {
				if (!instance.isEmpty()) {
					instance += ",";
				}
				instance += p.getArffData();
			}
		}
		return instance;
	}

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

	public String getPropertyInformativeString(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.toInformativeString();
		}
		return "";
	}

	public PropertyType getPropertyType(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.getPropertyType();
		}
		return PropertyType.UNKNOWN;
	}

	public String get(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.toString();
		}
		return "";
	}

	public void set(PropertyType pt, String name, String value) {
		putProperty(Property.createProperty(pt, name, value));
	}

	public AgentStatus getStatus() {
		String statusString = get(Agent.STATUS);
		AgentStatus status = AgentStatus.UNKNOWN;
		if (!statusString.isEmpty()) {
			status = AgentStatus.valueOf(statusString);
		}
		return status;
	}

	// public boolean isHidden() {
	// return get("Hidden").equals(Boolean.toString(true));
	// }

	public String toXML() {
		String xml = "";
		for (Property p : getProperties().values()) {
			xml += p.toXML();
		}
		return XMLTool.addRootTag(xml, "PropertyObject");
	}
}