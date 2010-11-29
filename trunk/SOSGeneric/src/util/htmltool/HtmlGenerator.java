package util.htmltool;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class HtmlGenerator {

	protected StringBuffer buffer;
	
	public HtmlGenerator() {
		this.buffer = new StringBuffer("\n");
	}

	/**
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff the custom HTML
	 */
	public void addCustomHtml(String stuff) {
		buffer.append(stuff);
	}

	/**
	 * Adds a H2 header to the content.
	 * 
	 * @param header the header text
	 */
	public void addHeader(String header) {
		header = convertToHtml(header);
		buffer.append(HtmlTool.createHeader2(header));
	}

	/**
	 * Adds a H3 header to the content.
	 * 
	 * @param header the header text
	 */
	public void addSubHeader(String header) {
		header = convertToHtml(header);
		buffer.append(HtmlTool.createHeader3(header));
	}

	/**
	 * Adds a paragraph with text to the content.
	 * 
	 * @param text the text to be wrapped in the paragraph
	 */
	public void addParagraph(String text) {
		text = convertToHtml(text);
		buffer.append(HtmlTool.createParagraph(text, null));
	}
	
	/**
	 * Creates a link to an agent.
	 * 
	 * @param av the agent view
	 */
	public void addLinkToAgent(AgentViewable av) {
		String id = av.getID();
		buffer.append(HtmlTool.createLink(id+".html", HtmlTool.createImage(av.getIcon(), id)+av.get(Agent.LABEL), "hidden_frame"));
	}
	
	/**
	 * Creates a deeplink to an agent.
	 * 
	 * @param av the agent view
	 */
	public void addDeepLinkToAgent(AgentViewable av) {
		String id = av.getID();
		buffer.append(HtmlTool.createLink("?" + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + id, HtmlTool.createImage("link.png", "deeplink to "+id)));
	}

	/**
	 * Returns the HTML code of the details pane.
	 * 
	 * @return the code
	 */
	public StringBuffer getHtml() {
		//creating copy so the buffer cannot be modified
		return new StringBuffer(buffer);
	}
	
	/**
	 * Returns whether the pane is still empty
	 *  
	 * @return empty
	 */
	public boolean isEmpty(){
		return buffer.length()==0;
	}
	
	/**
	 * Converts text to HTML compatible text, inserting HTML entities where necessary.
	 * 
	 * @param text the input text
	 * @return the HTML compatible output
	 */
	protected static String convertToHtml(String text){
		if (text == null){
			return null;
		}
		text = text.replaceAll("\n", " ");
		text = HTMLEntities.htmlentities(text);
		return text;
	}
}