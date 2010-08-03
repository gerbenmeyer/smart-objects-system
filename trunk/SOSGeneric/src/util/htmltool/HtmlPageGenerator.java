package util.htmltool;

import java.util.HashMap;

public class HtmlPageGenerator {

	protected String title;
	protected String css;
	protected StringBuffer headerHtml;
	protected StringBuffer onLoadScript;
	protected StringBuffer bodyHtml;
	protected StringBuffer finalScript;

	public HtmlPageGenerator(String title, String css) {
		this.title = title;
		this.css = css;
		this.headerHtml = new StringBuffer("\n");
		this.onLoadScript = new StringBuffer("");
		this.bodyHtml = new StringBuffer("\n");
		this.finalScript = new StringBuffer("\n");
	}

	public void addToHeaderHtml(String stuff) {
		headerHtml.append(stuff);
		headerHtml.append("\n");
	}

	public void addToOnLoadScript(String stuff) {
		onLoadScript.append(stuff);
	}

	public void addToBodyHtml(String stuff) {
		bodyHtml.append(stuff);
		bodyHtml.append("\n");
	}

	public void addToFinalScript(String stuff) {
		finalScript.append(stuff);
		finalScript.append("\n");
	}
	
	

	public StringBuffer createHtml() {
		
		StringBuffer body = new StringBuffer(bodyHtml);

		HashMap<String, String> scriptAttr = new HashMap<String, String>();
		scriptAttr.put("type", "text/javascript");

		body.append(HtmlTool.createScript(finalScript, scriptAttr));

		HashMap<String, String> bodyAttributes = new HashMap<String, String>();
		bodyAttributes.put("onresize", "setWindowSize();");
		bodyAttributes.put("onload", "load();" + onLoadScript.toString());

		return HtmlTool.createHeadBody(title, css, body, headerHtml, bodyAttributes);
	}
}