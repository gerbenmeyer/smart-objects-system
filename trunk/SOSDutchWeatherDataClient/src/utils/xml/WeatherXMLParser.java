package utils.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import model.agent.Agent;
import model.agent.agents.EmptyAgent;
import model.agent.property.properties.LocationProperty;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import util.enums.PropertyType;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class WeatherXMLParser {
	
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
			try{
				Node station = weatherStations.item(i);
				String id = station.getAttributes().getNamedItem("id").getTextContent();
				String name = ((Node)xPath.evaluate("stationnaam", station, XPathConstants.NODE)).getTextContent();
				double lat = Double.parseDouble(((Node)xPath.evaluate("lat", station, XPathConstants.NODE)).getTextContent());
				double lon = Double.parseDouble(((Node)xPath.evaluate("lon", station, XPathConstants.NODE)).getTextContent());
				double tempCelcius = Double.parseDouble(((Node)xPath.evaluate("temperatuurGC", station, XPathConstants.NODE)).getTextContent());
				double windSpeedMS = Double.parseDouble(((Node)xPath.evaluate("windsnelheidMS", station, XPathConstants.NODE)).getTextContent());
				String windDirection = ((Node)xPath.evaluate("windrichting", station, XPathConstants.NODE)).getTextContent();
				double rainMMPerHour = Double.parseDouble(((Node)xPath.evaluate("regenMMPU", station, XPathConstants.NODE)).getTextContent());
				String weatherInWords = ((Node)xPath.evaluate("icoonactueel", station, XPathConstants.NODE)).getAttributes().getNamedItem("zin").getTextContent();
				String url = ((Node)xPath.evaluate("url", station, XPathConstants.NODE)).getTextContent();
				
				Agent agent = new EmptyAgent(id);
				agent.set(PropertyType.TEXT, Agent.TYPE, "Station");
				agent.set(PropertyType.TEXT, Agent.LABEL, HTMLEntities.unhtmlentities(name).replaceAll("Meetstation ", ""));
				agent.set(PropertyType.TEXT, Agent.DESCRIPTION, HTMLEntities.unhtmlentities(weatherInWords));
				agent.set(PropertyType.LOCATION, Agent.LOCATION, LocationProperty.getCoordinate(lat, lon));
				agent.set(PropertyType.NUMBER, "TemperatureCelcius", Double.toString(tempCelcius));
				agent.set(PropertyType.NUMBER, "WindSpeedMS", Double.toString(windSpeedMS));
				agent.set(PropertyType.TEXT, "WindDirection", windDirection);
				agent.set(PropertyType.NUMBER, "RainMMPerHour", Double.toString(rainMMPerHour));
				agent.set(PropertyType.TEXT, "URL", HTMLEntities.unhtmlentities(url));
				agents.add(agent);
			} catch(Exception e){
			}
		}
		return agents;
	}
	
}
