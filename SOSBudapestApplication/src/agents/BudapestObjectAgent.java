package agents;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollection;
import model.agent.property.properties.LocationProperty;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

/**
 * 
 * @author Gerben G. Meyer
 */
public class BudapestObjectAgent extends Agent {

	public BudapestObjectAgent(String id) {
		super(id);
	}

	public void act() throws Exception {
		//no acting needed for this object
	}

	public void lastWish() {
		//no last wish needed for this object
	}

	/**
	 * @return the garbage
	 */
	public boolean isGarbage() {
		//this agent is never garbage
		return false;
	}
	
	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		//check whether the used link is a deeplink to this object
		boolean deeplink = params != null && params.containsKey("deeplink");
		if (deeplink) {
			//in case of a deeplink, add all objects to the map as the home-agent does
			AgentCollection.getInstance().get("home").generateMapContent(mapContent, params);
		}
		
		//pan the map to the location of this object
		LocationProperty lp = new LocationProperty("", get(Agent.LOCATION));
		if (!lp.isNull()) {
			mapContent.panToLocation(lp.getLatitude(), lp.getLongitude());
			mapContent.setZoom(14);
		}
		
		//show the info window of this object
		mapContent.popupInfoWindow(this.getID());

	}
	
	@Override
	public void generateDetailsPaneContent(HtmlDetailsPaneContentGenerator detailsPane, HashMap<String, String> params) {
		//show all attracctions by using the always existing search agent to search for all agents
		detailsPane.addHeader("More " + get(Agent.TYPE) + "s");
		AgentViewable av = AgentCollection.getInstance().get("search");
		params.put("q", "type:" + get(Agent.TYPE));
		av.generateDetailsPaneContent(detailsPane, params);
	}

	@Override
	public String createMapBalloonContent() {
		String infoWindowContent = "";
		if (Boolean.parseBoolean(Settings.getProperty(Settings.SHOW_AGENT_DETAILS))) {
			HashMap<String, String> infoWindowContentAttriutes = new HashMap<String, String>();
			infoWindowContentAttriutes.put("class", "infoWindowContent");
			infoWindowContent += HtmlTool.createLink(getID()+".html", HtmlTool.createImage(getIcon(), getID())+get(Agent.LABEL), "hidden_frame")
				+ HtmlTool.createLink("?" + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + getID(), HtmlTool.createImage("link.png", "deeplink to "+getID()))
				+ HtmlTool.createDiv(get(Agent.DESCRIPTION), infoWindowContentAttriutes);
		}
		return infoWindowContent;
	}
}