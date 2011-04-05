package util.clientconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import util.xmltool.XMLTool;

/**
 * XMLServerConnection is used to connect a client to a server and send commands
 * to the server.
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

	private int connections = 0;

	/**
	 * Constructs a new XMLServerConnection instance to a certain server.
	 * 
	 * @param serverAddress
	 *            the server address
	 * @param serverPort
	 *            the server port
	 * @param username
	 *            a valid username for the server
	 * @param password
	 *            the user's password
	 */
	public XMLServerConnection(String serverAddress, int serverPort,
			String username, String password) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
	}

	/**
	 * Build the connection.
	 */
	public void connect() {
		synchronized (this) {
			if (connections == 0) {
				try {
					sock = new Socket(serverAddress, serverPort);
					output = new PrintWriter(sock.getOutputStream(), true);
					input = new BufferedReader(new InputStreamReader(sock
							.getInputStream()));
					output.println(username);
					output.println(password);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Unable to connect to server "
							+ serverAddress + ":" + serverPort);
				}
			}
			connections++;
		}
	}

	/**
	 * Disconnect the connection.
	 */
	public void disconnect() {
		synchronized (this) {
			connections = Math.max(connections - 1, 0);
			if (connections == 0) {
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
				}
			}
		}
	}
	
	/**
	 * Sends an XMLServerCommand to the server for handling.
	 * 
	 * @param command
	 *            the command
	 * @return the result
	 */
	public synchronized String sendCommandToServer(XMLCommand command) {
		String result = "unknown";
		synchronized (this) {
			if (connections == 0) {
				System.err.println("not connected");
				result = "error";
			} else {
				String msg = XMLTool.addRootTag(command.toXML(), "Command");
				output.println(msg);

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
					result = "error";
				} else {
					result = XMLTool.removeRootTag(result);
					if (result.equals("error")) {
						System.err
								.println("The server was not able to process the command \'"
										+ command.getName()
										+ "\' with parameter "
										+ command.getParameter());
					}
					if (result.equals("unknown")) {
						System.err.println(
								"The server did not know the command \'"
										+ command.getName()
										+ "\' with parameter "
										+ command.getParameter()
										+ "\n-command-\n" + command.toXML());
					}
				}
			}

		}
		return result;
	}
}