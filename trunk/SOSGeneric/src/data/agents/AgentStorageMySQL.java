package data.agents;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import model.agent.property.Property;
import util.db.MySQLConnection;
import util.enums.PropertyType;

/**
 * AgentStorageMySQL handles the storage of agent properties using a MySQL database.
 * 
 * @author W.H. Mook
 */
public class AgentStorageMySQL extends AgentStorage {
	
	private MySQLConnection conn = null;

	/**
	 * Construct a new AgentStorageMySQL object.
	 */
	public AgentStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
	}
	
	public Property getProperty(String id, String name) {
		Property p = null;
		Statement stm = null;
		ResultSet propertyResult = null;
		try {
			stm = conn.getConnection().createStatement();
			String propertySQL = "SELECT type,value FROM properties WHERE agent_id = '"+id+"' AND name = '"+name+"' LIMIT 1;";
			propertyResult = stm.executeQuery(propertySQL);
			if(propertyResult.first()) {
				p = Property.createProperty(PropertyType.valueOf(propertyResult.getString("type")), name, propertyResult.getString("value"));
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
		return p;
	}
	
	public String getPropertyValue(String id, String name){
		Statement stm = null;
		ResultSet propertyResult = null;
		try {
			stm = conn.getConnection().createStatement();
			String propertySQL = "SELECT value FROM properties WHERE agent_id = '"+id+"' AND name = '"+name+"' LIMIT 1;";
			propertyResult = stm.executeQuery(propertySQL);
			if(propertyResult.first()) {
				return propertyResult.getString("value");
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
		return "";		
	}
	
	public HashMap<String, Property> getProperties(String id) {
		HashMap<String, Property> map = new HashMap<String, Property>();
		Statement stm = null;
		ResultSet propertyResult = null;
		try {
			stm = conn.getConnection().createStatement();
			String propertySQL = "SELECT name,value,type FROM properties WHERE agent_id = '"+id+"';";
			propertyResult = stm.executeQuery(propertySQL);
			while (propertyResult.next()) {
				String propertyName = propertyResult.getString("name");
				PropertyType propertyType = PropertyType.valueOf(propertyResult.getString("type"));
				String propertyValue = propertyResult.getString("value");
				Property p = Property.createProperty(propertyType, propertyName, propertyValue);
				map.put(propertyName, p);
//				AgentViewable av = AgentCollection.getInstance().get(id);
//				if (av != null) {
//					p.setAgentView(av);
//				}
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
		return map;
	}
	
	public Set<String> getPropertiesKeySet(String id) {
		Set<String> set = new HashSet<String>();
		Statement stm = null;
		ResultSet propertyResult = null;
		try {
			stm = conn.getConnection().createStatement();
			String propertySQL = "SELECT name FROM properties WHERE agent_id = '"+id+"';";
			propertyResult = stm.executeQuery(propertySQL);
			while (propertyResult.next()) {
				set.add(propertyResult.getString("name"));
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
		return set;
	}
	
	
	public void putProperty(String id, Property p) {
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String value = p.toString().replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
			String propertySQL = "INSERT INTO properties (agent_id,type,name,value) VALUES "
				+ "('"+id+"','"+p.getPropertyType().toString()+"','"+p.getName()+"','"+value+"') "
				+ "ON DUPLICATE KEY UPDATE value=VALUES(value),type=VALUES(type);";
			stm.executeUpdate(propertySQL);
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
	
	public void putProperties(String id, HashMap<String, Property> properties) {
		try {
			insertOrUpdateProperties(id, properties);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeProperty(String id, String name) {
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "DELETE FROM properties WHERE agent_id = '"+id+"' AND name = '"+name+"';";
			stm.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.println("removal of property "+name+" failed for agent "+id);
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
	
	/**
	 * Inserts or updates the properties of an Agent.
	 * 
	 * @param id the identifier of the agent
	 * @param properties the properties to be inserted or updated
	 * @throws SQLException
	 */
	private void insertOrUpdateProperties(String id, HashMap<String, Property> properties) throws SQLException {
		if (properties.isEmpty()) return;
		Statement stm = conn.getConnection().createStatement();
		StringBuffer propertySQL = new StringBuffer("INSERT INTO properties (agent_id,type,name,value) VALUES ");
		for (Property p : properties.values()) {
			String value = p.toString().replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
			propertySQL.append("('"+id+"','"+p.getPropertyType().toString()+"','"+p.getName()+"','"+value+"'),");
		}
		propertySQL.deleteCharAt(propertySQL.length()-1);
		propertySQL.append(" ON DUPLICATE KEY UPDATE value=VALUES(value);");
		try{
			stm.executeUpdate(propertySQL.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
		stm.close();
	}

	@Override
	public boolean delete(String id) {
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "DELETE FROM properties WHERE agent_id = '"+id+"';";
			stm.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.println("removal of agent "+id+" failed");
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