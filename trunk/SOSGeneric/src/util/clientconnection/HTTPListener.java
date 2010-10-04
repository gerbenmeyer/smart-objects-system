package util.clientconnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import main.Settings;
import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.agents.IndexAgent;
import model.agent.collection.AgentCollectionViewable;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Authenticator.Failure;
import com.sun.net.httpserver.Authenticator.Result;
import com.sun.net.httpserver.Authenticator.Retry;
import com.sun.net.httpserver.Authenticator.Success;

/**
 * The HTTPListener listens for HTTP client connections and handles the request.
 * 
 * @author W.H. Mook
 */
public class HTTPListener implements HttpHandler {
	private AgentCollectionViewable agentCollectionView;

	private final String errorPage = "<!DOCTYPE html>\n<html>\n"
		+ HtmlTool.createHeadBody("File not found", null, new StringBuffer(HtmlTool.createHeader1("File not found!")), null, null).toString()
		+ "\n</html>";

	private String htmlDir1;
	private String htmlDir2;

	HTTPAuthenticator authenticator = null;

	/**
	 * Constructs a new HTTPListener instance, with an AgentCollectionView to request data from agents and
	 * a usernames/passwords hash which is used for construction of a {@link HTTPAuthenticator}.
	 * The listening port is read from the Settings of the project, as is the directory for accessing resources.
	 * 
	 * @param acv the view
	 * @param passwords the hash
	 */
	public HTTPListener(AgentCollectionViewable acv, HashMap<String, String> passwords) {
		super();
		this.agentCollectionView = acv;

		if (!passwords.isEmpty()) {
			authenticator = new HTTPAuthenticator(Settings.getProperty(Settings.APPLICATION_NAME), passwords);
		}

		this.htmlDir1 = Settings.getProperty(Settings.HTML_DATA_DIR);

		this.htmlDir2 = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (htmlDir2.charAt(htmlDir2.length() - 1) == '/') {
			htmlDir2 = htmlDir2.substring(0, htmlDir2.length() - 1);
		}
		while (htmlDir2.charAt(htmlDir2.length() - 1) != '/') {
			htmlDir2 = htmlDir2.substring(0, htmlDir2.length() - 1);
		}
		htmlDir2 += "generichtml/";

		int port = Integer.parseInt(Settings.getProperty(Settings.HTTP_PORT));
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", this);
			server.setExecutor(null);
			server.start();
			System.out.println("Listening for HTML clients on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Decodes an URL and returns its query parameters in a HashMap. 
	 * 
	 * @param msg the URL
	 * @return the hash
	 */
	private HashMap<String, String> decodeQuery(String msg) {
		HashMap<String, String> params = new HashMap<String, String>();
		String[] msgParams = msg.split("&");
		for (String param : msgParams) {
			String[] values = param.split("=");
			if (values.length >= 2) {
				try {
					params.put(values[0], URLDecoder.decode(values[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}

	/**
	 * Creates an URL with parameters from a HashMap with keys/values.
	 * 
	 * @param params the parameters
	 * @return the URL
	 */
	private String encodeQuery(HashMap<String, String> params) {
		String query = "";
		for (String key : params.keySet()) {
			if (key.equals("timestamp")) {
				continue;
			}
			if (!query.isEmpty()) {
				query += "&";
			}
			try {
				query += key + "=" + URLEncoder.encode(params.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return query;
	}

	/**
	 * Handles a HTTP request by gathering data from the AgentCollection or serving static resources.
	 * 
	 * @param t the request
	 */
	@Override
	public void handle(HttpExchange t) throws IOException {

		try {

			boolean authenticated = false;

			if (authenticator != null) {

				Result authResult = authenticator.authenticate(t);
				if (authResult instanceof Retry) {
					Retry retryResult = (Retry) authResult;
					t.sendResponseHeaders(retryResult.getResponseCode(), 0);
				}

				if (authResult instanceof Failure) {
					Failure failureResult = (Failure) authResult;
					t.sendResponseHeaders(failureResult.getResponseCode(), 0);
				}

				if (authResult instanceof Success) {
					authenticated = true;
				}

			} else {
				authenticated = true;
			}

			if (!authenticated) {
				OutputStream os = t.getResponseBody();
				os.close();
				return;
			}

			String query = "";
			if (t.getRequestURI().getQuery() != null) {
				query = t.getRequestURI().getQuery();
			}

			HashMap<String, String> params = decodeQuery(query);

			String paramString = (encodeQuery(params));

			t.getRequestBody();
			byte[] bytes = new byte[0];

			// display code
			File file;
			File altFile;
			if (t.getRequestURI().getPath().equals("/")) {
				file = new File(htmlDir1 + "index.html");
				altFile = new File(htmlDir2 + "index.html");
			} else if (t.getRequestURI().getPath().equals("/m")) {
				file = new File(htmlDir1 + "mobile.html");
				altFile = new File(htmlDir2 + "mobile.html");
			} else {
				file = new File(htmlDir1 + t.getRequestURI().getPath().substring(1));
				altFile = new File(htmlDir2 + t.getRequestURI().getPath().substring(1));
			}

			String filename = file.getName();
			String[] fileParts = filename.split("\\.");
			String agentCode = "";
			String extension = "";
			boolean details = false;
			if (fileParts.length == 2) {
				agentCode = fileParts[0];
				extension = fileParts[1];
			}

			details = agentCode.contains("details");
			String[] agentCodeParts = agentCode.split("_details");
			if (agentCodeParts.length == 1) {
				agentCode = agentCodeParts[0];
			}

			if (extension.equals("html") && !agentCode.isEmpty() && agentCollectionView.containsKey(agentCode)) {
				// System.out.println("This is an agent: " + agentCode +
				// ", details: " + details);
				AgentViewable av = agentCollectionView.get(agentCode);
				StringBuffer html;
				if (av instanceof IndexAgent) {
					html = ((IndexAgent) av).generatePage(params);
				} else if (details) {
					HtmlDetailsPaneContentGenerator detailsPane = new HtmlDetailsPaneContentGenerator();
					av.generateDetailsPaneContent(detailsPane, params);
					html = detailsPane.createHtml();
				} else {
					HtmlMapContentGenerator mapContent = new HtmlMapContentGenerator(agentCode);

					// TODO: Use other property then hidden for this!
					if (Boolean.parseBoolean(av.get(Agent.HIDDEN))) {
						if (Settings.getProperty(Settings.SHOW_OVERVIEW_LISTS).equals("true")) {
							mapContent.addCustomScript("parent.document.getElementById('details_canvas').innerHTML = 'Loading ...';\n");
							mapContent.addCustomScript("parent.setDetailsSize(true);\n");
							mapContent.addCustomScript("parent.loadDetails('" + agentCode + "_details.html"
									+ (!paramString.isEmpty() ? "?" + paramString : "") + "');\n");
						} else {
							mapContent.addCustomScript("parent.setDetailsVisible(false);\n");
						}
					} else {
						if (Settings.getProperty(Settings.SHOW_AGENT_DETAILS).equals("true")) {
							mapContent.addCustomScript("parent.document.getElementById('details_canvas').innerHTML = 'Loading ...';\n");
							mapContent.addCustomScript("parent.setDetailsSize("
									+ Settings.getProperty(Settings.SHOW_AGENT_DETAILS_SMALL).equals(
											Boolean.toString(true)) + ");\n");
							mapContent.addCustomScript("parent.loadDetails('" + agentCode + "_details.html"
									+ (!paramString.isEmpty() ? "?" + paramString : "") + "');\n");
						} else {
							mapContent.addCustomScript("parent.setDetailsVisible(false);\n");
						}
					}

					av.generateMapContent(mapContent, params);

					html = mapContent.createHtml();
				}
				bytes = html.toString().getBytes();
				t.getResponseHeaders().add("Content-Type", "text/html");
				t.sendResponseHeaders(200, bytes.length);
			} else if (altFile.isFile()) {
				// System.out.println("This is NOT an agent: " + filename);
				bytes = new byte[(int) altFile.length()];
				InputStream is = new FileInputStream(altFile);
				is.read(bytes);
				is.close();

				if (filename.endsWith(".png")) {
					t.getResponseHeaders().add("Content-Type", "image/png");
				} else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
					t.getResponseHeaders().add("Content-Type", "text/html");
				} else if (filename.endsWith(".js")) {
					t.getResponseHeaders().add("Content-Type", "text/javascript");
				} else if (filename.endsWith(".css")) {
					t.getResponseHeaders().add("Content-Type", "text/css");
				}
				t.sendResponseHeaders(200, bytes.length);
			} else if (file.isFile()) {
				// System.out.println("This is NOT an agent: " + filename);
				bytes = new byte[(int) file.length()];
				InputStream is = new FileInputStream(file);
				is.read(bytes);
				is.close();

				if (filename.endsWith(".png")) {
					t.getResponseHeaders().add("Content-Type", "image/png");
				} else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
					t.getResponseHeaders().add("Content-Type", "text/html");
				} else if (filename.endsWith(".js")) {
					t.getResponseHeaders().add("Content-Type", "text/javascript");
				} else if (filename.endsWith(".css")) {
					t.getResponseHeaders().add("Content-Type", "text/css");
				}
				t.sendResponseHeaders(200, bytes.length);
			} else {
				System.out.println("This is NOT good: " + filename);
				bytes = errorPage.getBytes();
				t.getResponseHeaders().add("Content-Type", "text/html");
				t.sendResponseHeaders(404, 0);
			}

			OutputStream os = t.getResponseBody();
			os.write(bytes);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}