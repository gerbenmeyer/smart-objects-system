package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.property.properties.LocationProperty;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;

/**
 * 
 * @author Gerben G. Meyer
 */
public class BudapestObjectAgent extends Agent {

	public BudapestObjectAgent(String id) {
		super(id);
		setNeedsDetailsPane(false);
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
		//pan the map to the location of this object
		LocationProperty lp = new LocationProperty("", get(Agent.LOCATION));
		if (!lp.isNull()) {
			mapContent.panToLocation(lp.getLatitude(), lp.getLongitude());
			mapContent.setZoom(14);
		}
		//show the info window of this object
		mapContent.popupInfoWindow(this.getID());

	}
	
	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String,String> params) {
		balloonContent.addAgentHeaderLink(this);
		balloonContent.addParagraph(get(Agent.DESCRIPTION));
	}
}