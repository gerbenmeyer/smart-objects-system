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
import util.htmltool.HtmlDetailsContentGenerator;
import util.htmltool.HtmlGenerator;
import util.htmltool.HtmlMapBalloonContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

import com.sun.net.httpserver.Authenticator.Failure;
import com.sun.net.httpserver.Authenticator.Result;
import com.sun.net.httpserver.Authenticator.Retry;
import com.sun.net.httpserver.Authenticator.Success;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
	public final static HashMap<String, String> decodeQuery(String msg) {
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
	public final static String encodeQuery(HashMap<String, String> params) {
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
			} else if (path.endsWith(".map") || path.endsWith(".details") || path.endsWith(".train")) { 
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
			if (fileParts.length == 2) {
				agentCode = fileParts[0];
				extension = fileParts[1];
			}

//			details = agentCode.contains("details");
//			String[] agentCodeParts = agentCode.split("_details");
//			if (agentCodeParts.length == 1) {
//				agentCode = agentCodeParts[0];
//			}
			
			boolean agentExtension = extension.equals("html") || extension.equals("details") || extension.equals("map") || extension.equals("train") || extension.equals("balloon"); 

			if (agentExtension && !agentCode.isEmpty() && agentCollectionView.containsKey(agentCode)) {
				byte[] bytes = new byte[0];
				AgentViewable av = agentCollectionView.get(agentCode);
				StringBuffer html = new StringBuffer();
				if (extension.equals("html") && av instanceof IndexAgent) {
					html = ((IndexAgent) av).generatePage(params);
				} else if (extension.equals("details")) {
					HtmlDetailsContentGenerator detailsPane = new HtmlDetailsContentGenerator();
					av.generateDetailsContent(detailsPane, params);
					html = detailsPane.getHtml();
				} else if (extension.equals("map")) {
					HtmlMapContentGenerator mapContent = new HtmlMapContentGenerator(agentCode);
					
					boolean showSidePane = av.needsDetailsPane();
					
					if (showSidePane && av.getID().equals("search")) {
						showSidePane = Settings.getProperty(Settings.SHOW_OVERVIEW_LISTS).equals("true");
					}
						
					if (showSidePane){
						mapContent.addCustomScript("parent.document.getElementById('details_canvas').innerHTML = 'Loading ...';");
						mapContent.addCustomScript("parent.loadDetails('" + agentCode + ".details"
								+ (!paramString.isEmpty() ? "?" + paramString : "") + "');");
					}

					av.generateMapContent(mapContent, params);
					html = mapContent.createMapContentScript();
				} else if (extension.equals("train")) {
					HtmlGenerator content = new HtmlGenerator();
					av.teachStatus(content,params);
					html = content.createScript();
				} else if (extension.equals("balloon")) {
					HtmlMapBalloonContentGenerator balloonContentGen = new HtmlMapBalloonContentGenerator();
					av.generateMapBalloonContent(balloonContentGen, params);
					html = balloonContentGen.getHtml();
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
				
				if (filename.endsWith(".png")) {
					t.getResponseHeaders().add("Content-Type", "image/png");
				} else if (filename.endsWith(".html") || filename.endsWith(".htm")) {
					t.getResponseHeaders().add("Content-Type", "text/html");
				} else if (filename.endsWith(".js")) {
					t.getResponseHeaders().add("Content-Type", "text/javascript");
				} else if (filename.endsWith(".css")) {
					t.getResponseHeaders().add("Content-Type", "text/css");
				}
				t.sendResponseHeaders(200, 0);
				
				int c;
				OutputStream os = t.getResponseBody();
				while ((c = is.read()) != -1) {
	                os.write(c);
	            }
				
				is.close();
				os.close();

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