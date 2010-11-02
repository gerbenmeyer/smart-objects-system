package model.locations;

import java.util.Collection;

import model.agent.property.properties.LocationProperty;

public interface LocationCollectionViewable {

	/**
	 * Gets the LocationProperty of an address from the collection if it exists,
	 * or look it up (and store it for later use).
	 * 
	 * @param address the address to lookup
	 * @return the location
	 */
	public LocationProperty getLocation(String address);
	
	/**
	 * Retrieves all locations from the storage in a collection.
	 * 
	 * @return the collection with location
	 */
	public Collection<LocationProperty> getLocations();
	
}