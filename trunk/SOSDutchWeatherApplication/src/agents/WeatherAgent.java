package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

public class WeatherAgent extends Agent {

	public WeatherAgent(String id) {
		super(id);
	}

	@Override
	public void act() throws Exception {
		set(PropertyType.STATUS, Agent.STATUS, getLearnedStatus().toString());
	}

	@Override
	public String getMapMarkerImage() {
		return get(Agent.DESCRIPTION).replaceAll("\\s", "_").replaceAll(",", "") + ".png";
	}

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		// pan the map to the location of this object
		LocationProperty lp = new LocationProperty("", get(Agent.LOCATION));
		if (!lp.isNull()) {
			mapContent.setZoom(9);
			mapContent.panToLocation(lp.getLatitude(), lp.getLongitude());
		}
		// show the info window of this object
		mapContent.popupInfoWindow(this.getID());
	}

	@Override
	public void generateMapBalloonContent(HtmlMapBalloonContentGenerator balloonContent, HashMap<String, String> params) {
		// create the content of the balloon of this object, containing a header
		// and a paragraph
		balloonContent.addCustomHtml(HtmlTool.createImageRight(getMapMarkerImage(), get(Agent.DESCRIPTION)));
		balloonContent.addAgentHeaderLink(this);
		balloonContent.addParagraph("Temperature: " + get("TemperatureCelcius") + " �C");
		balloonContent.addParagraph("Wind: " + get("WindSpeedMS") + " m/s " + get("WindDirection"));
		balloonContent.addParagraph("Rain: " + get("RainMMPerHour") + " mm/h");
	}

	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		detailsPane.addHeader(HtmlTool.createImage(getMapMarkerImage(), "") + " " + get(Agent.LABEL));
		
		Property status = getProperty(Agent.STATUS);
		detailsPane.addSubHeader("Problem detection");
		detailsPane.addDataHeader("", "Name", "Value");
		detailsPane.addDataRow(status.getIcon(), status.getName(), status.toInformativeString());
		
		detailsPane.addSubHeader("Training");
		detailsPane.addDataHeader("", "Training", "Status");
		detailsPane.addDataRowTrainingButtons(getID());
		
		detailsPane.addSubHeader("Properties");
		detailsPane.addDataHeader("", "Name", "Value");
		
		detailsPane.addDataRow("", "Temperature", get("TemperatureCelcius") + " �C");
		detailsPane.addDataRow("", "Wind", get("WindSpeedMS") + " m/s " + get("WindDirection"));
		detailsPane.addDataRow("", "Rain", get("RainMMPerHour") + " mm/h");
		
		Property description = getProperty(Agent.DESCRIPTION);
		detailsPane.addDataRow(description.getIcon(), description.getName(), description.toInformativeString());
		
		detailsPane.addDataRow("", "Source", HtmlTool.createLink(get("URL"), get("URL"), "_blank"));
		
	}
}