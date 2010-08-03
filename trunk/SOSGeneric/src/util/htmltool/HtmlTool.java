package util.htmltool;

import java.util.HashMap;

public class HtmlTool {

	private static String encapsulate(String content, String tagname, HashMap<String, String> tagAttributes) {
//		StringBuffer result = new StringBuffer(content);
//		result.insert(0, ">");
//		if (tagAttributes != null) {
//			for (String key : tagAttributes.keySet()) {
//				result.insert(0, " " + key + "=\"" + tagAttributes.get(key) + "\"");
//			}
//		}
//		result.insert(0, "<" + tagname);
//		result.append("</" + tagname + ">\n");
//		return result.toString();
		return encapsulate(new StringBuffer(content), tagname, tagAttributes).toString();
	}
	
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

	private static String tagify(String tagname, HashMap<String, String> tagAttributes) {
		StringBuffer content = new StringBuffer("<" + tagname);
		if (tagAttributes != null) {
			for (String key : tagAttributes.keySet()) {
				content.append(" " + key + "=\"" + tagAttributes.get(key) + "\"");
			}
		}
		content.append(" />\n");
		return content.toString();
	}

	public static String createTableRow(String[] values) {
		String strb = "";
		for (String value : values) {
			strb += encapsulate(value, "td", null);
		}
		return encapsulate(strb, "tr", null);
	}

	public static String createTable(String[] descriptions, String rows) {
		String strb = "";
		for (String desc : descriptions) {
			strb += encapsulate(desc, "th", null);
		}
		HashMap<String, String> tableAttributes = new HashMap<String, String>();
		tableAttributes.put("style","width: 100%;");
		
		return encapsulate(encapsulate(strb, "tr", null) + rows, "table", tableAttributes);
	}

	public static String createLink(String url, String text, String target) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("href", url);
		attributes.put("target", target);
		return encapsulate((text != null ? text : url), "a", attributes);

	}

	public static String createLink(String url, String text) {
		return createLink(url, text, "_self");
	}

	public static String createImage(String url, String text) {
		return createImage(url, text, 0);
	}

	public static String createImage(String url, String text, int size) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("src", url);
		attributes.put("title", text);
		if (size > 0) {
			attributes.put("width", "" + size);
			attributes.put("height", "" + size);
		}
		return tagify("img", attributes);
	}
	
	public static String createImageLeft(String url, String text, int size) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("src", url);
		attributes.put("title", text);
		attributes.put("align", "left");
		if (size > 0) {
			attributes.put("width", "" + size);
			attributes.put("height", "" + size);
		}
		return tagify("img", attributes);
	}

	public static String createImageRight(String url, String text, int size) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("src", url);
		attributes.put("title", text);
		attributes.put("align", "right");
		if (size > 0) {
			attributes.put("width", "" + size);
			attributes.put("height", "" + size);
		}
		return tagify("img", attributes);
	}

	public static String createHeader1(String text) {
		return encapsulate(text, "h1", null);
	}

	public static String createHeader2(String text) {
		return encapsulate(text, "h2", null);
	}

	public static String createHeader3(String text) {
		return encapsulate(text, "h3", null);
	}

	public static String createParagraph(String text, HashMap<String, String> attributes) {
		return encapsulate(text, "p", attributes);
	}

	public static String createParagraphRight(String text) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("style", "float:right");
		return encapsulate(text, "p", attributes);
	}

	public static String createIFrame(String id, String src){
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		attributes.put("src", src);
		return encapsulate("", "iframe", attributes);
	}
	
	public static String createScript(String content, HashMap<String, String> attributes) {
		return encapsulate(content, "script", attributes);
	}
	
	public static StringBuffer createScript(StringBuffer content, HashMap<String, String> attributes) {
		return encapsulate(content, "script", attributes);
	}	

	public static String createDiv(String content, HashMap<String, String> attributes) {
		return encapsulate(content, "div", attributes);
	}

	public static String createDiv(String content, String id) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		return encapsulate(content, "div", attributes);
	}

	public static StringBuffer createHeadBody(String title, String css, StringBuffer bodyContent, StringBuffer headContent,
			HashMap<String, String> bodyAttributes) {
		StringBuffer headBuffer = new StringBuffer(encapsulate(title, "title", null));
		if (headContent != null)
			headBuffer.append(headContent);

		if (css != null) {
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put("href", css);
			attributes.put("rel", "stylesheet");
			attributes.put("type", "text/css");
			headBuffer.append((tagify("link", attributes)));
		}

		return encapsulate(headBuffer, "head", null).append(encapsulate(bodyContent, "body", bodyAttributes));
	}

	public static StringBuffer createHTML(StringBuffer headbody){
		StringBuffer html = new StringBuffer("<!DOCTYPE html>\n");
		html.append(encapsulate(headbody, "html", null));
		return html;
	}

}