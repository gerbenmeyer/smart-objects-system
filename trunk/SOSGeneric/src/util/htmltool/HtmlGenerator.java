package util.htmltool;

import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

public class HtmlGenerator {

	protected StringBuffer buffer;
	
	public HtmlGenerator() {
		this.buffer = new StringBuffer();
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
	 * Adds a piece of HTML code to the content.
	 * 
	 * @param stuff
	 *            the custom HTML
	 */
	public void addCustomScript(String stuff) {
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
	 * Adds an image to the content.
	 * 
	 * @param image
	 * @param text
	 */
	public void addImage(String image, String text){
		addDiv(HtmlTool.createImage(image, text));
	}
	
	/**
	 * Adds a floating image to the right side of the content.
	 * 
	 * @param image
	 * @param text
	 */
	public void addImageRight(String image, String text){
		buffer.append(HtmlTool.createImageRight(image, text));
	}
	
	/**
	 * Creates a header with link the agent icon and label.
	 * 
	 * @param av the agent view
	 */
	public void addAgentHeaderLink(AgentViewable av) {
		String id = av.getID();
		if (av.needsDetailsPane()){
			addHeader(HtmlTool.createLink(id+".map", HtmlTool.createImage(av.getIcon(), id)+av.get(Agent.LABEL), "hidden_frame")+HtmlTool.createLink("?" + Settings.getProperty(Settings.KEYWORD_DEEPLINK) + "=" + id, HtmlTool.createImage("link.png", "deeplink to "+id)));
		} else {
			addHeader(HtmlTool.createImage(av.getIcon(), id)+av.get(Agent.LABEL));
		}
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
	 * Adds a content div.
	 * 
	 * @param text the text to be wrapped in the div
	 */
	public void addDiv(String text) {
		text = convertToHtml(text);
		HashMap<String, String> attr = new HashMap<String, String>();
		attr.put("class", "contentDiv");
		buffer.append(HtmlTool.createDiv(text, attr));
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
		return HTMLEntities.htmlentities(text.replaceAll("\n", " "));
	}
	
	/**
	 * Returns the HTML code of the script.
	 * 
	 * @return the code
	 */
	public StringBuffer createScript() {
		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		StringBuffer headcontent = new StringBuffer();
		headcontent.append(HtmlTool.createScript(buffer, scriptAttr));
		StringBuffer headbody = HtmlTool.createHeadBody("", null, new StringBuffer(), headcontent, null);

		return HtmlTool.createHTML(headbody);
	}
	
}