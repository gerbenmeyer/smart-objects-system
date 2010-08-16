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
 * A support class with static methods which generate basic XML elements and structures.
 * 
 * @author Gerben G. Meyer
 */
public abstract class XMLTool {

	/**
	 * Converts XML text to a KeyDataVector object.
	 * 
	 * @param xml the XML
	 * @return the collection of KeyData
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
	 * Converts a KeyDataVector to XML.
	 * 
	 * @param keyDataVector the data
	 * @return the XML text
	 */
	public static String PropertiesToXML(KeyDataVector keyDataVector) {
		String xml = "";
		for (KeyData item : keyDataVector) {
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
	 * Adds an element to the root tag of the XML document.
	 * 
	 * @param xml the existing XML
	 * @param element the element to be added.
	 * @return the resulting XML
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
	 * Get the root tag of an XML document.
	 * 
	 * @param xml the xml
	 * @return the root tag
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
	 * Determines if an XML document has a root tag, i.e. there is no more than one element at the top level.
	 * 
	 * @param xml the xml text
	 * @return true or false
	 */
	public static boolean hasRootTag(String xml) {
		if (xml == null) {
			return false;
		}
		int rootStart = xml.indexOf("<");
		int rootEnd = xml.indexOf(">");
		if (rootStart < 0 || rootEnd < 0) {
			return false;
		}
		return true;
	}

	/**
	 * Removes the root tag from an XML document.
	 * 
	 * @param xml the existing XML
	 * @return the resulting XML
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

	/**
	 * Adds a root tag to an XML text.
	 * 
	 * @param xml the XML
	 * @param rootTag the root tag name
	 * @return the resulting XML document
	 */
	public static String addRootTag(String xml, String rootTag) {
		return "<" + rootTag + ">" + xml + "</" + rootTag + ">";
	}

	/**
	 * Used to recursively search a KeyDataVector for the values with the given identifier.
	 * 
	 * @author Gijs B. Roest
	 * @param props the data collection
	 * @param key the identifier of the KeyData
	 * @return the results
	 */
	public static Vector<String> getValuesRecursively(KeyDataVector props, String key) {
		return XMLTool.getValuesRecursively(props, key, false);
	}

	/**
	 * Used to recursively search a KeyDataVector for the values with the given identifier.
	 * The root tag may be excluded from the search by setting the removeValueTag boolean.
	 * 
	 * @author Gijs B. Roest
	 * @param props the data collection
	 * @param key the identifier of the KeyData
	 * @param removeValueTag exclude root tag
	 * @return the results
	 */
	public static Vector<String> getValuesRecursively(KeyDataVector props, String key, boolean removeValueTag) {
		Vector<String> result = new Vector<String>(0, 1);
		for (KeyData property : props) {
			if (property.getKey().equals(key)) {
				result.add(removeValueTag ? XMLTool.removeRootTag(property.getValue()) : property.getValue());
			} else {
				if (XMLTool.XMLToProperties(property.getValue()).size() > 0) {
					result.addAll(getValuesRecursively(XMLTool.XMLToProperties(XMLTool.removeRootTag(property
							.getValue())), key));
				}
			}
		}
		return result;
	}

	/**
	 * Reads an entire XML document from a file. 
	 * 
	 * @param path the path to the file
	 * @return the XML text
	 */
	public static String xmlFromFile(String path) {
		return xmlFromFile(path,Integer.MAX_VALUE);
	}

	/**
	 * Reads an XML document from a file with a maximum number of lines.
	 * 
	 * @param path the path to the file
	 * @param maxLines the maximum number of lines to read
	 * @return the XML text
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
	 * Writes an XML document to a file.
	 * 
	 * @param path the path to the file
	 * @param xml the XML text
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
	 * Add an element to the root of an existing XML file.
	 * 
	 * @param path the path to the XML file
	 * @param element the element to be added
	 * @param rootTag the name of the root tag
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