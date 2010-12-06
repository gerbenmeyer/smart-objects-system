package model.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import main.Settings;
import model.agent.classification.Classifier;
import model.agent.classification.ClassifierCollection;
import model.agent.property.Property;
import model.agent.property.properties.DependenciesProperty;
import model.agent.property.properties.HistoryProperty;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;
import util.xmltool.XMLTool;
import data.agents.AgentCollectionStorage;
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

	private HashMap<String, Property> readBuffer = new HashMap<String, Property>();
	private HashMap<String, Property> writeBuffer = new HashMap<String, Property>();

	private String id;
	
	private boolean needsDetailsPane = true;
	
	/**
	 * Constructs a new Agent object.
	 * 
	 * @param id
	 *            the identifier for the agent
	 */
	public Agent(String id) {
		this.id = id;
		if (get(Agent.ID).isEmpty()) {
			set(PropertyType.TEXT, Agent.ID, id);
		}
		if (get(Agent.LABEL).isEmpty()) {
			set(PropertyType.TEXT, Agent.LABEL, get(Agent.ID));
		}
	}
	
	public boolean needsDetailsPane() {
		return needsDetailsPane;
	}

	/**
	 * @param needsDetailsPane the needsDetailsPane to set
	 */
	public void setNeedsDetailsPane(boolean needsDetailsPane) {
		this.needsDetailsPane = needsDetailsPane;
	}

	public void setReadBuffer(Map<String, Property> properties){
		readBuffer.putAll(properties);
	}
	
	public String getID(){
		return id;
	}
	
	public void recordHistory(){
		if (get(Agent.HISTORY).isEmpty()) {
			set(PropertyType.HISTORY, Agent.HISTORY, "");
		}
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
			Classifier r = ClassifierCollection.getInstance().get(
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
				Classifier classifier = ClassifierCollection.getInstance().get(
						get(Agent.TYPE), getArffAttributesString());
				status = classifier.getStatus(getArffInstanceString());
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

	public void generateMapContent(HtmlMapContentGenerator mapContent,
			HashMap<String, String> params){
	}

	public void generateDetailsPaneContent(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params){
	}
	
	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String,String> params) {
		balloonContent.addAgentHeaderLink(this);
		balloonContent.addDeepLinkToAgent(this);
		balloonContent.addParagraph(get(Agent.DESCRIPTION));
	}
	
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
		Property p = readBuffer.get(name);
		if (p == null && AgentStorage.getInstance() != null) {
			p = AgentStorage.getInstance().getProperty(getID(), name);
		}
		return p;
	}

	public HashMap<String, Property> getProperties() {
		HashMap<String, Property> properties = new HashMap<String, Property>();
		properties.putAll(writeBuffer);
		if (AgentStorage.getInstance() != null) {
			HashMap<String, Property> dbProperties = AgentStorage.getInstance()
					.getProperties(getID());
			for (String key : dbProperties.keySet()) {
				if (!properties.containsKey(key)) {
					Property newP = dbProperties.get(key);
					properties.put(key, newP);
				}
			}
		}
		return properties;
	}

	public Set<String> getPropertiesKeySet() {
		Set<String> set = new HashSet<String>();
		set.addAll(writeBuffer.keySet());
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
		boolean oldExist = getProperty(p.getName()) != null;
		String oldValue = get(p.getName());
		String newValue = p.toString();
		if (!oldValue.equals(newValue) || !oldExist){
			writeBuffer.put(p.getName(), p);
			readBuffer.put(p.getName(), p);	
			mutateHistory(p, oldValue);
		}
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
		writeBuffer.remove(name);
		if (AgentStorage.getInstance() != null) {
			AgentStorage.getInstance().removeProperty(getID(), name);
		}
	}

	public boolean save() {
		if (writeBuffer.isEmpty()){
			return true;
		}
		if (AgentCollectionStorage.getInstance() != null && AgentStorage.getInstance() != null) {
			AgentCollectionStorage.getInstance().putAgent(this);
			AgentStorage.getInstance().putProperties(getID(), writeBuffer);
			writeBuffer.clear();
			return true;
		}
		return false;
	}

	public boolean delete() {
		// lastWish();
		if (AgentCollectionStorage.getInstance() != null) {
			return AgentCollectionStorage.getInstance().delete(getID());
		}
		if (AgentStorage.getInstance() != null) {
			return AgentStorage.getInstance().delete(getID());
		}
		return true;
	}

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
		
		if (!attributes.isEmpty()) {
			attributes += "\n";
		}
		attributes+= "@ATTRIBUTE Status {" + AgentStatus.UNKNOWN.toString() + "," + AgentStatus.OK.toString()
		+ "," + AgentStatus.WARNING.toString() + "," + AgentStatus.ERROR.toString() + "}";
		
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
				instance += p.getArffData(this);
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

	public String toXML() {
		String xml = "";
		for (Property p : getProperties().values()) {
			xml += p.toXML();
		}
		return XMLTool.addRootTag(xml, "Agent");
	}
}