package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import agents.WeatherAgentFactory;
import agents.WeatherHomeAgent;

public class SOSDutchWeatherServer extends SOSServer {

	public SOSDutchWeatherServer(Properties settings) {
		super(settings, new WeatherAgentFactory(), new HashMap<String, String>());
		
		//add the home agent to the agent collection
		getAgentCollection().put(new WeatherHomeAgent("home"));
		
		runServer();
	}

	public static void main(String[] args) {
		File file = new File("config.ini");
		Properties settings = new Properties();

		// read config file
		try {
			FileInputStream fis = new FileInputStream(file);
			settings.load(fis);
			fis.close();
		} catch (Exception e) {
			
		}
		new SOSDutchWeatherServer(settings);
	}
}