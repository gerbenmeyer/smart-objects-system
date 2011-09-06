package util.htmltool;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;

public class HtmlGenerator {

	private StringBuffer buffer;

	public HtmlGenerator() {
		this.buffer = new StringBuffer();
	}

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void add(String stuff) {
		buffer.append(stuff);
	}
	
	/**
	 * Adds a piece of HTML code to the front of the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void insert(String stuff) {
		buffer.insert(0,stuff);
	}	
	
	
	
	/**
	 * Adds a header to the content.
	 * 
	 * @param header
	 *            the header text
	 */
	public void addHeader(String header) {
		add(HtmlTool.h2(header));
	}

	/**
	 * Adds a subheader to the content.
	 * 
	 * @param header
	 *            the header text
	 */
	public void addSubHeader(String header) {
		add(HtmlTool.h3(header));
	}

	/**
	 * Adds a paragraph to the content
	 * 
	 * @param content
	 *            the content of the paragraph
	 */
	public void addParagraph(String content) {
		add(HtmlTool.p(content));
	}

	/**
	 * Creates a header with the agent icon and label.
	 * 
	 * @param av
	 *            the agent view
	 */
	public void addAgentHeader(AgentViewable av) {
		String id = av.getID();
		String status = "";
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true))) {
			status = " " + HtmlTool.img(av.getStatus().toString().toLowerCase() + ".png", av.getStatus().toString().toLowerCase());
		}
		add(HtmlTool.h2(HtmlTool.img(av.getIcon(), id) + " " + av.get(Agent.LABEL) + status));
	}

	/**
	 * Creates a header with link the agent icon and label.
	 * 
	 * @param av
	 *            the agent view
	 */
	public void addAgentHeaderLink(AgentViewable av) {
		if (!av.needsDetailsPane()) {
			addAgentHeader(av);
			return;
		}
		String id = av.getID();
		String status = "";
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true))) {
			status = " " + HtmlTool.img(av.getStatus().toString().toLowerCase() + ".png", av.getStatus().toString().toLowerCase());
		}
		add(HtmlTool.h2(HtmlTool.img(av.getIcon(), id) + " " + HtmlTool.aLink(av.get(Agent.LABEL), id + ".map", "target=\"hidden_frame\"") + status));
	}

	/**
	 * Creates a header with link the agent icon and label.
	 * 
	 * @param av
	 *            the agent view
	 */
	public void addAgentLink(AgentViewable av) {
		String id = av.getID();
		String status = "";
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true))) {
			status = " " + HtmlTool.img(av.getStatus().toString().toLowerCase() + ".png", av.getStatus().toString().toLowerCase());
		}
		add(HtmlTool.p(HtmlTool.img(av.getIcon(), id) + " " + HtmlTool.aLink(av.get(Agent.LABEL), id + ".map", "target=\"hidden_frame\"") + status));
	}	
	
	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	@Deprecated
	public void addCustomHtml(String stuff) {
		buffer.append(stuff);
	}

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	@Deprecated
	public void addCustomScript(String stuff) {
		buffer.append(stuff);
	}

	/**
	 * Adds a paragraph with text to the content.
	 * 
	 * @param text
	 *            the text to be wrapped in the paragraph
	 */
	@Deprecated
	public void addParagraph(String text, HashMap<String, String> attributes) {
		buffer.append(HtmlTool.createParagraph(text, attributes));
	}

	/**
	 * Adds an image to the content.
	 * 
	 * @param image
	 * @param text
	 */
	@Deprecated
	public void addImage(String image, String text) {
		addParagraph(HtmlTool.createImage(image, text));
	}

	/**
	 * Adds a floating image to the right side of the content.
	 * 
	 * @param image
	 * @param text
	 */
	@Deprecated
	public void addImageRight(String image, String text) {
		buffer.append(HtmlTool.createImageRight(image, text));
	}

	/**
	 * Adds a content div.
	 * 
	 * @param text
	 *            the text to be wrapped in the div
	 */
	@Deprecated
	public void addDiv(String text) {
		HashMap<String, String> attr = new HashMap<String, String>();
		attr.put("class", "contentDiv");
		buffer.append(HtmlTool.createDiv(text, attr));
	}

	/**
	 * Adds a custom content div.
	 * 
	 * @param text
	 *            the text to be wrapped in the div
	 * @param attributes
	 *            all html attributes of the div
	 */
	@Deprecated
	public void addCustomDiv(String text, HashMap<String, String> attributes) {
		buffer.append(HtmlTool.createDiv(text, attributes));
	}

	/**
	 * Returns the HTML code of the details pane.
	 * 
	 * @return the code
	 */
	public StringBuffer getBuffer() {
		// creating copy so the buffer cannot be modified
		return new StringBuffer(buffer);
	}

	/**
	 * Returns whether the pane is still empty
	 * 
	 * @return empty
	 */
	public boolean isEmpty() {
		return buffer.length() == 0;
	}

	/**
	 * Returns the HTML code of the script.
	 * 
	 * @return the code
	 */
	@Deprecated
	public StringBuffer createScript() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = new StringBuffer();
		headcontent.append(HtmlTool.createScript(buffer, scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody("", null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}

}