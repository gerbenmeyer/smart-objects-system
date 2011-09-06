package util.htmltool;

import java.util.HashMap;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

/**
 * A support class with static methods which generate basic HTML elements and
 * structures.
 * 
 * @author G.G. Meyer
 */
public class HtmlTool {

	/**
	 * Creates an A link-tag
	 * 
	 * @param content
	 * @param href
	 * @return HTML code of the tag
	 */
	public static String aLink(String content, String href) {
		return aLink(content, href, "");
	}

	/**
	 * 
	 * Creates an A link-tag
	 * 
	 * @param content
	 * @param href
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String aLink(String content, String href, String optionalAttributes) {
		String attributes = "href=\"" + href + "\" " + optionalAttributes;
		content = convertToHtml(content);
		return tagify("a", content, attributes.trim());
	}

	/**
	 * Creates an A bookmark-tag
	 * 
	 * @param content
	 * @param href
	 * @return HTML code of the tag
	 */
	public static String aBookmark(String name) {
		return aBookmark(name, "");
	}

	/**
	 * 
	 * Creates an A bookmark-tag
	 * 
	 * @param content
	 * @param href
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String aBookmark(String name, String optionalAttributes) {
		String attributes = "name=\"" + name + "\" " + optionalAttributes;
		return tagify("a", attributes.trim());
	}

	/**
	 * Creates a br tag
	 * 
	 * @return HTML code of the tag
	 */
	public static String br() {
		return tagify("br", "");
	}

	/**
	 * Creates a div tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String div(String content) {
		return HtmlTool.div(content, "");
	}

	/**
	 * Creates a div tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String div(String content, String optionalAttributes) {
		return tagify("div", content, optionalAttributes.trim());
	}

	/**
	 * Creates a h1 tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String h1(String content) {
		return HtmlTool.h1(content, "");
	}

	/**
	 * Creates a h1 tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String h1(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("h1", content, optionalAttributes.trim());
	}

	/**
	 * Creates a h2 tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String h2(String content) {
		return HtmlTool.h2(content, "");
	}

	/**
	 * Creates a h2 tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String h2(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("h2", content, optionalAttributes.trim());
	}

	/**
	 * Creates a h3 tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String h3(String content) {
		return HtmlTool.h3(content, "");
	}

	/**
	 * Creates a h3 tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String h3(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("h3", content, optionalAttributes.trim());
	}

	/**
	 * Creates a iframe tag
	 * 
	 * @param src
	 * @return HTML code of the tag
	 */
	public static String iframe(String src) {
		return HtmlTool.iframe(src, "");
	}

	/**
	 * Creates a iframe tag
	 * 
	 * @param src
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String iframe(String src, String optionalAttributes) {
		String attributes = "src=\"" + src + "\" " + optionalAttributes;
		return tagify("iframe", HtmlTool.p("Your browser does not support iframes."), attributes.trim());
	}

	/**
	 * Creates a img tag
	 * 
	 * @param src
	 * @param alt
	 * @return HTML code of the tag
	 */
	public static String img(String src, String alt) {
		return HtmlTool.img(src, alt, "");
	}

	/**
	 * Creates a img tag
	 * 
	 * @param src
	 * @param alt
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String img(String src, String alt, String optionalAttributes) {
		String attributes = "src=\"" + src + "\" alt=\"" + alt + "\" " + optionalAttributes;
		return tagify("img", attributes.trim());
	}

	/**
	 * Creates a p tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String p(String content) {
		return HtmlTool.p(content, "");
	}

	/**
	 * Creates a p tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String p(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("p", content, optionalAttributes.trim());
	}

	/**
	 * Creates a table tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String table(String content) {
		return HtmlTool.table(content, "");
	}

	/**
	 * Creates a table tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String table(String content, String optionalAttributes) {
		return tagify("table", content, optionalAttributes.trim());
	}

	public static String tr(String content) {
		return HtmlTool.tr(content, "");
	}

	/**
	 * Creates a tr tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String tr(String content, String optionalAttributes) {
		return tagify("tr", content, optionalAttributes.trim());
	}

	/**
	 * Creates a th tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String th(String content) {
		return HtmlTool.th(content, "");
	}

	/**
	 * Creates a th tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String th(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("th", content, optionalAttributes.trim());
	}

	/**
	 * Creates a td tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String td(String content) {
		return HtmlTool.td(content, "");
	}

	/**
	 * Creates a td tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String td(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("td", content, optionalAttributes.trim());
	}

	/**
	 * Creates a span tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String span(String content) {
		return HtmlTool.span(content, "");
	}

	/**
	 * Creates a span tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String span(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("span", content, optionalAttributes.trim());
	}

	/**
	 * Creates a title tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String title(String content) {
		return HtmlTool.title(content, "");
	}

	/**
	 * Creates a title tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String title(String content, String optionalAttributes) {
		content = convertToHtml(content);
		return tagify("title", content, optionalAttributes.trim());
	}

	/**
	 * Creates a head tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String head(String content) {
		return HtmlTool.head(content, "");
	}

	/**
	 * Creates a head tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String head(String content, String optionalAttributes) {
		return tagify("head", content, optionalAttributes.trim());
	}

	/**
	 * Creates a head tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static StringBuffer head(StringBuffer content) {
		return HtmlTool.head(content, "");
	}

	/**
	 * Creates a head tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static StringBuffer head(StringBuffer content, String optionalAttributes) {
		return tagify("head", content, optionalAttributes.trim());
	}

	/**
	 * Creates a body tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String body(String content) {
		return HtmlTool.body(content, "");
	}

	/**
	 * Creates a body tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String body(String content, String optionalAttributes) {
		return tagify("body", content, optionalAttributes.trim());
	}

	/**
	 * Creates a body tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static StringBuffer body(StringBuffer content) {
		return HtmlTool.body(content, "");
	}

	/**
	 * Creates a body tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static StringBuffer body(StringBuffer content, String optionalAttributes) {
		return tagify("body", content, optionalAttributes.trim());
	}

	/**
	 * Creates a html tag
	 * 
	 * @param head
	 * @param body
	 * @return HTML code of the tag
	 */
	public static String html(String head, String body) {
		return "<!DOCTYPE html>\n" + tagify("html", head + body);
	}

	/**
	 * Creates a html tag
	 * 
	 * @param head
	 * @param body
	 * @return HTML code of the tag
	 */
	public static StringBuffer html(StringBuffer head, StringBuffer body) {
		StringBuffer result = new StringBuffer();
		result.append("<!DOCTYPE html>\n");

		StringBuffer content = new StringBuffer();
		content.append(head);
		content.append(body);

		return result.append(tagify("html", content, ""));
	}

	/**
	 * Creates a script tag
	 * 
	 * @param content
	 * @return HTML code of the tag
	 */
	public static String script(String content) {
		return HtmlTool.script(content, "");
	}

	/**
	 * Creates a script tag
	 * 
	 * @param content
	 * @param optionalAttributes
	 * @return HTML code of the tag
	 */
	public static String script(String content, String optionalAttributes) {
		String attributes = "type=\"text/javascript\" " + optionalAttributes;
		return tagify("script", content, attributes.trim());
	}

	/**
	 * Creates a link tag for linking a css file
	 * 
	 * @param href
	 * @return HTML code of the tag
	 */
	public static String linkCss(String href) {
		String attributes = "rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"";
		return tagify("link", attributes);
	}

	/**
	 * 
	 * @param tagname
	 * @param content
	 * @param attributes
	 * @return
	 */
	private static String tagify(String tagname, String content, String attributes) {
		return "<" + tagname + (attributes.isEmpty() ? "" : " ") + attributes + ">" + content + "</" + tagname + ">";
	}

	/**
	 * 
	 * @param tagname
	 * @param content
	 * @param attributes
	 * @return
	 */
	private static StringBuffer tagify(String tagname, StringBuffer content, String attributes) {
		StringBuffer result = new StringBuffer();
		result.append("<" + tagname + (attributes.isEmpty() ? "" : " ") + attributes + ">");
		result.append(content);
		result.append("</" + tagname + ">");
		return result;
	}

	/**
	 * 
	 * @param tagname
	 * @param attributes
	 * @return
	 */
	private static String tagify(String tagname, String attributes) {
		return "<" + tagname + (attributes.isEmpty() ? "" : " ") + attributes + " />";
	}
	
	/**
	 * Converts text to HTML compatible text, inserting HTML entities where
	 * necessary.
	 * 
	 * @param text
	 *            the input text
	 * @return the HTML compatible output
	 */
	public static String convertToHtml(String text) {
		if (text == null) {
			return null;
		}
		return HTMLEntities.htmlentities(text.replaceAll("\n", " "));
	}

	/*
	 * DEPRECATED FROM HERE
	 */

	/**
	 * Same as {@link #encapsulate(StringBuffer, String, HashMap)}, but requires
	 * and returns a String instead of a StringBuffer.
	 */
	@Deprecated
	private static String encapsulate(String content, String tagname, HashMap<String, String> tagAttributes) {
		return encapsulate(new StringBuffer(content), tagname, tagAttributes).toString();
	}

	/**
	 * Encloses the specified content with a tag, and adds the optional
	 * attributes to the tag.
	 * 
	 * @param content
	 *            the content to encapsulate
	 * @param tagname
	 *            the name of the tag
	 * @param tagAttributes
	 *            the tag attributes
	 * @return the encapsulated content
	 */
	@Deprecated
	private static StringBuffer encapsulate(StringBuffer content, String tagname, HashMap<String, String> tagAttributes) {
		StringBuffer result = new StringBuffer(content.length() + 1000);
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
	 * Creates a closed empty tag, as specified in XHTML and adds the optional
	 * attributes to the tag.
	 * 
	 * @param tagname
	 *            the name of the tag
	 * @param tagAttributes
	 *            the tag attributes
	 * @return the empty tag
	 */
	@Deprecated
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
	 * @param values
	 *            the cell values
	 * @return the table row
	 */
	@Deprecated
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
	 * @param descriptions
	 *            to be used in the header
	 * @param rows
	 *            the HTML with row content
	 * @return the table code
	 */
	@Deprecated
	public static StringBuffer createTable(String[] descriptions, StringBuffer rows) {
		StringBuffer headers = new StringBuffer();
		for (String desc : descriptions) {
			headers.append(encapsulate(desc, "th", null));
		}
		HashMap<String, String> tableAttributes = new HashMap<String, String>();
		// tableAttributes.put("style","width: 100%;");
		return encapsulate(encapsulate(headers, "tr", null).append(rows), "table", tableAttributes);
	}

	/**
	 * Creates a link to an url.
	 * 
	 * @param url
	 *            the url to link to
	 * @param text
	 *            the text that will be clickable
	 * @param target
	 *            a target for the link (like "_blank")
	 * @return the link code
	 */
	@Deprecated
	public static String createLink(String url, String text, String target) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("href", url);
		attributes.put("target", target);
		return createLink(url, text, attributes);
	}

	/**
	 * Creates a link to an url which opens on the same page.
	 * 
	 * @param url
	 *            the url to link to
	 * @param text
	 *            the text that will be clickable
	 * @return the link code
	 */
	@Deprecated
	public static String createLink(String url, String text) {
		return createLink(url, text, "_self");
	}

	/**
	 * Creates a link to an url which opens on the same page.
	 * 
	 * @param url
	 *            the url to link to
	 * @param text
	 *            the text that will be clickable
	 * @param attributes
	 *            additional HTML attributes
	 * @return the link code
	 */
	@Deprecated
	public static String createLink(String url, String text, HashMap<String, String> attributes) {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put("href", url);
		return encapsulate((text != null ? text : url), "a", attributes);
	}

	/**
	 * Creates a copyable link
	 * 
	 * @param url
	 * @return
	 */
	@Deprecated
	public static String createDeeplink(String url) {
		String id = "" + System.nanoTime();
		String link = "<a href=\"#\" onClick=\"document.getElementById('" + id + "').style.visibility='visible';document.getElementById('" + id + "').focus();document.getElementById('" + id + "').select();\">" + HtmlTool.createImage("link.png", "Link") + "</a>";
		String value = "<input type=\"text\" id=\"" + id + "\" title=\"Copy-paste the link in an e-mail or chat message\" size=\"30\" readonly style=\"visibility:hidden;margin:0px;padding:0px;\" value=\"" + url + "\"/>";
		return link + " " + value;
	}

	/**
	 * Creates the HTML code to display an image with specific attributes.
	 * 
	 * @param url
	 *            the path to the image
	 * @param text
	 *            the alt text of the image
	 * @param attributes
	 *            the attribues of the image
	 * @return the image HTML code
	 */
	@Deprecated
	public static String createImage(String url, String text, HashMap<String, String> attributes) {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		attributes.put("src", url);
		attributes.put("alt", text);
		return tagify("img", attributes);
	}

	/**
	 * Creates the HTML code to display an image.
	 * 
	 * @param url
	 *            the path to the image
	 * @param text
	 *            the alt text of the image
	 * @return the image HTML code
	 */
	@Deprecated
	public static String createImage(String url, String text) {
		return createImage(url, text, null);
	}

	/**
	 * Creates the HTML code to display an image with a specific size.
	 * 
	 * @param url
	 *            the path to the image
	 * @param text
	 *            the alt text of the image
	 * @param size
	 *            the size of the image
	 * @return the image HTML code
	 */
	@Deprecated
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
	 * @param url
	 *            the path to the image
	 * @param text
	 *            the title of the image
	 * @return the image HTML code
	 */
	@Deprecated
	public static String createImageLeft(String url, String text) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("align", "left");
		return createImage(url, text, attributes);
	}

	/**
	 * Creates the HTML code to display an image which aligns to the right.
	 * 
	 * @param url
	 *            the path to the image
	 * @param text
	 *            the title of the image
	 * @return the image HTML code
	 */
	@Deprecated
	public static String createImageRight(String url, String text) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("align", "right");
		return createImage(url, text, attributes);
	}

	/**
	 * Creates a H1 header.
	 * 
	 * @param text
	 *            the content of the header
	 * @return the HTML code
	 */
	@Deprecated
	public static String createHeader1(String text) {
		return encapsulate(text, "h1", null);
	}

	/**
	 * Creates a H2 header.
	 * 
	 * @param text
	 *            the content of the header
	 * @return the HTML code
	 */
	@Deprecated
	public static String createHeader2(String text) {
		return encapsulate(text, "h2", null);
	}

	/**
	 * Creates a H3 header.
	 * 
	 * @param text
	 *            the content of the header
	 * @return the HTML code
	 */
	@Deprecated
	public static String createHeader3(String text) {
		return encapsulate(text, "h3", null);
	}

	/**
	 * Creates a paragraph.
	 * 
	 * @param text
	 *            the text
	 * @return the paragraph
	 */
	@Deprecated
	public static String createParagraph(String text) {
		return createParagraph(text, null);
	}

	/**
	 * Creates a paragraph with optional attributes around text.
	 * 
	 * @param text
	 *            the text
	 * @param attributes
	 *            the attributes
	 * @return the paragraph
	 */
	@Deprecated
	public static String createParagraph(String text, HashMap<String, String> attributes) {
		return encapsulate(text, "p", attributes);
	}

	/**
	 * Creates an iframe HTML element.
	 * 
	 * @param id
	 *            the id of the frame
	 * @param src
	 *            the source page of the frame
	 * @return the iframe
	 */
	@Deprecated
	public static String createIFrame(String id, String src) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		attributes.put("src", src);
		return encapsulate("", "iframe", attributes);
	}

	/**
	 * Creates an empty javascript tag with attributes.
	 * 
	 * @param attributes
	 *            the attributes
	 * @return the script tag
	 */
	@Deprecated
	public static String createEmptyScript(HashMap<String, String> attributes) {
		return encapsulate("", "script", attributes);
	}

	/**
	 * Creates a javascript tag with content and attributes.
	 * 
	 * @param content
	 *            the content of the script
	 * @param attributes
	 *            the attributes
	 * @return the script tag
	 */
	@Deprecated
	public static StringBuffer createScript(StringBuffer content, HashMap<String, String> attributes) {
		return encapsulate(content, "script", attributes);
	}

	/**
	 * Create a div HTML element.
	 * 
	 * @param content
	 *            the content of the div element
	 * @param attributes
	 *            optional attributes
	 * @return the div element
	 */
	@Deprecated
	public static String createDiv(String content, HashMap<String, String> attributes) {
		return encapsulate(content, "div", attributes);
	}

	/**
	 * Create a div HTML element with an id.
	 * 
	 * @param content
	 *            the content of the div element
	 * @param id
	 *            the id of the element
	 * @return the div element
	 */
	@Deprecated
	public static String createDiv(String content, String id) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("id", id);
		attributes.put("name", id);
		return encapsulate(content, "div", attributes);
	}

	/**
	 * Creates the head and body of a HTML page.
	 * 
	 * @param title
	 *            the title of the page
	 * @param css
	 *            the css of the page
	 * @param bodyContent
	 *            the body content
	 * @param headContent
	 *            the head content
	 * @param bodyAttributes
	 *            the body attributes
	 * @return the HTML code
	 */
	@Deprecated
	public static StringBuffer createHeadBody(String title, String css, StringBuffer bodyContent, StringBuffer headContent, HashMap<String, String> bodyAttributes) {
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
	 * @param headbody
	 *            the head and body HTML code
	 * @return the HTML page
	 */
	@Deprecated
	public static StringBuffer createHTML(StringBuffer headbody) {
		StringBuffer html = new StringBuffer("<!DOCTYPE html>\n");
		html.append(encapsulate(headbody, "html", null));
		return html;
	}
}