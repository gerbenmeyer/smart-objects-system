package main;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import model.agent.Agent;
import util.clientconnection.RemoteAgentCollection;
import util.enums.PropertyType;
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
		Collection<Agent> agents = null;

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
				agents = kmlp.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// if KML parsing was successful, process the result, and send the objects to the server
		if (agents != null) {
			// data post-processing
			for (Agent a : agents) {
				// type
				String type = a.get(Agent.TYPE);
				if (type.equals("Apartments")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Apartment");
				} else if (type.equals("Apartment rooms")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Apartment");
				} else if (type.equals("Hotels")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Hotel");
				} else if (type.equals("Sights")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Attraction");
				} else if (type.equals("Airports")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Airport");
				} else if (type.equals("Transport")) {
					a.set(PropertyType.TEXT,Agent.TYPE,"Trainstation");
				}

				// label
				String label = a.get(Agent.LABEL);
				label = label.replaceAll("\\s*in Budapest", "");
				a.set(PropertyType.TEXT,Agent.LABEL,label);

				// description
				String description = a.get(Agent.DESCRIPTION);
				description = description
						.replaceAll(
								"<img src='http://www.budapestrooms.com/panel/bas_logo.gif' style='width:110px;height:110px' />",
								"");
				description = description.replaceAll("<\\/?div.*?>", "");
				description = description.replaceAll("<a.*?\\/a>", "");
				a.set(PropertyType.TEXT,Agent.DESCRIPTION,description);
			}
			// send objects to server
			remoteAgentCollection.put(agents);
			
			System.out.println(agents.size()+" objects sent to server!");
		}
		
		
	}
}