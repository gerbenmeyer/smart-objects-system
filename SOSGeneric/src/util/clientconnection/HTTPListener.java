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

			// display code
			File file;
			InputStream inputStream = null;
			String path = t.getRequestURI().getPath();
			if (path.equals("/")) {
				file = new File("index.html");
			} else if (path.equals("/m")) {
				file = new File("mobile.html");
			} else if (path.endsWith(".html")) { 
				file = new File(path);
			} else {
				file = new File(Settings.getProperty(Settings.HTML_DATA_DIR)+path.substring(1));
				if (!file.isFile()) {
					inputStream = this.getClass().getResourceAsStream("/resources/"+path.substring(1));
				}
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
				byte[] bytes = new byte[0];
				AgentViewable av = agentCollectionView.get(agentCode);
				StringBuffer html;
				if (av instanceof IndexAgent) {
					html = ((IndexAgent) av).generatePage(params);
				} else if (details) {
					HtmlDetailsPaneContentGenerator detailsPane = new HtmlDetailsPaneContentGenerator();
					av.generateDetailsPaneContent(detailsPane, params);
					html = detailsPane.getHtml();
				} else {
					HtmlMapContentGenerator mapContent = new HtmlMapContentGenerator(agentCode);

					boolean detailsSmall = true;
					boolean showSidePane = true;
					
					if (av.needsDetailsPane()) {
						detailsSmall = Settings.getProperty(Settings.SHOW_AGENT_DETAILS_SMALL).equals(Boolean.toString(true));
					} else {
						showSidePane = false;
					}
				
					if (av.getID().equals("search")) {
						if (Settings.getProperty(Settings.SHOW_OVERVIEW_LISTS).equals("true")) {
							detailsSmall = true;
						} else {
							showSidePane = false;
						}
					}
						
					if (showSidePane){
						mapContent.addCustomScript("parent.document.getElementById('details_canvas').innerHTML = 'Loading ...';\n");
						mapContent.addCustomScript("parent.setDetailsSize("	+ detailsSmall + ");\n");
						mapContent.addCustomScript("parent.loadDetails('" + agentCode + "_details.html"
								+ (!paramString.isEmpty() ? "?" + paramString : "") + "');\n");
					}

					av.generateMapContent(mapContent, params);
					html = mapContent.createHtml();
				}
				bytes = html.toString().getBytes();
				t.getResponseHeaders().add("Content-Type", "text/html");
				t.sendResponseHeaders(200, bytes.length);

				OutputStream os = t.getResponseBody();
				os.write(bytes);
				os.close();
			} else if (file.isFile() || inputStream != null) {
				InputStream is;
				if (file.isFile()) {
					is = new FileInputStream(file);
				} else {
					is = inputStream;
				}
				int c;
				t.sendResponseHeaders(200, 0);
				OutputStream os = t.getResponseBody();
				while ((c = is.read()) != -1) {
	                os.write(c);
	            }
				is.close();
				os.close();

				if (filename.endsWith(".png")) {
					t.getResponseHeaders().add("Content-Type", "image/png");
				} else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
					t.getResponseHeaders().add("Content-Type", "text/html");
				} else if (filename.endsWith(".js")) {
					t.getResponseHeaders().add("Content-Type", "text/javascript");
				} else if (filename.endsWith(".css")) {
					t.getResponseHeaders().add("Content-Type", "text/css");
				}
			} else {
				System.out.println("This is NOT good: " + filename);
				t.getResponseHeaders().add("Content-Type", "text/html");
				t.sendResponseHeaders(404, 0);
				OutputStream os = t.getResponseBody();
				os.write(errorPage.getBytes());
				os.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}