package model.agent.classification;

import data.classification.ClassifierCollectionStorage;

/**
 * This class represents a collection of classifiers.
 * 
 * @author Gerben G. Meyer
 * 
 */
public class ClassifierCollection {

	private static ClassifierCollection relations = null;

	private ClassifierCollection() { }

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
	public Classifier get(String agentType, String arffAttributes) {
		String key = agentType + "_" + Integer.toString(arffAttributes.hashCode());
		Classifier classifier = ClassifierCollectionStorage.getInstance().get(key);
		if (classifier == null) {
			classifier = new Classifier(agentType, arffAttributes, "");
		}
		return classifier;
	}
	
	/**
	 * Adds or replaces a Classifier in the collection.
	 * 
	 * @param classifier the classifier to add or update
	 */
	public void put(Classifier classifier) {
		ClassifierCollectionStorage.getInstance().put(classifier);
	}
}