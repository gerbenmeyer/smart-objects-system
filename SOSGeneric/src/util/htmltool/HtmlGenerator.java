package util.htmltool;

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
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void add(StringBuffer stuff) {
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
	 * Adds a piece of HTML code to the front of the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void insert(StringBuffer stuff) {
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

}