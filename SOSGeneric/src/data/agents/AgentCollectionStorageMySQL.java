package data.agents;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import model.agent.Agent;
import util.db.MySQLConnection;

/**
 * AgentCollectionStorageMySQL handles the storage of agents using a MySQL database.
 * 
 * @author W.H. Mook
 */
public class AgentCollectionStorageMySQL extends AgentCollectionStorage {
	
	private MySQLConnection conn = null;

	/**
	 * Construct a new AgentCollectionStorageMySQL object.
	 */
	public AgentCollectionStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
	}
	
	public boolean containsKey(String id) {
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
			String sql = "SELECT id FROM `agents` WHERE id = '"+id+"' LIMIT 1;";
			ResultSet result = stm.executeQuery(sql);
			return result.first();
		} catch (SQLException e) {
			System.err.println("checking agent "+id+" failed");
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return false;
	}
	
	public int getSize() {
		int idsNumber = 0;
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
			String sql = "SELECT COUNT(DISTINCT id) AS ids FROM `agents`;";
			ResultSet result = stm.executeQuery(sql);
			if (result.first()) {
				idsNumber = result.getInt("ids");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return idsNumber;
	}
	
	public List<String> getTypes() {
		Vector<String> types = new Vector<String>();
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
			// do not select hidden types
			String sql = "SELECT DISTINCT type FROM `agents` WHERE hidden != 1;";
			ResultSet result = stm.executeQuery(sql);
			while (result.next()) {
				types.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return types;
	}

	public List<String> getIDs() {
		Vector<String> ids = new Vector<String>();
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
			String sql = "SELECT DISTINCT id FROM `agents`;";
			ResultSet result = stm.executeQuery(sql);
			while (result.next()) {
				ids.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return ids;
	}

	@Override
	public void putAgent(Agent agent) {
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
			String agentSQL = "INSERT INTO `agents` (id,label,description,status,hidden,type) VALUES "
				+ "('"+agent.getID()+"','"+agent.get(Agent.LABEL)+"','"+agent.get(Agent.DESCRIPTION)+"','"+agent.get(Agent.STATUS)+"','"+(agent.get(Agent.HIDDEN) == Boolean.toString(true) ? 1 : 0)+"','"+agent.get(Agent.TYPE)+"') "
				+ "ON DUPLICATE KEY UPDATE label=VALUES(label),description=VALUES(description),status=VALUES(status),hidden=VALUES(hidden),type=VALUES(type);";
			stm.executeUpdate(agentSQL);
		} catch (SQLException e) {
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