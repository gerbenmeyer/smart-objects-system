package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import model.agent.agents.IndexAgent;
import model.agent.agents.MenuAgent;
import model.agent.agents.NotifyAgent;
import model.agent.agents.SearchAgent;
import model.agent.agents.StatsAgent;
import model.agent.collection.AgentCollection;
import model.agent.collection.AgentCollectionMutable;
import model.agent.execution.AgentsProcessor;
import model.locations.LocationCollection;
import model.locations.LocationCollectionViewable;
import util.clientconnection.HTTPListener;
import util.clientconnection.XMLListener;
import data.agents.AgentCollectionStorage;
import data.agents.AgentCollectionStorageMySQL;
import data.agents.AgentStorage;
import data.agents.AgentStorageMySQL;
import data.classification.ClassifierCollectionStorage;
import data.classification.ClassifierCollectionStorageMySQL;
import data.locations.LocationCollectionStorage;
import data.locations.LocationCollectionStorageMySQL;

/**
 * The main object to start the server.
 * This class should be extended by every application.
 * 
 * @author Gerben G. Meyer
 */
public abstract class SOSServer {

	private static Logger devLogger = null;
	private AgentCollectionMutable agentCollection;
	private LocationCollection locations;
	private HashMap<String,String> passwords;
	
	/**
	 * The SOS Server. It instantiates the AgentCollection class which runs all
	 * Agents and AgentProcessors.
	 * 
	 * @param settings the Properties to be used for this server
	 * @param passwords a HashMap with an username as key and a password as value
	 */
	public SOSServer(Properties settings, HashMap<String,String> passwords) {
		super();
		this.passwords = passwords;
		
		new Settings(settings);

		//instantiating storage
		LocationCollectionStorage.setInstance(new LocationCollectionStorageMySQL());
		this.locations = new LocationCollection();
		AgentCollectionStorage.setInstance(new AgentCollectionStorageMySQL());
		this.agentCollection = new AgentCollection();
		AgentStorage.setInstance(new AgentStorageMySQL());
		ClassifierCollectionStorage.setInstance(new ClassifierCollectionStorageMySQL());
		
		getDevLogger().fine("Server initialized");
	}
	
	/**
	 * Starts the server's listeners 
	 */
	public void runServer() {
		getDevLogger().fine("Starting server");
		
		//starting agentsprocessor
		new AgentsProcessor();
		
		//adding default agents
		agentCollection.put(new IndexAgent("index"));
		agentCollection.put(new MenuAgent("menu"));
		agentCollection.put(new SearchAgent("search"));
		agentCollection.put(new StatsAgent("stats"));
		if (Boolean.parseBoolean(Settings.getProperty(Settings.NOTIFICATION_EMAIL_ENABLED))) {
			agentCollection.put(new NotifyAgent("notifier"));
		}
		
		//start listening
		new HTTPListener(agentCollection,passwords);
		new XMLListener(this);
		
		getDevLogger().fine("Server started");
	}
	
	/**
	 * Returns the AgentCollection currently present in the server.
	 * 
	 * @return the AgentCollection
	 */
	public AgentCollectionMutable getAgentCollection() {
		return agentCollection;
	}
	
	/**
	 * Returns the LocationCollection currently present in the server.
	 * @return the LocationCollection
	 */
	public LocationCollectionViewable getLocations() {
		return locations;
	}
	
	public static void main(String[] args) {
		System.err.println("This project is usable as base only.");
	}
	
	public static Logger getDevLogger() {
		if (devLogger == null) {
			devLogger = Logger.getLogger("SOSServerDevLogger");
			devLogger.setLevel(Level.ALL);
			try {
				Handler h = new FileHandler("%t/sos_server_dev.log");
				h.setFormatter(new SimpleFormatter());
				devLogger.addHandler(h);
				devLogger.setUseParentHandlers(false);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return devLogger;
	}
}