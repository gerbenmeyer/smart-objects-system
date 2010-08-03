package main;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import model.agent.property.PropertiesObject;
import util.clientconnection.RemoteAgentCollection;
import utils.kml.KMLParser;
import utils.settings.BudapestClientSettings;

public class SOSBudapestDataClient {

	private static RemoteAgentCollection remoteAgentCollection = new RemoteAgentCollection(
			BudapestClientSettings.SERVER_ADDRESS, BudapestClientSettings.SERVER_PORT, BudapestClientSettings.USERNAME,
			BudapestClientSettings.PASSWORD);

	private static final String KML_URL = "http://hotels.budapestrooms.com/maps/kml_dir/budapest_map.kml";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// a KML parser
		KMLParser kmlp = null;
		// a collection which can hold all the PropertiesObjects
		Collection<PropertiesObject> pos = null;

		// create new KML file parser, via http
		try {
			URL url = new URL(KML_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			kmlp = new KMLParser(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// create new KML file parser, via file, if via http was not successful
		if (kmlp == null) {
			try {
				kmlp = new KMLParser("kml/budapest_map.kml");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		// if KML file parses is created successfully, parse the KML
		if (kmlp != null) {
			try {
				pos = kmlp.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// if KML parsing was successful, process the result, and send the objects to the server
		if (pos != null) {
			// data post-processing
			for (PropertiesObject po : pos) {
				// type
				String type = po.getType();
				if (type.equals("Apartments")) {
					po.setType("Apartment");
				} else if (type.equals("Apartment rooms")) {
					po.setType("Apartment");
				} else if (type.equals("Hotels")) {
					po.setType("Hotel");
				} else if (type.equals("Sights")) {
					po.setType("Attraction");
				} else if (type.equals("Airports")) {
					po.setType("Airport");
				} else if (type.equals("Transport")) {
					po.setType("Trainstation");
				}

				// label
				String label = po.getLabel();
				label = label.replaceAll("\\s*in Budapest", "");
				po.setLabel(label);

				// description
				String description = po.getDescription();
				description = description
						.replaceAll(
								"<img src='http://www.budapestrooms.com/panel/bas_logo.gif' style='width:110px;height:110px' />",
								"");
				description = description.replaceAll("<\\/?div.*?>", "");
				description = description.replaceAll("<a.*?\\/a>", "");
				po.setDescription(description);
			}
			// send objects to server
			remoteAgentCollection.sendObjectsToServer(pos);
			
			System.out.println(pos.size()+" objects sent to server!");
		}
		
		
	}
}