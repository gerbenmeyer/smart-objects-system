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

	/**
	 * Get a country name based on coordinates
	 * 
	 * @param lp the LocationProperty to use the coordinates from
	 * @return the country in the following format: code;name
	 */
	public String getCountry(LocationProperty lp);

	/**
	 * Get a country name based on coordinates
	 * 
	 * @param latitude the latitude
	 * @param longitude the longitude
	 */
	public String getCountry(double latitude, double longitude);
}