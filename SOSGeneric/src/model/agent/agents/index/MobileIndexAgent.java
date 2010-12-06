package model.agent.agents.index;

import java.util.HashMap;

import main.Settings;
import model.agent.agents.IndexAgent;
import util.htmltool.HtmlMobileMapPageGenerator;
import util.htmltool.HtmlTool;

/**
 * This agent provides methods for generating an index page for mobile devices.
 * 
 * @author Gerben G. Gijs B. Roest, Rijksuniversiteit Groningen.
 * @version 24 jun 2010
 * 
 */
public class MobileIndexAgent extends IndexAgent {

	/**
	 * Constructs a new MobileIndexAgent object.
	 * 
	 * @param id the identifier for the agent
	 */
	public MobileIndexAgent(String id) {
		super(id);
	}

	@Override
	public void act() throws Exception {
	}

	@Override
	public boolean isGarbage() {
		return false;
	}

	@Override
	public void lastWish() {
		System.out.println("Mobileagent: I am dying!!! ARGH!!!");
	}

	@Override
	public StringBuffer generatePage(HashMap<String, String> params) {
		String appName = Settings.getProperty(Settings.APPLICATION_NAME);

		HtmlMobileMapPageGenerator htmlPage = new HtmlMobileMapPageGenerator(appName, "main.css");
	
		String header = HtmlTool.createImageLeft(Settings.getProperty(Settings.APPLICATION_ICON), "logo");
		header += HtmlTool.createHeader1(Settings.getProperty(Settings.APPLICATION_NAME));
		header += HtmlTool.createHeader2(Settings.getProperty(Settings.APPLICATION_VERSION));
		
		htmlPage.addToBodyHtml(HtmlTool.createDiv(header, "header_canvas"));

		// default source
		String source = Settings.getProperty(Settings.DEFAULT_SCRIPT);

		// deeplink to id.
		if (params != null && params.containsKey(Settings.getProperty(Settings.KEYWORD_DEEPLINK))
				&& params.get(Settings.getProperty(Settings.KEYWORD_DEEPLINK)) != null) {
			source = params.get(Settings.getProperty(Settings.KEYWORD_DEEPLINK)) + ".html?deeplink=true";
		}
		
		htmlPage.addToOnLoadScript("document.getElementById('hidden_frame').src='" + source + "';");

		// clustering enabled / disabled.
		if (Settings.getProperty(Settings.DEFAULT_CLUSTERING).equals(Boolean.toString(true))) {
			htmlPage.addToOnLoadScript("setClustering(true);");
		}

		// HtmlTool.outputHTML(Settings.HTML_DATA_DIR + "index.html", html);
		return HtmlTool.createHTML(htmlPage.getHtml());		
	}
}