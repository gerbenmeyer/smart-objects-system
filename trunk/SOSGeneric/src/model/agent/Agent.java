package model.agent;

import java.util.Collections;
import java.util.GregorianCalendar;
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
import model.agent.property.properties.LocationProperty;
import model.agent.property.properties.ObjectProperty;
import model.agent.property.properties.TimeProperty;
import model.messageboard.MessageBoardInterface;
import util.enums.AgentStatus;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
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

	// some default property names
	public final static String TYPE = "Type";
	public final static String LABEL = "Label";
	public final static String CLASS = "Class";
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
	
	private MessageBoardInterface mbi;

	/**
	 * Constructs a new Agent object.
	 * 
	 * @param id
	 *            the identifier for the agent
	 */
	public Agent(String id) {
		this.id = id;
		initText(Agent.ID, id);
		initText(Agent.LABEL, get(Agent.ID));
		initText(Agent.CLASS, getClass().getName());
		mbi = new MessageBoardInterface(id);
	}

	public boolean needsDetailsPane() {
		return needsDetailsPane;
	}

	/**
	 * @param needsDetailsPane
	 *            the needsDetailsPane to set
	 */
	public void setNeedsDetailsPane(boolean needsDetailsPane) {
		this.needsDetailsPane = needsDetailsPane;
	}

	public void setReadBuffer(Map<String, Property> properties) {
		readBuffer.putAll(properties);
	}

	public String getID() {
		return id;
	}

	public void recordHistory() {
		if (get(Agent.HISTORY).isEmpty()) {
			set(PropertyType.HISTORY, Agent.HISTORY, "");
		}
	}

	private HistoryProperty getHistory() {
		return (HistoryProperty) getProperty(Agent.HISTORY);
	}

	private void mutateHistory(Property p, String oldValue) {
		if (p.toString().equals(oldValue))
			return;
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
	final public void actAndSave() throws InterruptedException {
		try {
			act();
			save();
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
	protected void act() throws Exception {
	}

	/**
	 * Provides a messageboard interface, which can be used by the agent to send
	 * and receive messages
	 * 
	 * @return the messageboard interface
	 */
	protected MessageBoardInterface messages() {
		return mbi;
	}

	public void teachStatus(HtmlMapContentGenerator content, HashMap<String, String> params) {
		if (!Settings.getProperty(Settings.AGENT_PROBLEM_LEARNING_ENABLED).equals(Boolean.toString(true))) {
			content.add("alert('Training of agents is disabled!');");
			return;
		}
		for (String key : params.keySet()) {
			if (key.equals("learnstatus")) {
				try {
					AgentStatus newStatus = AgentStatus.valueOf(params.get(key));
					Classifier r = ClassifierCollection.getInstance().get(get(Agent.TYPE), getArffAttributesString());
					r.addTrainingInstance(getArffInstanceString(), newStatus);
					content.add("alert('New status learned!');");
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Get the current learned status.
	 * 
	 * @return the status
	 */
	public AgentStatus getLearnedStatus() {
		AgentStatus status = AgentStatus.UNKNOWN;
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true))) {
			try {
				Classifier classifier = ClassifierCollection.getInstance().get(get(Agent.TYPE), getArffAttributesString());
				status = classifier.classifyStatus(getArffInstanceString());
			} catch (Exception e) {
			}
		}
		return status;
	}

	/**
	 * This method is executed just before the agent is removed from its
	 * collection.
	 */
	public void lastWish() {
	}

	/**
	 * True if this agent is garbage and may be disposed.
	 * 
	 * @return a boolean
	 */
	public boolean isGarbage() {
		return false;
	}

	public String getIcon() {
		return get(Agent.TYPE).toLowerCase() + "_icon.png";
	}

	public String getMapMarkerImage() {
		return get(Agent.TYPE).toLowerCase() + ".png";
	}

	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
	}

	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		detailsPane.addAgentHeader(this);
		for (String key : getPropertiesKeySet()) {
			Property p = getProperty(key);
			if (!p.isHidden()) {
				detailsPane.addDataRow(p.getIcon(), key, p.toInformativeString());
			}
		}

	}

	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String, String> params) {
		balloonContent.addAgentHeaderLink(this);
		balloonContent.add(HtmlTool.p(get(Agent.DESCRIPTION)));
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
			HashMap<String, Property> dbProperties = AgentStorage.getInstance().getProperties(getID());
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

	public void putProperties(Map<String, Property> properties) {
		for (Property p : properties.values()) {
			putProperty(p);
		}
	}

	public void putProperty(Property p) {
		boolean oldExist = false;
		String oldValue = "";
		if (AgentStorage.getInstance() != null) {
			oldExist = AgentStorage.getInstance().getProperty(getID(), p.getName()) != null; // exists
																								// in
																								// the
																								// database
			if (oldExist) {
				oldValue = AgentStorage.getInstance().getProperty(getID(), p.getName()).toString();
			}
		}
		String newValue = p.toString();
		if (!oldValue.equals(newValue) || !oldExist) { // if changed or not in
														// the database, add to
														// writebuffer
			writeBuffer.put(p.getName(), p);
			mutateHistory(p, oldValue);
		}
		readBuffer.put(p.getName(), p); // always update the readbuffer
	}

	public void removeProperty(String name) {
		writeBuffer.remove(name);
		readBuffer.remove(name);
		if (AgentStorage.getInstance() != null) {
			AgentStorage.getInstance().removeProperty(getID(), name);
		}
	}

	public void save() {
		if (writeBuffer.isEmpty()) {
			return;
		}
		if (AgentStorage.getInstance() != null) {
			AgentStorage.getInstance().putProperties(getID(), writeBuffer);
			writeBuffer.clear();
		}
		if (AgentCollectionStorage.getInstance() != null) {
			AgentCollectionStorage.getInstance().putAgent(this);
		}
	}

	public void delete() {
		setBool("markedForDeletion", true);
		save();
	}

	public boolean isMarkedForDeletion() {
		return getBool("markedForDeletion");
	}

	public String getArffAttributesString() {
		String attributes = "";
		Vector<String> propertiesKeySet = new Vector<String>(getPropertiesKeySet());
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
		attributes += "@ATTRIBUTE Status {" + AgentStatus.UNKNOWN.toString() + "," + AgentStatus.OK.toString() + "," + AgentStatus.WARNING.toString() + "," + AgentStatus.ERROR.toString() + "}";

		return attributes;
	}

	public String getArffInstanceString() {
		String instance = "";
		Vector<String> propertiesKeySet = new Vector<String>(getPropertiesKeySet());
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

	public LocationProperty getLocation() {
		return new LocationProperty("", get(Agent.LOCATION));
	}

	public void setLocation(LocationProperty location) {
		set(PropertyType.LOCATION, Agent.LOCATION, location.toString());
	}

	public AgentStatus getStatus() {
		String statusString = get(Agent.STATUS);
		AgentStatus status = AgentStatus.UNKNOWN;
		if (!statusString.isEmpty()) {
			status = AgentStatus.valueOf(statusString);
		}
		return status;
	}

	public void setStatus(AgentStatus status) {
		set(PropertyType.STATUS, Agent.STATUS, status.toString());
	}

	public String toXML() {
		String xml = "";
		for (Property p : getProperties().values()) {
			xml += p.toXML();
		}
		return XMLTool.addRootTag(xml, "Agent");
	}

	// Generic init, get, and set methods

	public void init(PropertyType pt, String name, String value) {
		if (get(name).isEmpty()) {
			set(pt, name, value);
		}
	}

	public String get(String name) {
		Property p = getProperty(name);
		if (p != null) {
			return p.toString();
		}
		return "";
	}

	public void set(PropertyType pt, String name, String value) {
		Property p = Property.createProperty(pt, name, value);
		if (p != null) {
			putProperty(p);
		}
	}

	// Init, get, add, and remove methods for Dependency property

	public void initDependency(String name) {
		init(PropertyType.DEPENDENCIES, name, new DependenciesProperty(name).toString());
	}

	public Vector<String> getDependencyIDs(String name) {
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

	public boolean addDependencyID(String name, String id) {
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

	public boolean removeDependencyID(String name, String id) {
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

	// Init, get, and set methods for Integer property

	public void initInt(String name, int value) {
		init(PropertyType.INTEGER, name, Integer.toString(value));
	}

	public int getInt(String name) {
		String value = get(name);
		if (value.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	public void setInt(String name, int value) {
		set(PropertyType.INTEGER, name, Integer.toString(value));
	}

	// Init, get, and set methods for Number property

	public void initNumber(String name, double value) {
		init(PropertyType.NUMBER, name, Double.toString(value));
	}

	public double getNumber(String name) {
		String value = get(name);
		if (value.isEmpty()) {
			return 0.0;
		}
		return Double.parseDouble(value);
	}

	public void setNumber(String name, double value) {
		set(PropertyType.NUMBER, name, Double.toString(value));
	}

	// Init, get, and set methods for Text property

	public void initText(String name, String value) {
		init(PropertyType.TEXT, name, value);
	}

	public String getText(String name) {
		return get(name);
	}

	public void setText(String name, String value) {
		set(PropertyType.TEXT, name, value);
	}

	// Init, get, and set methods for boolean property

	public void initBool(String name, boolean value) {
		init(PropertyType.BOOLEAN, name, Boolean.toString(value));
	}

	public boolean getBool(String name) {
		String value = get(name);
		if (value.isEmpty()) {
			return false;
		}
		return value.equals(Boolean.toString(true));
	}

	public void setBool(String name, boolean value) {
		set(PropertyType.BOOLEAN, name, Boolean.toString(value));
	}

	// Init, get, add, and remove methods for Time property

	public void initTime(String name, GregorianCalendar value) {
		init(PropertyType.TIME, name, new TimeProperty(name, value).toString());
	}

	public void initTimeNow(String name) {
		init(PropertyType.TIME, name, TimeProperty.nowString());
	}

	public GregorianCalendar getTime(String name) {
		Property p = getProperty(name);
		if (p == null) {
			return new GregorianCalendar();
		}
		if (p.getPropertyType() != PropertyType.TIME) {
			return new GregorianCalendar();
		}
		TimeProperty tp = (TimeProperty) p;
		return new TimeProperty(name, tp.toString()).getDateTime();
	}

	public void setTime(String name, GregorianCalendar value) {
		set(PropertyType.TIME, name, new TimeProperty(name, value).toString());
	}

	public void setTimeNow(String name) {
		set(PropertyType.TIME, name, TimeProperty.nowString());
	}

	// Init, get, and set methods for Object property

	public void initObject(String name, Object value) {
		init(PropertyType.OBJECT, name, ObjectProperty.objectToString(value));
	}

	public Object getObject(String name) {
		String value = get(name);
		if (value.isEmpty()) {
			return null;
		}
		return ObjectProperty.objectFromString(value);
	}

	public void setObject(String name, Object value) {
		set(PropertyType.OBJECT, name, ObjectProperty.objectToString(value));
	}

}