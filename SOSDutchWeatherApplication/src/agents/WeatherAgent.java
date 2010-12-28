package agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.property.Property;
import model.agent.property.properties.LocationProperty;
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

public class WeatherAgent extends Agent {

	public WeatherAgent(String id) {
		super(id);
	}
	
	@Override
	public String getIcon(){
		return get(Agent.DESCRIPTION).replaceAll("\\s", "_").replaceAll(",", "")+".png";
	}
	
	@Override
	public String getMapMarkerImage(){
		return getIcon();
	}	

	@Override
	public void generateMapContent(HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		//pan the map to the location of this object
		LocationProperty lp = new LocationProperty("", get(Agent.LOCATION));
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
		balloonContent.addParagraph("Temperature: " + get("TemperatureCelcius")+" °C");
		balloonContent.addParagraph("Wind speed: " + get("WindSpeedMS")+" m/s");
		balloonContent.addParagraph("Wind direction: " + get("WindDirection"));
		balloonContent.addParagraph("Rain: " + get("RainMMPerHour")+" mm/h");
	}

	@Override
	public void generateDetailsContent(
			HtmlDetailsContentGenerator detailsPane,
			HashMap<String, String> params) {
		
		detailsPane.addHeader(HtmlTool.createLink(get("URL"), HtmlTool.createImage(get("Icon"), "", 48), "_blank"));
		String time = get("Date");
		if (!time.isEmpty()) {
			detailsPane.addParagraph(HtmlTool.createImage("clock.png", "clock")
					+ " " + time);
		}

		 for (String key : getPropertiesKeySet()) {
			 Property p = getProperty(key);
			 detailsPane.addParagraph(key + " : " +p.toInformativeString());
		 }
	}
}