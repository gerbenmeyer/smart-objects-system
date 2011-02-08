package util.clientconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.SOSServer;
import util.xmltool.XMLTool;

/**
 * XMLServerConnection is used to connect a client to a server and send commands to the server.
 * 
 * @author Gerben G. Meyer
 */
public class XMLServerConnection {
	private Socket sock;
	private PrintWriter output;
	private BufferedReader input;
	
	private String serverAddress;
	private int serverPort;
	private String username;
	private String password;
	
	private boolean connected = false;

	/**
	 * Constructs a new XMLServerConnection instance to a certain server.
	 * 
	 * @param serverAddress the server address
	 * @param serverPort the server port
	 * @param username a valid username for the server
	 * @param password the user's password
	 */
	public XMLServerConnection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
	}

	/**
	 * Build the connection.
	 */
	public void connect(){
		if (connected){
			return;
		}
		try {
			sock = new Socket(serverAddress, serverPort);
			output = new PrintWriter(sock.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output.println(username);
			output.println(password);
			connected = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to connect to server "+serverAddress+":"+serverPort);
		}
	}
	
	/**
	 * Disconnect the connection.
	 */
	public void disconnect(){
		try {
			input.close();
		} catch (Exception e) {
		}
		try {
			output.flush();
			output.close();
		} catch (Exception e) {
		}
		try {
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		connected = false;
	}
	/**
	 * Sends an XMLServerCommand to the server for handling.
	 * 
	 * @param command the command
	 * @return the result
	 */
	public synchronized String sendCommandToServer(XMLCommand command) {
		if (!connected){
			System.err.println("not connected");
			return "error";
		}
		String msg = XMLTool.addRootTag(command.toXML(), "Command");
		output.println(msg);

		String result = "";
		boolean error = false;
		try {
			result = input.readLine();
			if (result == null) {
				error = true;
			}
		} catch (IOException e) {
			error = true;
		}
		if (error) {
			System.err.println("Connection to server lost");
			System.exit(0);
		}
		result = XMLTool.removeRootTag(result);
		if (result.equals("error")) {
			System.err.println("The server was not able to process the command \'" + command.getName()
					+ "\' with parameter " + command.getParameter());
		}
		if (result.equals("unknown")) {
			SOSServer.getDevLogger().warning("The server did not know the command \'" + command.getName() + "\' with parameter "
					+ command.getParameter()+"\n-command-\n"+command.toXML());
			
//			System.err.println("----");
//			System.err.println(command.toXML());
//			System.err.println("----");
		}
		return result;
	}
}