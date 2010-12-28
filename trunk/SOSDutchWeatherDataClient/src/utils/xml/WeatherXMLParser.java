package utils.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import model.agent.Agent;
import model.agent.agents.EmptyAgent;
import model.agent.property.properties.LocationProperty;
import model.agent.property.properties.TimeProperty;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import util.enums.PropertyType;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class WeatherXMLParser {
	
	private static final String WEATHER_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	private XPath xPath = null;
	private InputStream inputstream;

	private WeatherXMLParser() {
		XPathFactory xFactor = XPathFactory.newInstance();
		xPath = xFactor.newXPath();
	}

	public WeatherXMLParser(String filePath) throws FileNotFoundException {
		this();
		inputstream = new FileInputStream(filePath);
	}

	public WeatherXMLParser(InputStream inputstream) throws FileNotFoundException {
		this();
		this.inputstream = inputstream;
	}
	
	public Collection<Agent> parse() throws Exception {
		Collection<Agent> agents = new Vector<Agent>();
		
		InputSource inputSource = new InputSource(inputstream);
		NodeList weatherStations = (NodeList) xPath.evaluate("/buienradarnl/weergegevens/actueel_weer/weerstations/weerstation", inputSource, XPathConstants.NODESET);

		for (int i = 0; i < weatherStations.getLength(); i++) {
			Node station = weatherStations.item(i);
			String id = station.getAttributes().getNamedItem("id").getTextContent();
			Node stationName = (Node)xPath.evaluate("stationnaam", station, XPathConstants.NODE);
			String name = stationName.getTextContent();
			String region = stationName.getAttributes().getNamedItem("regio").getTextContent();
			double lat = Double.parseDouble(((Node)xPath.evaluate("lat", station, XPathConstants.NODE)).getTextContent());
			double lon = Double.parseDouble(((Node)xPath.evaluate("lon", station, XPathConstants.NODE)).getTextContent());
			String dateString = ((Node)xPath.evaluate("datum", station, XPathConstants.NODE)).getTextContent();
			Date date = new SimpleDateFormat(WEATHER_TIME_FORMAT, Locale.US).parse(dateString);
			String humidity = ((Node)xPath.evaluate("luchtvochtigheid", station, XPathConstants.NODE)).getTextContent();
			String tempCelcius = ((Node)xPath.evaluate("temperatuurGC", station, XPathConstants.NODE)).getTextContent();
			String windSpeedMS = ((Node)xPath.evaluate("windsnelheidMS", station, XPathConstants.NODE)).getTextContent();
			String windSpeedBF = ((Node)xPath.evaluate("windsnelheidBF", station, XPathConstants.NODE)).getTextContent();
			String windDirectionDegees = ((Node)xPath.evaluate("windrichtingGR", station, XPathConstants.NODE)).getTextContent();
			String windDirection = ((Node)xPath.evaluate("windrichting", station, XPathConstants.NODE)).getTextContent();
			String pressure = ((Node)xPath.evaluate("luchtdruk", station, XPathConstants.NODE)).getTextContent();
			String sight = ((Node)xPath.evaluate("zichtmeters", station, XPathConstants.NODE)).getTextContent();
			String gustsMS = ((Node)xPath.evaluate("windstotenMS", station, XPathConstants.NODE)).getTextContent();
			String rainMMPerHour = ((Node)xPath.evaluate("regenMMPU", station, XPathConstants.NODE)).getTextContent();
			String weatherInWords = ((Node)xPath.evaluate("icoonactueel", station, XPathConstants.NODE)).getAttributes().getNamedItem("zin").getTextContent();
			String temp10cm = ((Node)xPath.evaluate("temperatuur10cm", station, XPathConstants.NODE)).getTextContent();
			String url = ((Node)xPath.evaluate("url", station, XPathConstants.NODE)).getTextContent();
			
			Agent agent = new EmptyAgent(id);
			agent.set(PropertyType.TEXT, Agent.TYPE, "Station");
			agent.set(PropertyType.TEXT, Agent.LABEL, HTMLEntities.unhtmlentities(name).replaceAll("Meetstation ", ""));
			agent.set(PropertyType.TEXT, Agent.DESCRIPTION, HTMLEntities.unhtmlentities(weatherInWords));
			agent.set(PropertyType.TEXT, "Region", HTMLEntities.unhtmlentities(region));
			TimeProperty timeP = new TimeProperty("Date");
			timeP.getDateTime().setTime(date);
			agent.putProperty(timeP);
			agent.set(PropertyType.LOCATION, Agent.LOCATION, LocationProperty.getCoordinate(lat, lon));
			agent.set(PropertyType.NUMBER, "Humidity", humidity);
			agent.set(PropertyType.NUMBER, "TemperatureCelcius", tempCelcius);
			agent.set(PropertyType.NUMBER, "WindSpeedMS", windSpeedMS);
			agent.set(PropertyType.NUMBER, "WindSpeedBF", windSpeedBF);
			agent.set(PropertyType.NUMBER, "WindDirectionDegrees", windDirectionDegees);
			agent.set(PropertyType.TEXT, "WindDirection", windDirection);
			agent.set(PropertyType.NUMBER, "Pressure", pressure);
			agent.set(PropertyType.NUMBER, "Sight", sight);
			agent.set(PropertyType.NUMBER, "GustsMS", gustsMS);
			agent.set(PropertyType.NUMBER, "RainMMPerHour", rainMMPerHour);
			agent.set(PropertyType.NUMBER, "Temperature10CM", temp10cm);
			agent.set(PropertyType.TEXT, "URL", HTMLEntities.unhtmlentities(url));
			agents.add(agent);
			
		}
		return agents;
	}
}
