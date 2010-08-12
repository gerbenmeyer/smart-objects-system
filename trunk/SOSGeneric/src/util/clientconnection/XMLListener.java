package util.clientconnection;

import java.net.ServerSocket;
import java.net.Socket;

import main.SOSServer;
import main.Settings;

/**
 * A listener which delegates received XML based requests.
 * 
 * @author G.G. Meyer
 */
public class XMLListener {
	
	private SOSServer server;
	private int port;

	/**
	 * Constructs a new XMLListener instance for a SOSServer.
	 * 
	 * @param server the server instance
	 */
	public XMLListener(SOSServer server) {
		this.server = server;
		port = Integer.parseInt(Settings.getProperty(Settings.XML_PORT));
		System.out.println("Listening for XML clients on port " + port);
		listen();
	}
	
	/**
	 * Start listening and handling.
	 */
	private void listen(){
		while (true) {
			try {
				ServerSocket sock = new ServerSocket(port);
				Socket clientSocket = sock.accept();
				new XMLServerClientHandler(server, clientSocket);
				sock.close();
			} catch (Exception e) {
				System.out.println("A problem occured with waiting for clients: " + e.getMessage());
			}
		}
	}
}