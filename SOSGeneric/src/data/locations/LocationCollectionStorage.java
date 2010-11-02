package data.locations;

import java.util.Collection;

import model.agent.property.properties.LocationProperty;

public abstract class LocationCollectionStorage {

	/**
	 * The instance.
	 */
	private static LocationCollectionStorage instance;

	/**
	 * Get the instance of LocationCollectionStorage for this application.
	 * 
	 * @return the instance
	 */
	public static LocationCollectionStorage getInstance() {
		return instance;
	}

	/**
	 * Sets the instance of LocationCollectionStorage for this application.
	 */
	public static void setInstance(LocationCollectionStorage locationCollectionStorage) {
		instance = locationCollectionStorage;
	}

	/**
	 * Retrieves a location from the storage, returns null when not found.
	 * 
	 * @param address the address to lookup
	 * @return the location
	 */
	public abstract LocationProperty getLocation(String address);

	/**
	 * Retrieves all locations from the storage in a collection.
	 * 
	 * @return the collection with locations
	 */
	public abstract Collection<LocationProperty> getLocations();

	/**
	 * Adds a location to the collection.
	 * 
	 * @param location the location
	 */
	public abstract void putLocation(LocationProperty location);
}