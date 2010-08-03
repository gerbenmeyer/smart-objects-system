package model.locations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import main.Settings;
import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;

import org.w3c.dom.Document;

import util.enums.GoogleLocationType;
import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class LocationCollection extends HashMap<String, LocationProperty> {

	// singleton //

	/**
	 * 
	 */
	private static final long serialVersionUID = -7444940490537223100L;

	private final String BASE_GEOCODER_URL = "http://maps.google.com/maps/api/geocode/xml";
	private final String BASE_GEOCODER_BACKUP_URL = "http://maps.google.com/maps/geo";
	private final String ENCODING = "UTF8";

	private static int geocode_sleep_time = 1000;
	private static int geocode_overload_sleep_time = 600000;

	private long doNotUseV3TillThisTimeInMillis = 0;
	private long doNotUseV2TillThisTimeInMillis = 0;
	
	/**
	 * 
	 */
	public LocationCollection() {
		super();
	}
	
	public void readLocationsFromXML(){
		String locationDir = Settings.getProperty(Settings.LOCATIONS_DATA_DIR) + "locationdata.xml";
		String xml = XMLTool.xmlFromFile(locationDir);
		handleXMLLocationCollection(xml);
	}

	public void handleXMLLocationCollection(String xml) {
		xml = XMLTool.removeRootTag(xml);
		KeyDataVector prop = XMLTool.XMLToProperties(xml);

		for (KeyData item : prop) {
			this.handleXMLLocationInfo(item.getValue());
		}
	}

	private void handleXMLLocationInfo(String xml) {
		LocationProperty location = (LocationProperty) LocationProperty.fromXML(xml);

		String address = location.getAddress().trim().toLowerCase();

		LocationProperty result = this.get(address);
		if (result == null) { // street not known
			this.put(address, location);
		}
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public LocationProperty getLocationInfo(String address) {
		String normAddress = Property.normalize(address);
		LocationProperty result = this.get(normAddress);
		if (result == null) { // street not known
			result = locationLookup(normAddress);
			if (result != null) {
				putLocationInfo(result);
			}
		}
		return result;
	}

	public synchronized void putLocationInfo(LocationProperty location) {

		// store result
		if (!this.containsKey(location.getAddress())) {
			// don't store address name
			location.setAddressName("");

			// new result, so add it to an XML file
			this.put(location.getAddress(), location);

			// create the xml of the newly learned instance
			String xml = location.toXML();

			// add this xml instance to the data file
			String locationDir = Settings.getProperty(Settings.LOCATIONS_DATA_DIR) + "locationdata.xml";
			XMLTool.addElementToRootInFile(locationDir, xml, "LocationCollection");
		}

	}

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
}