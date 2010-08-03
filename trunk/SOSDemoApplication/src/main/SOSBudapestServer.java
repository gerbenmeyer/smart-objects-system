package main;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import model.agent.Agent;
import agents.BudapestAgentFactory;
import agents.BudapestHomeAgent;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class SOSBudapestServer extends SOSServer {

	/**
	 * 
	 * @param settings
	 */
	public SOSBudapestServer(Properties settings) {
		//call constructor of SOSGeneric
		super(settings, new BudapestAgentFactory(), new HashMap<String, String>());

		//add the home agent to the agent collection
		Agent homeAgent = new BudapestHomeAgent("home", getAgentCollection());
		homeAgent.setHidden(true);
		getAgentCollection().put(homeAgent);

		//run the server
		runServer();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//read settings from the config file
		File file = new File("config.ini");
		Properties settings = new Properties();
		try {
			FileInputStream fis = new FileInputStream(file);
			settings.load(fis);
			fis.close();
		} catch (Exception e) {
		}
		
		//create new SOS server
		new SOSBudapestServer(settings);

	}

}
