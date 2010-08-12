package util.clientconnection;

import java.util.HashMap;

import com.sun.net.httpserver.BasicAuthenticator;

/**
 * This is a BasicAuthenticator implementation which is used for authenticating web users. 
 * 
 * @author G.G. Meyer
 */
public class HTTPAuthenticator extends BasicAuthenticator {

	private HashMap<String,String> passwords;
	
	/**
	 * Constructs a new HTTPAuthenticator for a given realm.
	 * Valid usernames and passwords must be passed in a HashMap.
	 * 
	 * @param realm the realm
	 * @param passwords the username and password hash
	 */
	public HTTPAuthenticator(String realm, HashMap<String,String> passwords) {
		super(realm);
		this.passwords = passwords;
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		return passwords.containsKey(username) && passwords.get(username).equals(password);
	}
}