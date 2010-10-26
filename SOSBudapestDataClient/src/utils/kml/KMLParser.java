package utils.kml;

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

public class KMLParser {
	
	private XPath xPath = null;
	private InputStream inputstream;

	private KMLParser() {
		XPathFactory xFactor = XPathFactory.newInstance();
		xPath = xFactor.newXPath();
		xPath.setNamespaceContext(new KMLNamespaceContext());
	}

	public KMLParser(String filePath) throws FileNotFoundException {
		this();
		inputstream = new FileInputStream(filePath);
	}

	public KMLParser(InputStream inputstream) throws FileNotFoundException {
		this();
		this.inputstream = inputstream;
	}
	
	public Collection<Agent> parse() throws Exception {
		Vector<Agent> agents = new Vector<Agent>();
		
		InputSource inputSource = new InputSource(inputstream);
		NodeList places = (NodeList) xPath.evaluate("/kml:kml/kml:Document/kml:Folder/kml:Placemark", inputSource, XPathConstants.NODESET);

		for (int i = 0; i < places.getLength(); i++) {
			Node place = places.item(i);
			String id = place.getAttributes().getNamedItem("id").getTextContent();
			String folderName = ((Node)xPath.evaluate("kml:name", place.getParentNode(), XPathConstants.NODE)).getTextContent();
			String name = ((Node)xPath.evaluate("kml:name", place, XPathConstants.NODE)).getTextContent();
			String description = ((Node)xPath.evaluate("kml:description", place, XPathConstants.NODE)).getTextContent();
			String[] coords = ((Node)xPath.evaluate("kml:Point/kml:coordinates", place, XPathConstants.NODE)).getTextContent().split(",");
			Agent agent = new EmptyAgent(id);
			agent.set(PropertyType.TEXT,Agent.TYPE,HTMLEntities.unhtmlentities(folderName));
			agent.set(PropertyType.TEXT,Agent.LABEL,HTMLEntities.unhtmlentities(name));
			agent.set(PropertyType.TEXT,Agent.DESCRIPTION,HTMLEntities.unhtmlentities(description));
			agent.set(PropertyType.LOCATION, Agent.LOCATION, LocationProperty.getCoordinate(Double.parseDouble(coords[1]), Double.parseDouble(coords[0])));
			agents.add(agent);
		}
		return agents;
	}
}