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
		// set the status to the one which is learned from all training
		// instances
		set(PropertyType.STATUS, Agent.STATUS, getLearnedStatus().toString());
	}

	@Override
	public String getMapMarkerImage() {
		// a custom map marker image, representing the current weather (i.e.
		// cloudy, sunny)
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
		// create custom content for the balloon of this agent type

		// add the icon of the weather status
		balloonContent.addCustomHtml(HtmlTool.createImageRight(getMapMarkerImage(), get(Agent.DESCRIPTION)));

		// add the label of this agent to the balloon
		balloonContent.addAgentHeaderLink(this);

		// add the weather properties of this location
		balloonContent.addParagraph(HtmlTool.createImage("temp_icon.png", "temp") + " Temperature: "
				+ get("TemperatureCelcius") + " °C");
		balloonContent.addParagraph(HtmlTool.createImage("wind_icon.png", "temp") + " Wind: " + get("WindSpeedMS")
				+ " m/s " + get("WindDirection"));
		balloonContent.addParagraph(HtmlTool.createImage("rain_icon.png", "temp") + " Rain: " + get("RainMMPerHour")
				+ " mm/h");
	}

	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		// create custom content for the details pane of this agent type

		// add the icon of the weather status
		detailsPane.addCustomHtml(HtmlTool.createImageRight(getMapMarkerImage(), get(Agent.DESCRIPTION)));

		// add the label of this agent to the details pane
		detailsPane.addAgentHeader(this);

		// add information about the current status of this agent
		Property status = getProperty(Agent.STATUS);
		detailsPane.addSubHeader("Problem detection");
		detailsPane.addDataHeader("", "Name", "Value");
		detailsPane.addDataRow(status.getIcon(), status.getName(), status.toInformativeString());

		// add controls to train agents of this type
		detailsPane.addSubHeader("Training");
		detailsPane.addDataHeader("", "Training", "Status");
		detailsPane.addDataRowTrainingButtons(getID());

		// add the weather properties
		detailsPane.addSubHeader("Properties");
		detailsPane.addDataHeader("", "Name", "Value");
		detailsPane.addDataRow("temp_icon.png", "Temperature", get("TemperatureCelcius") + " °C");
		detailsPane.addDataRow("wind_icon.png", "Wind", get("WindSpeedMS") + " m/s " + get("WindDirection"));
		detailsPane.addDataRow("rain_icon.png", "Rain", get("RainMMPerHour") + " mm/h");

		// add the weather description
		Property description = getProperty(Agent.DESCRIPTION);
		detailsPane.addDataRow(description.getIcon(), description.getName(), description.toInformativeString());

		// add a link to the data source
		detailsPane.addDataRow("", "Source", HtmlTool.createLink(get("URL"), get("URL"), "_blank"));

	}
}