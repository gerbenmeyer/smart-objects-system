package main;


import java.util.HashMap;
import java.util.Properties;

import model.agent.Agent;
import model.agent.agents.MenuAgent;
import model.agent.agents.SearchAgent;
import model.agent.agents.StatsAgent;
import model.agent.agents.index.MobileIndexAgent;
import model.agent.agents.index.NormalIndexAgent;
import model.agent.collection.AgentCollection;
import model.agent.collection.AgentFactory;
import model.agent.property.Property;
import model.locations.LocationCollection;
import util.clientconnection.HTTPListener;
import util.clientconnection.XMLListener;
import util.enums.PropertyType;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public abstract class SOSServer {

	private AgentCollection agentCollection;
	private LocationCollection locations;
	private HashMap<String,String> passwords;
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
		
		new Settings(settings);

		this.locations = new LocationCollection();
		this.locations.readLocationsFromXML();
		this.agentCollection = new AgentCollection(factory);

		Agent indexAgent = new NormalIndexAgent("index", agentCollection);
		indexAgent.putProperty(Property.createProperty(PropertyType.BOOLEAN, "Divine", Boolean.toString(true)));
		indexAgent.setHidden(true);
		agentCollection.put(indexAgent);

		Agent mobileAgent = new MobileIndexAgent("mobile", agentCollection);
		mobileAgent.putProperty(Property.createProperty(PropertyType.BOOLEAN, "Divine", Boolean.toString(true)));
		mobileAgent.setHidden(true);
		agentCollection.put(mobileAgent);

		Agent menuAgent = new MenuAgent("menu", agentCollection);
		menuAgent.putProperty(Property.createProperty(PropertyType.BOOLEAN, "Divine", Boolean.toString(true)));
		menuAgent.setHidden(true);
		agentCollection.put(menuAgent);
		
		Agent searchAgent = new SearchAgent("search", agentCollection);
		searchAgent.putProperty(Property.createProperty(PropertyType.BOOLEAN, "Divine", Boolean.toString(true)));
		searchAgent.setHidden(true);
		agentCollection.put(searchAgent);

		Agent statsAgent = new StatsAgent("stats", agentCollection);
		statsAgent.putProperty(Property.createProperty(PropertyType.BOOLEAN, "Divine", Boolean.toString(true)));
		statsAgent.setHidden(true);
		agentCollection.put(statsAgent);
		
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
	public AgentCollection getAgentCollection() {
		return agentCollection;
	}
	/**
	 * Returns the LocationCollection currently present in the server.
	 * @return the LocationCollection
	 */
	public LocationCollection getLocations() {
		return locations;
	}
}