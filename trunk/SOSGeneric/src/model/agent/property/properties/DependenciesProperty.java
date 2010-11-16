package model.agent.property.properties;

import java.util.Vector;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import model.agent.collection.AgentCollectionViewable;
import model.agent.property.Property;
import util.enums.AgentStatus;
import util.enums.PropertyType;

/**
 * A Property implementation that holds a list of dependencies.
 * Also, it includes several BooleanProperties used for displaying the dependencies on the map. 
 * 
 * @author Gerben G. Meyer
 */
public class DependenciesProperty extends Property {

	private Vector<String> list = new Vector<String>();

	private BooleanProperty drawOnMap = new BooleanProperty("", true);

	private BooleanProperty drawAsRoute = new BooleanProperty("", false);

	private BooleanProperty drawRouteAsPolyLines = new BooleanProperty("", false);

	private BooleanProperty hideFinishedObjectsInRoute = new BooleanProperty("", false);

	private BooleanProperty includeCurrentLocationInRoute = new BooleanProperty("", false);

	private BooleanProperty usableForTraining = new BooleanProperty("", true);

	/**
	 * Constructs a new named DependenciesProperty instance.
	 * 
	 * @param name the name
	 */
	public DependenciesProperty(String name) {
		super(name, PropertyType.DEPENDENCIES);
	}

	/**
	 * Constructs a new named DependenciesProperty instance with a value.
	 * 
	 * @param name the name
	 * @param value the value
	 */
	public DependenciesProperty(String name, String value) {
		this(name);
		parseString(value);
	}

	/**
	 * The returned BooleanProperty is set to true if the dependencies should be drawn on the map.
	 * 
	 * @return drawOnMap 
	 */
	public BooleanProperty getDrawOnMap() {
		return drawOnMap;
	}

	/**
	 * The returned BooleanProperty is set to true if the dependencies should be drawn in a route on the map.
	 * 
	 * @return drawAsRoute
	 */
	public BooleanProperty getDrawAsRoute() {
		return drawAsRoute;
	}

	/**
	 * The returned BooleanProperty is set to true if the dependencies route should be drawn as polygon lines on the map.
	 * 
	 * @return drawRouteAsPolyLines
	 */
	public BooleanProperty getDrawRouteAsPolyLines() {
		return drawRouteAsPolyLines;
	}

	/**
	 * The returned BooleanProperty is set to true if finished dependencies should not be drawn on the map.
	 * 
	 * @return hideFinishedObjects
	 */
	public BooleanProperty getHideFinishedObjectsInRoute() {
		return hideFinishedObjectsInRoute;
	}

	/**
	 * The returned BooleanProperty is set to true if the current location of the Agent which this DependenciesProperty belongs to should be included in the route.
	 * 
	 * @return includeCurrentLocationInRoute
	 */
	public BooleanProperty getIncludeCurrentLocationInRoute() {
		return includeCurrentLocationInRoute;
	}

	/**
	 * The returned BooleanProperty is set to true if the dependencies should be used for training.
	 * 
	 * @return usableForTraining
	 */
	public BooleanProperty getUsableForTraining() {
		return usableForTraining;
	}

	/**
	 * Gets the list with identifiers of the dependencies. 
	 * 
	 * @return the list
	 */
	public Vector<String> getList() {
		return list;
	}

	/**
	 * Adds an identifier to the dependencies.
	 * 
	 * @param id the identifier to be added
	 */
	public void addID(String id) {
		if (!list.contains(id)) {
			list.add(id);
		}
		// getHistory().update(this);
	}

	/**
	 * Removes an identifier from the dependencies.
	 * 
	 * @param id the identifier
	 * @return success
	 */
	public boolean removeID(String id) {
		// getHistory().update(this);
		return list.remove(id);
	}

	@Override
	public String toString() {
		String result = drawOnMap.toString() + ";";
		result += drawAsRoute.toString() + ";";
		result += drawRouteAsPolyLines.toString() + ";";
		result += hideFinishedObjectsInRoute.toString() + ";";
		result += includeCurrentLocationInRoute.toString() + ";";
		result += usableForTraining.toString() + ";";
		for (String id : list) {
			result += id + ";";
		}
		return result;
	}

	@Override
	public String toInformativeString() {
		String result = "";
		AgentCollectionViewable acv = AgentCollection.getInstance();
		for (String id : list) {
			AgentViewable depPov = acv.get(id);
			if (depPov != null) {
				if (!result.isEmpty()) {
					result += ", ";
				}
				if (depPov.get(Agent.LABEL).isEmpty()) {
					result += depPov.getID();
				} else {
					result += depPov.get(Agent.LABEL);
				}
			}
		}
		return result;
	}

	@Override
	public void parseString(String str) {
		list = new Vector<String>();
		String[] values = str.split(";");
		drawOnMap.parseString(values[0]);
		drawAsRoute.parseString(values[1]);
		drawRouteAsPolyLines.parseString(values[2]);
		hideFinishedObjectsInRoute.parseString(values[3]);
		includeCurrentLocationInRoute.parseString(values[4]);
		usableForTraining.parseString(values[5]);

		for (int i = 6; i < values.length; i++) {
			list.add(values[i]);
		}
		// getHistory().update(this);
	}

	public static String parseHint() {
		return BooleanProperty.parseHint() + ";" + BooleanProperty.parseHint() + ";" + BooleanProperty.parseHint()
				+ ";" + BooleanProperty.parseHint() + ";" + BooleanProperty.parseHint() + ";"
				+ BooleanProperty.parseHint() + ";ID1;ID2;ID3;";
	}

	

	@Override
	public String getArffAttributeDeclaration() {
		if (!usableForTraining.getValue()) {
			return null;
		}

		return "@ATTRIBUTE " + getName() + "Status {" + AgentStatus.UNKNOWN.toString() + ","
				+ AgentStatus.OK.toString() + "," + AgentStatus.WARNING.toString() + "," + AgentStatus.ERROR.toString()
				+ "}";
	}

	@Override
	public String getArffData() {
		if (!usableForTraining.getValue()) {
			return null;
		}
		AgentStatus status = AgentStatus.UNKNOWN;
		AgentCollectionViewable acv = AgentCollection.getInstance();
		for (String id : list) {
			AgentViewable pov = acv.get(id);
			if (pov != null) {
				try {
					AgentStatus newValue = pov.getStatus();
					status = AgentStatus.min(status, newValue);
				} catch (Exception e) {
				}
			}
		}
		return status.toString();
	}
}