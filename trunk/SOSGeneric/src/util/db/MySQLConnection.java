package util.db;

import java.sql.Connection;
import java.sql.DriverManager;

import main.Settings;

public class MySQLConnection {
	private static MySQLConnection instance = null;

	public Connection connection = null;
	
	public static MySQLConnection getInstance() {
		if (instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}
	
	private MySQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://"+Settings.getProperty(Settings.DATABASE_HOST)+"/"+Settings.getProperty(Settings.DATABASE_NAME)+
                                               "?user="+Settings.getProperty(Settings.DATABASE_USER)+"&password="+Settings.getProperty(Settings.DATABASE_PASSWORD));
            if (connection == null) {
    			System.err.println("no database connection to "+Settings.getProperty(Settings.DATABASE_HOST)+"!");
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}