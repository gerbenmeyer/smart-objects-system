package util.clientconnection;

import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * An XMLServerCommand is used to construct XML requests for client-server communication.
 * 
 * @author Gerben G. Meyer
 */
public class XMLServerCommand {

	public final static String GET_AGENT = "getAgent";
	public final static String PUT_AGENT = "putAgent";
	public final static String GET_AGENT_IDS = "getAgentIDS";
	public final static String GET_LOCATION_INFO = "getLocationInfo";
	public final static String PUT_LOCATION_INFO = "putLocationInfo";
	public final static String GET_LOCATION_COLLECTION = "getLocationCollection";
	public final static String ADD_TRAINING_INSTANCE = "addTrainingInstance";
	
	private String name = "";
	private String parameter = "";

	/**
	 * Constructs a new XMLServerCommand instance without a name or parameter.
	 */
	public XMLServerCommand() {
		super();
	}

	/**
	 * Constructs a new XMLServerCommand instance with a name and parameter.
	 * 
	 * @param name the command name
	 * @param parameter the parameter
	 */
	public XMLServerCommand(String name, String parameter) {
		super();
		this.name = name;
		this.parameter = parameter;
	}

	/**
	 * Returns the name of this XMLServerCommand.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this XMLServerCommand.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the parameter of this XMLServerCommand.
	 * 
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Sets the parameter of this XMLServerCommand.
	 * 
	 * @param parameter the parameter
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * Constructs a new XMLServerCommand from an XML input.
	 * 
	 * @param xml the XML input
	 * @return the command
	 */
	public static XMLServerCommand fromXML(String xml) {
		XMLServerCommand result = new XMLServerCommand();
		KeyDataVector prop = XMLTool.XMLToProperties(xml);
		for (KeyData item : prop) {
			if (item.getTag().equals("Name")) {
				result.name = XMLTool.removeRootTag(item.getValue());
			} else if (item.getTag().equals("Parameter")) {
				result.parameter = XMLTool.removeRootTag(item.getValue());
			} else {
				System.err.println("WorldCommand: Unknown data in xml: " + item.getTag());
			}
		}
		return result;
	}

	/**
	 * Converts this XMLServerCommand to XML.
	 * 
	 * @return the XML
	 */
	public String toXML() {
		KeyDataVector properties = new KeyDataVector();
		properties.add(new KeyData("Name", "" + this.name));
		properties.add(new KeyData("Parameter", this.parameter));
		return XMLTool.PropertiesToXML(properties);
	}
}