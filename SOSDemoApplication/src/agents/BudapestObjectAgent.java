package agents;

import java.util.HashMap;

import model.agent.Agent;
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
		//generate the details pane in the same way as the default agent does that
		detailsPane.addHeader(HtmlTool.createImage(getIcon(), get(Agent.TYPE), 16) + " " + get(Agent.LABEL));
		
		detailsPane.addParagraph(get(Agent.DESCRIPTION));
	}

}