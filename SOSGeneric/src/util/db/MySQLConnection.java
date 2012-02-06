package util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import main.Settings;

/**
 * This singleton class holds the connection to a MySQL database.
 * Only one instance may be present per application.
 * 
 * @author W.H. Mook
 */
public class MySQLConnection {

	private final static String[] tables = new String[] {"agents", "properties", "locations", "classification"};
	private static MySQLConnection instance = null;

	private Connection connection = null;
	private int counter = 0;

	/**
	 * Get the instance of MySQLConnection for this application.
	 * 
	 * @return the instance
	 */
	public static MySQLConnection getInstance() {
		if (instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}
	
	public Connection getConnection(){
		counter++;
		return connection;
	}
	
	public int getCounter(){
		return counter;
	}
	
	public void resetCounter(){
		counter = 0;
	}	

	/**
	 * Constructs a new MySQLConnection object.
	 */
	private MySQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://"+Settings.getProperty(Settings.DATABASE_HOST)+"/"
                                               + "?user="+Settings.getProperty(Settings.DATABASE_USER)+"&password="+Settings.getProperty(Settings.DATABASE_PASSWORD));
            if (connection == null) {
            	throw new Exception("could not connect to database "+Settings.getProperty(Settings.DATABASE_HOST)+"!");
            }
            createDatabase();
            createTables();
        } catch (Exception ex) {
        	System.err.println("Unable to connect to the MySQL database: "+ex.getMessage()+".\nExiting...");
        	System.exit(1);
        }
    }

	/**
	 * Creates the predefined table. 
	 * 
	 * @param tableName the name of the table to create.
	 * @return success
	 */
	private void createTables() {
		for (String tableName : tables) {
			Statement stm = null;
			try {
				String sql = "";
				if (tableName.equals("agents")) {
					sql = "CREATE TABLE IF NOT EXISTS `agents` ("
						+ "  `id` varchar(32) NOT NULL,"
						+ "  `label` varchar(128) NOT NULL,"
						+ "  `description` text NOT NULL,"
						+ "  `status` enum('UNKNOWN','OK','WARNING','ERROR') NOT NULL,"
						+ "  `hidden` enum('false','true') NOT NULL DEFAULT 'false',"
						+ "  `type` varchar(32) NOT NULL,"
						+ " `location` varchar(200) NOT NULL,"
						+ "  PRIMARY KEY (`id`),"
						+ "  KEY `label` (`label`),"
						+ "  KEY `status` (`status`),"
						+ "  KEY `type` (`type`),"
						+ "  KEY `hidden` (`hidden`)"
						+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
				} else if (tableName.equals("properties")) {
					sql = "CREATE TABLE IF NOT EXISTS `properties` ("
						+ "  `agent_id` varchar(32) NOT NULL,"
						+ "  `type` enum('UNKNOWN','BOOLEAN','INTEGER','NUMBER','TEXT','TIME','TIMEWINDOW','LOCATION','STATUS','DEPENDENCIES','HISTORY','OBJECT') NOT NULL,"
						+ "  `name` varchar(32) NOT NULL,"
						+ "  `value` longtext NOT NULL,"
						+ "  PRIMARY KEY (`agent_id`,`name`),"
						+ " KEY `agent_id` (`agent_id`),"
						+ " KEY `type` (`type`)"
						+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
				} else if (tableName.equals("locations")) {
					sql = "CREATE TABLE IF NOT EXISTS `locations` ("
						+ "  `id` int(11) NOT NULL AUTO_INCREMENT,"
						+ "  `address` varchar(128) NOT NULL,"
						+ "  `latitude` decimal(7,5) NOT NULL,"
						+ "  `longitude` decimal(7,5) NOT NULL,"
						+ "  `precision` enum('APPROXIMATE','GEOMETRIC_CENTER','RANGE_INTERPOLATED','ROOFTOP') NOT NULL,"
						+ "  PRIMARY KEY (`id`),"
						+ "  UNIQUE KEY `address` (`address`)"
						+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
				} else if (tableName.equals("classification")) {
					sql = "CREATE TABLE IF NOT EXISTS `classification` ("
						+ "  `agent_type_hash` varchar(32) NOT NULL,"
						+ "  `attributes` text NOT NULL,"
						+ "  `data` mediumtext NOT NULL,"
						+ "  `lmt` longblob NOT NULL,"
						+ "  PRIMARY KEY (`agent_type_hash`)"
						+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
				} else {
					throw new Exception("trying to create unknown table: "+tableName);
				}
				stm = connection.createStatement();
				stm.executeUpdate(sql);
			} catch (Exception e) {
				System.err.println("creating table `"+tableName+"` failed");
				e.printStackTrace();
			} finally {
			    if (stm != null) {
			        try {
			        	stm.close();
			        } catch (SQLException sqlEx) { }
			        stm = null;
			    }
			}
		}
	}
	
	private void createDatabase() {
		Statement stm = null;
		String databaseName = Settings.getProperty(Settings.DATABASE_NAME);
		try {
			String sql = "CREATE DATABASE IF NOT EXISTS `"+databaseName+"` DEFAULT CHARACTER SET utf8;";
			stm = connection.createStatement();
			stm.executeUpdate(sql);
        	stm.close();
			sql = "USE `"+databaseName+"`;";
			stm = connection.createStatement();
			stm.executeQuery(sql);
		} catch (Exception e) {
			System.err.println("creating database "+databaseName+" failed");
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
	}
}