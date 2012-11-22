package util.clientconnection;

import java.net.ServerSocket;
import java.net.Socket;

import main.SOSServer;
import main.Settings;

/**
 * A listener which delegates received XML based requests.
 * 
 * @author Gerben G. Meyer
 */
public class XMLListener implements Runnable {
	
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
		SOSServer.getDevLogger().info("Listening for XML clients on port " + port);
		(new Thread(this)).start();
	}
	
	/**
	 * Start listening and handling.
	 */
	public void run(){
		while (true) {
			try {
				ServerSocket sock = new ServerSocket(port);
				Socket clientSocket = sock.accept();
				new XMLClientConnection(server, clientSocket);
				sock.close();
			} catch (Exception e) {
				SOSServer.getDevLogger().warning("A problem occured with waiting for clients: " + e.getMessage());
			}
		}
	}
}