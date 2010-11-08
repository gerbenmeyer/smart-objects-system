package data.classification;

import model.agent.classification.Classifier;

public abstract class ClassifierCollectionStorage {

	/**
	 * The instance.
	 */
	private static ClassifierCollectionStorage instance;

	/**
	 * Get the instance of LocationCollectionStorage for this application.
	 * 
	 * @return the instance
	 */
	public static ClassifierCollectionStorage getInstance() {
		return instance;
	}

	/**
	 * Sets the instance of LocationCollectionStorage for this application.
	 */
	public static void setInstance(ClassifierCollectionStorage classifierCollectionStorage) {
		instance = classifierCollectionStorage;
	}
	
	/**
	 * Get a Classifier from the collection.
	 * 
	 * @param key the key identifying the Classifier
	 * @return the Classifier
	 */
	public abstract Classifier get(String key);
	
	/**
	 * Add or replace a Classifier in the collection
	 * 
	 * @param classifier the Classifier to add or update
	 */
	public abstract void put(Classifier classifier);
}