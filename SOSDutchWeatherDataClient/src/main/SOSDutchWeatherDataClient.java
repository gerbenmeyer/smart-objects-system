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

	private static RemoteAgentCollection remoteAgentCollection = new RemoteAgentCollection(
			DutchWeatherClientSettings.SERVER_ADDRESS, DutchWeatherClientSettings.SERVER_PORT,
			DutchWeatherClientSettings.USERNAME, DutchWeatherClientSettings.PASSWORD);

	private static final String XML_URL = "http://xml.buienradar.nl/";

	public SOSDutchWeatherDataClient() throws FileNotFoundException {

		while (true) {
			// the parser
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

			// sending agents to server
			try {
				Collection<Agent> agents = parser.parse();
				remoteAgentCollection.connect();
				remoteAgentCollection.put(agents);
				remoteAgentCollection.disconnect();
				System.out.println(agents.size() + " objects sent to server!");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(15 /*minutes*/ * 60 /*seconds*/ * 1000 /*milliseconds*/);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			new SOSDutchWeatherDataClient();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}