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

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		//pan the map to the location of this object
		LocationProperty lp = getLocation();
		if (!lp.isNull()) {
			mapContent.setZoom(14);
			mapContent.panToLocation(lp.getLatitude(), lp.getLongitude());
		}
		//show the info window of this object
		mapContent.popupInfoWindow(this.getID());

	}
	
	@Override
	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String,String> params) {
		//create the content of the balloon of this object, containing a header and a paragraph
		balloonContent.addAgentHeaderLink(this);
		balloonContent.addParagraph(get(Agent.DESCRIPTION));
	}
}