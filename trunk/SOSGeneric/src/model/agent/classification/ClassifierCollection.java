package model.agent.classification;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a collection of classifiers.
 * 
 * @author Gerben G. Meyer
 * 
 */
public class ClassifierCollection {

	private static ClassifierCollection relations = null;

	private Map<String, Classifier> map;

	private ClassifierCollection() {
		map = Collections.synchronizedMap(new HashMap<String, Classifier>());
	}

	/**
	 * Returns the singleton classifier collection instance.
	 * 
	 * @return The instance of the classifier collection
	 */
	public static ClassifierCollection getInstance() {
		if (relations == null) {
			relations = new ClassifierCollection();
		}

		return relations;
	}

	/**
	 * Returns the classifier for the agent of a certain type with given
	 * attributes.
	 * 
	 * @param agentType
	 *            The type of the agent
	 * @param arffAttributes
	 *            ARFF representation of the attributes of the agent
	 * @return the classifier
	 */
	public Classifier getRelation(String agentType, String arffAttributes) {
		String key = agentType + "_" + Integer.toString(arffAttributes.hashCode());

		if (!map.containsKey(key)) {
			map.put(key, new Classifier(agentType, arffAttributes));
		}
		return map.get(key);
	}
}