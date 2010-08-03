package util.clientconnection;

import util.xmltool.KeyData;
import util.xmltool.KeyDataVector;
import util.xmltool.XMLTool;

/**
 * 
 * @author P250799
 * 
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
	 * 
	 */
	public XMLServerCommand() {
		super();
	}

	/**
	 * @param name
	 * @param parameter
	 */
	public XMLServerCommand(String name, String parameter) {
		super();
		this.name = name;
		this.parameter = parameter;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * 
	 * @param parameter
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 * @param XML
	 * @return
	 */
	public static XMLServerCommand fromXML(String xml) {
		XMLServerCommand result = new XMLServerCommand();

		KeyDataVector prop = XMLTool.XMLToProperties(xml);

		for (KeyData item : prop) {

			if (item.getTag().equals("Name")) {
				result = xmlHandleCommand(XMLTool
						.removeRootTag(item.getValue()), result);
			} else if (item.getTag().equals("Parameter")) {
				result = xmlHandleParameter(XMLTool.removeRootTag(item
						.getValue()), result);
			} else {
				System.err.println("WorldCommand: Unknown data in xml: "
						+ item.getTag());
			}
		}

		return result;
	}

	/**
	 * 
	 * @param xml
	 * @param result
	 * @return
	 */
	private static XMLServerCommand xmlHandleCommand(String xml,
			XMLServerCommand result) {
		result.name = xml;
		return result;
	}

	/**
	 * 
	 * @param xml
	 * @param result
	 * @return
	 */
	private static XMLServerCommand xmlHandleParameter(String xml,
			XMLServerCommand result) {
		result.parameter = xml;
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public String toXML() {
		KeyDataVector properties = new KeyDataVector();

		properties.add(new KeyData("Name", "" + this.name));
		properties.add(new KeyData("Parameter", this.parameter));

		return XMLTool.PropertiesToXML(properties);

	}
}
