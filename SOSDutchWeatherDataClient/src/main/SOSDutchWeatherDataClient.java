package main;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import model.agent.Agent;
import util.clientconnection.RemoteAgentCollection;
import utils.settings.DutchWeatherClientSettings;
import utils.xml.WeatherXMLParser;

public class SOSDutchWeatherDataClient {

	// The remove agent collection, to which the converted external data is
	// added (i.e. the SOS data store)
	private static RemoteAgentCollection remoteAgentCollection = new RemoteAgentCollection(
			DutchWeatherClientSettings.SERVER_ADDRESS, DutchWeatherClientSettings.SERVER_PORT,
			DutchWeatherClientSettings.USERNAME, DutchWeatherClientSettings.PASSWORD);

	// The external datasource which has to be interpreted
	private static final String XML_URL = "http://xml.buienradar.nl/";

	public SOSDutchWeatherDataClient() throws FileNotFoundException {

		while (true) {
			// The WeatherXMLParser is parsing the external data source, and
			// converts it into agents, as required by the RemoteAgentCollection
			WeatherXMLParser parser = null;

			// create new XML file parser, via http
			try {
				URL url = new URL(XML_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				parser = new WeatherXMLParser(conn.getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (parser == null) {
				throw new FileNotFoundException();
			}

			// if KML parsing was successful, process the result, and send the
			// objects to the server
			try {
				Collection<Agent> agents = parser.parse();
				remoteAgentCollection.connect();
				remoteAgentCollection.put(agents);
				remoteAgentCollection.disconnect();
				System.out.println(agents.size() + " objects sent to server!");
			} catch (Exception e) {
				e.printStackTrace();
			}

			// wait 15 minutes, and do the same thing again
			try {
				Thread.sleep(15 /* minutes */* 60 /* seconds */* 1000 /* milliseconds */);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// Create a new SOS data client
		try {
			new SOSDutchWeatherDataClient();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}