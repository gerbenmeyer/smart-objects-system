package util.htmltool;

import util.HTMLEntities;

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
	 * Encodes a text to HTML compatible text.
	 * 
	 * @param text
	 *            the text to encode
	 * @return the encoded text
	 */
	public static String encodeHtml(String text) {
		if (text == null) {
			return null;
		}
		text = text.replaceAll("\n", " ");
		text = HTMLEntities.htmlentities(text);
		return text;
	}

	/**
	 * Decodes a HTML compatible text.
	 * 
	 * @param text
	 *            the text to decode
	 * @return the decoded text
	 */
	public static String decodeHtml(String text) {
		if (text == null) {
			return null;
		}
		return HTMLEntities.unhtmlentities(text);
	}

	/**
	 * Encodes a text to HTML compatible text, including &gt;, &lt;, en quotes.
	 * 
	 * @param text
	 *            the text to encode
	 * @return the encoded text
	 */
	public static String encodeHtmlComponent(String text) {
		if (text == null) {
			return null;
		}
		text = text.replace("\n", " ");
		text = HTMLEntities.htmlentities(text);
		text = HTMLEntities.htmlAngleBrackets(text);
		text = HTMLEntities.htmlQuotes(text);
		text = text.replace("\\", "&#92;");
		return text;
	}

	/**
	 * Decodes a HTML compatible text, including &gt;, &lt;, en quotes.
	 * 
	 * @param text
	 *            the text to decode
	 * @return the decoded text
	 */
	public static String decodeHtmlComponent(String text) {
		if (text == null) {
			return null;
		}
		text = text.replace("&#92;", "\\");
		text = HTMLEntities.unhtmlQuotes(text);
		text = HTMLEntities.unhtmlAngleBrackets(text);
		text = HTMLEntities.unhtmlentities(text);
		return text;
	}

}