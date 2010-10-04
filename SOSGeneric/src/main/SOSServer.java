package main;

import java.util.HashMap;
import java.util.Properties;

import model.agent.Agent;
import model.agent.collection.AgentCollection;
import model.agent.collection.AgentCollectionMutable;
import model.agent.collection.AgentFactory;
import model.locations.LocationCollection;
import util.clientconnection.HTTPListener;
import util.clientconnection.XMLListener;
import data.agents.AgentCollectionStorage;
import data.agents.AgentCollectionStorageMySQL;
import data.agents.AgentStorage;
import data.agents.AgentStorageMySQL;
import data.index.AgentIndex;
import data.index.AgentIndexMySQL;

/**
 * @author Gerben G. Meyer
 * 
 */
public abstract class SOSServer {

	private AgentCollectionMutable agentCollection;
	private LocationCollection locations;
	private HashMap<String,String> passwords;
	private AgentFactory factory;
	
	/**
	 * The SOS Server. It instantiates the AgentCollection class which runs all
	 * Agents and AgentProcessors.
	 * 
	 * @param settings the Properties to be used for this server
	 * @param factory an AgentFactory for producing agents
	 * @param passwords a HashMap with an username as key and a password as value
	 */
	public SOSServer(Properties settings, AgentFactory factory, HashMap<String,String> passwords) {
		super();
		this.passwords = passwords;
		this.factory = factory;
		new Settings(settings);

		this.locations = new LocationCollection();
		this.locations.readLocationsFromXML();
		AgentCollectionStorage.setInstance(new AgentCollectionStorageMySQL());
		this.agentCollection = new AgentCollection(factory);
		AgentIndex.setInstance(new AgentIndexMySQL());
		AgentStorage.setInstance(new AgentStorageMySQL());

		Agent a = factory.createAgent("index");
		a.initialize();
		agentCollection.put(a);
		Agent b = factory.createAgent("mobile");
		b.initialize();
		agentCollection.put(b);
		Agent c = factory.createAgent("menu");
		c.initialize();
		agentCollection.put(c);
		Agent d = factory.createAgent("search");
		d.initialize();
		agentCollection.put(d);
		Agent e = factory.createAgent("stats");
		e.initialize();		
		agentCollection.put(e);
	}
	/**
	 * Starts the server's listeners 
	 */
	public void runServer() {
		new HTTPListener(agentCollection,passwords);
		new XMLListener(this);
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
	public LocationCollection getLocations() {
		return locations;
	}
	public AgentFactory getFactory() {
		return factory;
	}
}