package model.locations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import main.SOSServer;
import main.Settings;
import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;

import org.w3c.dom.Document;

import util.enums.GoogleLocationType;
import data.locations.LocationCollectionStorage;

/**
 * The LocationCollection holds a collection of LocationProperties. 
 * It is used for caching locations, so they won't have to be geocoded on every occurrence.
 * 
 * @author Gerben G. Meyer
 */
public class LocationCollection implements LocationCollectionMutable {

	private final String BASE_GEOCODER_URL = "http://maps.googleapis.com/maps/api/geocode/xml";
	private final String BASE_GEOCODER_BACKUP_URL = "http://maps.google.com/maps/geo";
	private final String ENCODING = "UTF8";

	private static int geocode_sleep_time = 1000;
	private static int geocode_overload_sleep_time = 600000;

	private long doNotUseV3TillThisTimeInMillis = 0;
	private long doNotUseV2TillThisTimeInMillis = 0;
	
	/**
	 * Constructs a LocationCollection instance.
	 */
	public LocationCollection() {
		super();
	}
	
	public LocationProperty getLocation(String address) {
		String normAddress = Property.normalize(address);
		LocationProperty result = LocationCollectionStorage.getInstance().getLocation(normAddress);
		if (result == null) { // address unknown
			result = locationLookup(normAddress);
			if (result != null) {
				putLocation(result);
			}
		}
		return result;
	}

	public Collection<LocationProperty> getLocations() {
		return LocationCollectionStorage.getInstance().getLocations();
	}

	public synchronized void putLocation(LocationProperty location) {
		LocationCollectionStorage.getInstance().putLocation(location);
	}

	/**
	 * Lookup a location from a geocoder.
	 * If the first geocoder times out, a backup geocoder is used.
	 * 
	 * @param address the address to look up
	 * @return the location
	 */
	private synchronized LocationProperty locationLookup(String address) {
		boolean hasGeo = false;
		String orgAddress = address;
		double latitude = 0.0;
		double longitude = 0.0;
		GoogleLocationType type = GoogleLocationType.APPROXIMATE;

		while (!hasGeo) {
			try {
				while (!hasGeo && System.currentTimeMillis() >= doNotUseV3TillThisTimeInMillis) {
					// GOOGLE GEOCODER V3
					URL url = new URL(BASE_GEOCODER_URL + "?address=" + URLEncoder.encode(address, ENCODING)
							+ "&sensor=false");

					InputStream stream = url.openStream();

					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);

					XPath xpath = XPathFactory.newInstance().newXPath();
					String status = xpath.evaluate("/GeocodeResponse/status", document);

					if (status.equals("OK")) {
						type = GoogleLocationType.valueOf(xpath.evaluate(
								"/GeocodeResponse/result/geometry/location_type", document));
						latitude = Double.parseDouble(xpath.evaluate("/GeocodeResponse/result/geometry/location/lat",
								document));
						longitude = Double.parseDouble(xpath.evaluate("/GeocodeResponse/result/geometry/location/lng",
								document));
						hasGeo = true;
					} else if (status.equals("ZERO_RESULTS")) {
						int i = address.indexOf(',');
						if (i < 0) {
							hasGeo = true;
						} else {
							address = address.substring(i + 1);
						}
					} else {
						doNotUseV3TillThisTimeInMillis = System.currentTimeMillis() + geocode_overload_sleep_time;
						System.err.println("Google V3 overloaded! (" + status + ")");
					}

					stream.close();
					Thread.sleep(geocode_sleep_time);
				}

				while (!hasGeo && System.currentTimeMillis() >= doNotUseV2TillThisTimeInMillis) {
					// GOOGLE GEOCODER V2
					URL url = new URL(BASE_GEOCODER_BACKUP_URL + "?q=" + URLEncoder.encode(address, ENCODING) + "&key="
							+ Settings.getProperty(Settings.GOOGLE_MAPS_V2_API_KEY) + "&output=csv");

					InputStream stream = url.openStream();
					InputStreamReader inputstreamReader = new InputStreamReader(stream);
					BufferedReader reader = new BufferedReader(inputstreamReader);

					String line = reader.readLine();
					String[] split = line.split(",");
					int statusCode = Integer.parseInt(split[0]);
					int accuracy = Integer.parseInt(split[1]);
					latitude = Double.parseDouble(split[2]);
					longitude = Double.parseDouble(split[3]);
					if (statusCode == 200) {
						if (accuracy <= 0) {
							int i = address.indexOf(',');
							if (i < 0) {
								hasGeo = true;
							} else {
								address = address.substring(i + 1);
							}
						} else {
							if (accuracy < 5) {
								type = GoogleLocationType.APPROXIMATE;
							} else if (accuracy < 7) {
								type = GoogleLocationType.GEOMETRIC_CENTER;
							} else if (accuracy < 9) {
								type = GoogleLocationType.RANGE_INTERPOLATED;
							} else if (accuracy <= 10) {
								type = GoogleLocationType.ROOFTOP;
							}
							hasGeo = true;
						}
					} else {
						doNotUseV2TillThisTimeInMillis = System.currentTimeMillis() + geocode_overload_sleep_time;
						System.err.println("Google V2 overloaded! (" + statusCode + ")");
					}
					stream.close();
					inputstreamReader.close();
					reader.close();
					Thread.sleep(geocode_sleep_time);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (latitude != 0.0 && longitude != 0.0) {
			LocationProperty lp = new LocationProperty("");
			lp.setAddress(orgAddress);
			lp.setLocationType(type);
			lp.setLatitude(latitude);
			lp.setLongitude(longitude);
			return lp;
		}
		return null;
	}
	
	public String getCountry(LocationProperty lp) {
		return countryLookup(lp.getLatitude(), lp.getLongitude());
	}
	
	public String getCountry(double latitude, double longitude) {
		return countryLookup(latitude, longitude);
	}
	
	private synchronized String countryLookup(double latitude, double longitude) {
		String country = null;
		if (latitude != 0.0 && longitude != 0.0) {
			InputStream stream = null;
			try {
				URL url = new URL(BASE_GEOCODER_URL + String.format(Locale.US, "?latlng=%f,%f&sensor=false&language=en", latitude, longitude));
				stream = url.openStream();
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
				XPath xpath = XPathFactory.newInstance().newXPath();

				String status = xpath.evaluate("/GeocodeResponse/status", document);
				if (status.equals("OK")) {
					Object result = xpath.evaluate("/GeocodeResponse/result/address_component[type='country'][1]", document, XPathConstants.NODE);
					country = xpath.evaluate("short_name", result)+";"+xpath.evaluate("long_name", result);
				} else {
					throw new RuntimeException("Return status was: "+status);
				}
			} catch (Exception e) {
				SOSServer.getDevLogger().severe("Failed to resolve country for "+latitude+","+longitude+": "+e.getMessage());
				country = null;
			} finally {
				try { stream.close(); } catch (IOException e) { }
			}
		}
		if (country == null || country.isEmpty()){
			return null;
		}
		return country;
	}
}