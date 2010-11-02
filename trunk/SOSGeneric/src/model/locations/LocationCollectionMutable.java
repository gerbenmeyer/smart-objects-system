package model.locations;

import model.agent.property.properties.LocationProperty;

public interface LocationCollectionMutable extends LocationCollectionViewable {

	/**
	 * Adds a location to the collection.
	 * 
	 * @param location the location
	 */
	public void putLocation(LocationProperty location);
}