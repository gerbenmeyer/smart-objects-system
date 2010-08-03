package util.clientconnection;

import java.util.HashMap;

import com.sun.net.httpserver.BasicAuthenticator;

public class HTTPAuthenticator extends BasicAuthenticator {

	private HashMap<String,String> passwords;
	
	public HTTPAuthenticator(String realm, HashMap<String,String> passwords) {
		super(realm);
		this.passwords = passwords;
	}

	@Override
	public boolean checkCredentials(String username, String password) {
		return passwords.containsKey(username) && passwords.get(username).equals(password);
	}

}
