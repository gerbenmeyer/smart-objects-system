/*
 * Created on 14-mrt-2006
 *
 * Copyright 2005 The Agent Laboratory, Rijksuniversiteit Groningen
 */
package util.xmltool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

/**
 * 
 * @author Gerben Meyer
 * 
 */
public abstract class XMLTool {

	/**
	 * 
	 * @param xml
	 * @return The XML converted to Properties
	 */
	public static KeyDataVector XMLToProperties(String xml) {
		KeyDataVector prop = new KeyDataVector();

		int index = 0;
		while (true) {
			int startPos = xml.indexOf("<", index);
			int endPos = xml.indexOf(">", index);

			// EOF
			if (startPos < 0 || endPos < 0 || (startPos + 1 >= endPos)) {
				break;
			}

			String tag = xml.substring(startPos + 1, endPos);

			// try {
			// tag = URLDecoder.decode(tag,"UTF-8");
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }

			String startTag = "<" + tag + ">";
			String endTag = "</" + tag + ">";

			int startContent = xml.indexOf(startTag, index) + startTag.length();
			int endContent = xml.indexOf(endTag, startContent);

			// EOF
			if (startPos < 0 || endPos < 0 || startContent < 0 || endContent < 0) {
				break;
			}

			String content = xml.substring(startContent, endContent);
			// try {
			// content = URLDecoder.decode(content,"UTF-8");
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }

			prop.add(new KeyData(tag, content));

			index = endContent+endTag.length();
		}

		return prop;
	}

	/**
	 * 
	 * @param prop
	 * @param rootNode
	 * @return prop converted to XML, with rootNode as rootNode
	 */
	public static String PropertiesToXML(KeyDataVector prop) {
		String xml = "";

		for (KeyData item : prop) {

			// try {
			// String tag = URLEncoder.encode(item.getTag(),"UTF-8");
			// String value = URLEncoder.encode(item.getValue(),"UTF-8");
			String tag = item.getTag();
			String value = item.getValue();
			xml += "<" + tag + ">";
			xml += value;
			xml += "</" + tag + ">";
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }

		}

		return xml;
	}

	/**
	 * 
	 * @param xml
	 * @param element
	 * @return
	 */
	public static String addElementToRoot(String xml, String element) {
		boolean hasRootTag = XMLTool.hasRootTag(xml);
		String rootTag = "";

		if (hasRootTag) {
			rootTag = XMLTool.getRootTag(xml);
			xml = XMLTool.removeRootTag(xml);
		}

		xml = xml + element;
		if (hasRootTag) {
			xml = XMLTool.addRootTag(xml, rootTag);
		}
		return xml;
	}

	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static String getRootTag(String xml) {
		if (xml == null) {
			return "";
		}

		int rootStart = xml.indexOf("<");
		int rootEnd = xml.indexOf(">");

		if (rootStart < 0 || rootEnd < 0) {
			return "";
		}

		return xml.substring(rootStart + 1, rootEnd);
	}

	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static boolean hasRootTag(String xml) {
		if (xml == null)
			return false;

		int rootStart = xml.indexOf("<");
		int rootEnd = xml.indexOf(">");

		if (rootStart < 0 || rootEnd < 0) {
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param xml
	 * @return The xml without the roottag
	 */
	public static String removeRootTag(String xml) {
		KeyDataVector properties = XMLToProperties(xml);

		if (properties.size() == 1) {
			return properties.get(0).getValue();
		} else {
			return xml;
		}

		// int rootStart = xml.indexOf("<");
		// int rootEnd = xml.indexOf(">");
		//
		// if (rootStart < 0 || rootEnd < 0) {
		// return xml;
		// }
		//
		// String rootTag = xml.substring(rootStart + 1, rootEnd);
		//
		// String rootStartTag = "<" + rootTag + ">";
		// String rootEndTag = "</" + rootTag + ">";
		//
		// int rootStartContent = xml.indexOf(rootStartTag)
		// + rootStartTag.length();
		// int rootEndContent = xml.lastIndexOf(rootEndTag);
		//
		// if (rootStartContent < 0 || rootEndContent < 0) {
		// return xml;
		// }
		//
		// return xml.substring(rootStartContent, rootEndContent);
	}

	public static String addRootTag(String xml, String rootTag) {
		return "<" + rootTag + ">" + xml + "</" + rootTag + ">";
	}

	/**
	 * Used to recursively search for the values with the given identifier.
	 * 
	 * @author Gijs B. Roest
	 * @param props
	 * @param identifier
	 * @param boolean to remove the tag automatically from the value.
	 * @return
	 */
	public static Vector<String> getValuesRecursively(KeyDataVector props, String identifier) {
		return XMLTool.getValuesRecursively(props, identifier, false);
	}

	public static Vector<String> getValuesRecursively(KeyDataVector props, String identifier, boolean removeValueTag) {
		Vector<String> result = new Vector<String>(0, 1);

		for (KeyData property : props) {
			if (property.getKey().equals(identifier)) {
				result.add(removeValueTag ? XMLTool.removeRootTag(property.getValue()) : property.getValue());

			} else {
				if (XMLTool.XMLToProperties(property.getValue()).size() > 0) {
					result.addAll(getValuesRecursively(XMLTool.XMLToProperties(XMLTool.removeRootTag(property
							.getValue())), identifier));
				}
			}
		}

		return result;
	}

	public static String xmlFromFile(String path) {
		return xmlFromFile(path,Integer.MAX_VALUE);
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String xmlFromFile(String path, int maxLines) {
		String xml = "";
		File f = new File(path);

		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String bla = null;
			int lines = 0;
			while ((bla = in.readLine()) != null && lines < maxLines) {
				if (!xml.isEmpty()){
					xml += "\n";
				}
				xml += bla;
				lines ++;
			}

			in.close();
		} catch (java.io.FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * 
	 * @param path
	 * @param xml
	 */
	public static void xmlToFile(String path, String xml) {
		File f = new File(path);

		try {

			BufferedWriter out = new BufferedWriter(new FileWriter(f, false));

			out.write(xml);

			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param path
	 * @param element
	 * @param rootTag
	 */
	public static void addElementToRootInFile(String path, String element, String rootTag) {
		String xml = XMLTool.xmlFromFile(path);

		if (!XMLTool.hasRootTag(xml)) {
			xml = XMLTool.addRootTag(xml, rootTag);
		}
		xml = XMLTool.addElementToRoot(xml, element);
		XMLTool.xmlToFile(path, xml);
	}
}
