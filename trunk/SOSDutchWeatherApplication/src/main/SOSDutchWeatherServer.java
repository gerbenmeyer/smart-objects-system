package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import agents.WeatherAgentFactory;
import agents.WeatherHomeAgent;

public class SOSDutchWeatherServer extends SOSServer {

	public SOSDutchWeatherServer(Properties settings) {
		// call constructor of SOSGeneric
		super(settings, new WeatherAgentFactory(), new HashMap<String, String>());
		
		// add the home agent to the agent collection
		getAgentCollection().put(new WeatherHomeAgent("home"));
		
		// run the server
		runServer();
	}

	public static void main(String[] args) {
		// read settings from the config file
		File file = new File("config.ini");
		Properties settings = new Properties();
		try {
			FileInputStream fis = new FileInputStream(file);
			settings.load(fis);
			fis.close();
		} catch (Exception e) {
		}
		
		// create new SOS server
		new SOSDutchWeatherServer(settings);
	}
}