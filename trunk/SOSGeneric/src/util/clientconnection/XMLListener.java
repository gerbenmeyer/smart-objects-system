package util.clientconnection;

import java.net.ServerSocket;
import java.net.Socket;

import main.SOSServer;
import main.Settings;

public class XMLListener {
	
	private SOSServer server;
	
	private int port;

	public XMLListener(SOSServer server) {
		this.server = server;
		port = Integer.parseInt(Settings.getProperty(Settings.XML_PORT));
		System.out.println("Listening for XML clients on port " + port);
		listen();
	}
	
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
