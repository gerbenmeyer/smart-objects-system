package util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import main.Settings;

public class MySQLConnection {

	private final static String[] tables = new String[] {"agents", "properties"};
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
            	throw new Exception("could not connect to database "+Settings.getProperty(Settings.DATABASE_HOST)+"!");
            }
            if (!checkTables(true)) {
            	throw new Exception("error checking and creating tables");
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
	
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

	private boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
	    return connection.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"}).first();
	}
	
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