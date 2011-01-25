package util.htmltool;

import java.util.HashMap;

/**
 * A support class with static methods which generate basic HTML elements and structures.
 * 
 * @author G.G. Meyer
 */
public class HtmlTool {

	/**
	 * Same as {@link #encapsulate(StringBuffer, String, HashMap)}, but requires and returns a String instead of a StringBuffer.
	 */
	private static String encapsulate(String content, String tagname, HashMap<String, String> tagAttributes) {
		return encapsulate(new StringBuffer(content), tagname, tagAttributes).toString();
	}
	
	/**
	 * Encloses the specified content with a tag, and adds the optional attributes to the tag.
	 * 
	 * @param content the content to encapsulate
	 * @param tagname the name of the tag
	 * @param tagAttributes the tag attributes
	 * @return the encapsulated content
	 */
	private static StringBuffer encapsulate(StringBuffer content, String tagname, HashMap<String, String> tagAttributes) {
		StringBuffer result = new StringBuffer(content.length()+1000);
		result.append("<" + tagname);
		if (tagAttributes != null) {
			for (String key : tagAttributes.keySet()) {
				result.append(" ");
				result.append(key);
				result.append("=\"");
				result.append(tagAttributes.get(key));
				result.append("\"");
			}
		}
		result.append(">");
		result.append(content);
		result.append("</");
		result.append(tagname);
		result.append(">\n");
		return result;
	}

	/**
	 * Creates a closed empty tag, as specified in XHTML and adds the optional attributes to the tag. 
	 * 
	 * @param tagname the name of the tag
	 * @param tagAttributes the tag attributes
	 * @return the empty tag
	 */
	private static String tagify(String tagname, HashMap<String, String> tagAttributes) {
		StringBuffer content = new StringBuffer("<" + tagname);
		if (tagAttributes != null) {
			for (String key : tagAttributes.keySet()) {
				content.append(" " + key + "=\"" + tagAttributes.get(key) + "\"");
			}
		}
		content.append(" />");
		return content.toString();
	}

	/**
	 * Creates a table row with several cells.
	 * 
	 * @param values the cell values
	 * @return the table row
	 */
	public static StringBuffer createTableRow(String[] values) {
		StringBuffer strb = new StringBuffer();
		for (String value : values) {
			strb.append(encapsulate(value, "td", null));
		}
		return encapsulate(strb, "tr", null);
	}

	/**
	 * Creates a table with headers and content.
	 * 
	 * @param descriptions to be used in the header
	 * @param rows the HTML with row content
	 * @return the table code
	 */
	public static StringBuffer createTable(String[] descriptions, StringBuffer rows) {
		StringBuffer headers = new StringBuffer();
		for (String desc : descriptions) {
			headers.append(encapsulate(desc, "th", null));
		}
		HashMap<String, String> tableAttributes = new HashMap<String, String>();
//		tableAttributes.put("style","width: 100%;");
		return encapsulate(encapsulate(headers, "tr", null).append(rows), "table", tableAttributes);
	}

	/**
	 * Creates a link to an url.
	 * 
	 * @param url the url to link to
	 * @param text the text that will be clickable
	 * @param target a target for the link (like "_blank")
	 * @return the link code
	 */
	public static String createLink(String url, String text, String target) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("href", url);
		attributes.put("target", target);
		return encapsulate((text != null ? text : url), "a", attributes);
	}

	/**
	 * Creates a link to an url which opens on the same page.
	 * 
	 * @param url the url to link to
	 * @param text the text that will be clickable
	 * @return the link code
	 */
	public static String createLink(String url, String text) {
		return createLink(url, text, "_self");
	}
	
	/**
	 * Creates a copyable link
	 * 
	 * @param url
	 * @return
	 */
	public static String createDeeplink(String url){
		String id = ""+System.nanoTime();
		String link = "<a href=\"#\" onClick=\"document.getElementById('"+id+"').style.visibility='visible';document.getElementById('"+id+"').focus();document.getElementById('"+id+"').select();\">"+HtmlTool.createImage("link.png", "Link")+"</a>";
		String value = "<input type=\"text\" id=\""+id+"\" title=\"Copy-paste the link in an e-mail or chat message\" size=\"30\" readonly style=\"visibility:hidden;margin:0px;padding:0px;\" value=\""+url+"\"/>";
		return link+" "+value;
	}

	/**
	 * Creates the HTML code to display an image with specific attributes.
	 * 
	 * @param url the path to the image
	 * @param text the title of the image
	 * @param attributes the attribues of the image
	 * @return the image HTML code
	 */
	public static String createImage(String url, String text, HashMap<String, String> attributes) {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put("src", url);
		attributes.put("title", text);
		return tagify("img", attributes);
	}
	
	/**
	 * Creates the HTML code to display an image.
	 * 
	 * @param url the path to the image
	 * @param text the title of the image
	 * @return the image HTML code
	 */
	public static String createImage(String url, String text) {
		return createImage(url, text, null);
	}

	/**
	 * Creates the HTML code to display an image with a specific size.
	 * 
	 * @param url the path to the image
	 * @param text the title of the image
	 * @param size the size of the image
	 * @return the image HTML code
	 */
	public static String createImage(String url, String text, int size) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		if (size > 0) {
			attributes.put("width", "" + size);
			attributes.put("height", "" + size);
		}
		return createImage(url, text, attributes);
	}
	
	/**
	 * Creates the HTML code to display an image which aligns to the left.
	 * 
	 * @param url the path to the image
	 * @param text the title of the image
	 * @return the image HTML code
	 */
	public static String createImageLeft(String url, String text) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("align", "left");
		return createImage(url, text, attributes);
	}

	/**
	 * Creates the HTML code to display an image which aligns to the right.
	 * 
	 * @param url the path to the image
	 * @param text the title of the image
	 * @return the image HTML code
	 */
	public static String createImageRight(String url, String text) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("align", "right");
		return createImage(url, text, attributes);
	}

	/**
	 * Creates a H1 header.
	 * 
	 * @param text the content of the header
	 * @return the HTML code
	 */
	public static String createHeader1(String text) {
		return encapsulate(text, "h1", null);
	}

	/**
	 * Creates a H2 header.
	 * 
	 * @param text the content of the header
	 * @return the HTML code
	 */
	public static String createHeader2(String text) {
		return encapsulate(text, "h2", null);
	}

	/**
	 * Creates a H3 header.
	 * 
	 * @param text the content of the header
	 * @return the HTML code
	 */
	public static String createHeader3(String text) {
		return encapsulate(text, "h3", null);
	}
	
	/**
	 * Creates a paragraph.
	 * 
	 * @param text the text
	 * @return the paragraph
	 */
	public static String createParagraph(String text) {
		return createParagraph(text, null);
	}

	/**
	 * Creates a paragraph with optional attributes around text.
	 * 
	 * @param text the text
	 * @param attributes the attributes
	 * @return the paragraph
	 */
	public static String createParagraph(String text, HashMap<String, String> attributes) {
		return encapsulate(text, "p", attributes);
	}

	/**
	 * Creates an iframe HTML element.
	 * 
	 * @param id the id of the frame
	 * @param src the source page of the frame
	 * @return the iframe
	 */
	public static String createIFrame(String id, String src){
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		attributes.put("src", src);
		return encapsulate("", "iframe", attributes);
	}
	
	/**
	 * Creates an empty javascript tag with attributes.
	 * 
	 * @param attributes the attributes
	 * @return the script tag
	 */
	public static String createEmptyScript(HashMap<String, String> attributes) {
		return encapsulate("", "script", attributes);
	}

	/**
	 * Creates a javascript tag with content and attributes.
	 * 
	 * @param content the content of the script
	 * @param attributes the attributes
	 * @return the script tag
	 */
	public static StringBuffer createScript(StringBuffer content, HashMap<String, String> attributes) {
		return encapsulate(content, "script", attributes);
	}	

	/**
	 * Create a div HTML element.
	 * 
	 * @param content the content of the div element
	 * @param attributes optional attributes
	 * @return the div element
	 */
	public static String createDiv(String content, HashMap<String, String> attributes) {
		return encapsulate(content, "div", attributes);
	}

	/**
	 * Create a div HTML element with an id.
	 * 
	 * @param content the content of the div element
	 * @param id the id of the element
	 * @return the div element
	 */
	public static String createDiv(String content, String id) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		return encapsulate(content, "div", attributes);
	}

	/**
	 * Creates the head and body of a HTML page.
	 * 
	 * @param title the title of the page
	 * @param css the css of the page
	 * @param bodyContent the body content
	 * @param headContent the head content
	 * @param bodyAttributes the body attributes
	 * @return the HTML code
	 */
	public static StringBuffer createHeadBody(String title, String css, StringBuffer bodyContent, StringBuffer headContent,
			HashMap<String, String> bodyAttributes) {
		StringBuffer headBuffer = new StringBuffer(encapsulate(title, "title", null));
		if (css != null) {
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("href", css);
			attributes.put("rel", "stylesheet");
			attributes.put("type", "text/css");
			headBuffer.append((tagify("link", attributes)));
		}
		if (headContent != null) {
			headBuffer.append(headContent);
		}

		return encapsulate(headBuffer, "head", null).append(encapsulate(bodyContent, "body", bodyAttributes));
	}

	/**
	 * Creates a HTML page from a head and body.
	 * 
	 * @param headbody the head and body HTML code
	 * @return the HTML page
	 */
	public static StringBuffer createHTML(StringBuffer headbody){
		StringBuffer html = new StringBuffer("<!DOCTYPE html>\n");
		html.append(encapsulate(headbody, "html", null));
		return html;
	}
}