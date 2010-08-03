package util.clientconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import util.xmltool.XMLTool;

public class XMLServerConnection {
	private Socket sock;
	private PrintWriter output;
	private BufferedReader input;
	
	private String serverAddress;
	private int serverPort;
	private String username;
	private String password;
	
	private boolean connected = false;

	public XMLServerConnection(String serverAddress, int serverPort, String username, String password) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
	}

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
			System.err.println("Unable to connect to server");
		}
	}
	
	public void disconnect(){
		try {
			input.close();
		} catch (Exception e) {
		}
		try {
		output.close();
		} catch (Exception e) {
		}
		try {
			sock.close();
		} catch (Exception e) {
		}
		connected = false;
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	public synchronized String sendCommandToServer(XMLServerCommand command) {
		if (!connected){
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
			System.err.println("The server did not know the command \'" + command.getName() + "\' with parameter "
					+ command.getParameter());
			
			System.err.println("----");
			System.err.println(command.toXML());
			System.err.println("----");
		}
		return result;
	}
	

}
