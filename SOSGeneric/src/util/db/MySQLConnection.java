package util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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

	private final static String[] tables = new String[] {"agents", "properties"};
	private static MySQLConnection instance = null;

	public Connection connection = null;

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

	/**
	 * Constructs a new MySQLConnection object.
	 */
	private MySQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://"+Settings.getProperty(Settings.DATABASE_HOST)+"/"+Settings.getProperty(Settings.DATABASE_NAME)+
                                               "?user="+Settings.getProperty(Settings.DATABASE_USER)+"&password="+Settings.getProperty(Settings.DATABASE_PASSWORD));
            if (connection == null) {
            	throw new Exception("could not connect to database "+Settings.getProperty(Settings.DATABASE_HOST)+"!");
            }
            if (!checkTables(true)) {
            	throw new Exception("error checking and creating tables");
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
	
	/**
	 * Check if all required tables exist in the database, with the possibility to automatically create them. 
	 * 
	 * @param autoCreate true if automatic creation should happen
	 * @return true if all required tables are present, or have been created successfully
	 * @throws SQLException
	 */
	private boolean checkTables(boolean autoCreate) throws SQLException {
		boolean result = true;
		DatabaseMetaData metaData = connection.getMetaData();
		for (String tableName : tables) {
			boolean exists = tableExists(metaData, tableName);
			if (autoCreate && !exists) {
				exists = createTable(tableName);
			}
			result &= exists;
		}
		return result;
	}

	/**
	 * Check if a table exists.
	 * 
	 * @param metaData the Metadata of the database
	 * @param tableName the name of the table
	 * @return if the table exists in the database
	 * @throws SQLException
	 */
	private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
	    return connection.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"}).first();
	}
	
	/**
	 * Creates the predefined table. 
	 * 
	 * @param tableName the name of the table to create.
	 * @return success
	 */
	private boolean createTable(String tableName) {
		Statement stm = null;
		try {
			String sql = "";
			if (tableName.equals("agents")) {
				sql = "CREATE TABLE IF NOT EXISTS `agents` ("
					  +"`id` varchar(32) NOT NULL,"
					  +"`label` varchar(32) NOT NULL,"
					  +"`description` text NOT NULL,"
					  +"`status` enum('UNKNOWN','OK','WARNING','ERROR') NOT NULL,"
					  +"`hidden` tinyint(1) NOT NULL DEFAULT '0',"
					  +"`type` varchar(32) NOT NULL,"
					  +"PRIMARY KEY (`id`),"
					  +"KEY `label` (`label`),"
					  +"KEY `status` (`status`),"
					  +"KEY `type` (`type`)"
					  +") ENGINE=MyISAM DEFAULT CHARSET=latin1;";
			} else if (tableName.equals("properties")) {
				sql = "CREATE TABLE IF NOT EXISTS `properties` ("
					+"`agent_id` varchar(32) NOT NULL,"
					+"`type` enum('UNKNOWN','BOOLEAN','NUMBER','TEXT','TIME','TIMEWINDOW','LOCATION','STATUS','DEPENDENCIES','HISTORY') NOT NULL,"
					+"`name` varchar(32) NOT NULL,"
					+"`value` longtext NOT NULL,"
					+"PRIMARY KEY (`agent_id`,`name`),"
					+"KEY `type` (`type`)"
					+") ENGINE=MyISAM DEFAULT CHARSET=latin1;";
			} else {
				throw new Exception("trying to create unknown table: "+tableName);
			}
			stm = connection.createStatement();
			stm.executeUpdate(sql);
			System.out.println("created table `"+tableName+"`");
		} catch (Exception e) {
			System.err.println("creating table `"+tableName+"` failed");
			e.printStackTrace();
			return false;
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return true;
	}
}