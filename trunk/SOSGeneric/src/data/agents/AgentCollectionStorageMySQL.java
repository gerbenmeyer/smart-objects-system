package data.agents;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.agent.Agent;
import model.agent.property.Property;
import util.db.MySQLConnection;
import util.enums.AgentStatus;
import util.enums.PropertyType;

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

	@Override
	public boolean containsKey(String id) {
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
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

	@Override
	public Map<String, Property> get(String id) {
		List<String> ids = new Vector<String>();
		ids.add(id);
		List<Map<String, Property>> list = get(ids);
		if (list.isEmpty()) {
			return new HashMap<String, Property>();
		} 
		return get(ids).get(0);
	}

	@Override
	public List<Map<String, Property>> get(List<String> ids) {
		Vector<Map<String, Property>> agents = new Vector<Map<String, Property>>();
		
	    Iterator<String> iter = ids.iterator();
	    StringBuffer buffer = new StringBuffer("'"+iter.next()+"'");
	    while (iter.hasNext()) buffer.append(',').append("'"+iter.next()+"'");
		
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "SELECT * FROM `agents` WHERE id IN ("+buffer.toString()+") LIMIT "+ids.size()+";";
			ResultSet result = stm.executeQuery(sql);
			while(result.next()){
				Map<String, Property> properties = new HashMap<String, Property>();
				properties.put(Agent.ID,Property.createProperty(PropertyType.TEXT, Agent.ID, result.getString("id")));
				properties.put(Agent.LABEL,Property.createProperty(PropertyType.TEXT, Agent.LABEL, result.getString("label")));
				properties.put(Agent.DESCRIPTION,Property.createProperty(PropertyType.TEXT, Agent.DESCRIPTION, result.getString("description")));
				properties.put(Agent.STATUS,Property.createProperty(PropertyType.STATUS, Agent.STATUS, result.getString("status")));
				properties.put(Agent.HIDDEN,Property.createProperty(PropertyType.BOOLEAN, Agent.HIDDEN, result.getString("hidden")));
				properties.put(Agent.TYPE,Property.createProperty(PropertyType.TEXT, Agent.TYPE, result.getString("type")));
				properties.put(Agent.LOCATION,Property.createProperty(PropertyType.LOCATION, Agent.LOCATION, result.getString("location")));
				agents.add(properties);
			}
		} catch (SQLException e) {
			System.err.println("fetching agents failed");
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
	    
		return agents;
	}

	@Override
	public int getSize() {
		int idsNumber = 0;
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
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

	@Override
	public List<String> getTypes() {
		Vector<String> types = new Vector<String>();
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			// do not select hidden types
			String sql = "SELECT DISTINCT type FROM `agents` WHERE hidden = 'false';";
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

	@Override
	public List<String> getIDs() {
		Vector<String> ids = new Vector<String>();
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
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
			stm = conn.getConnection().createStatement();
			String label = agent.get(Agent.LABEL).replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
			String description = agent.get(Agent.DESCRIPTION).replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
			String location = agent.get(Agent.LOCATION).replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
			String agentSQL = "INSERT INTO `agents` (id,label,description,status,hidden,type,location) VALUES "
				+ "('"+agent.getID()+"','"+label+"','"+description+"','"+(agent.get(Agent.STATUS).isEmpty() ? AgentStatus.UNKNOWN.toString() : agent.get(Agent.STATUS))+"','"+(agent.get(Agent.HIDDEN).isEmpty() ? Boolean.toString(false) : agent.get(Agent.HIDDEN))+"','"+agent.get(Agent.TYPE)+"','"+location+"') "
				+ "ON DUPLICATE KEY UPDATE label=VALUES(label),description=VALUES(description),status=VALUES(status),hidden=VALUES(hidden),type=VALUES(type),location=VALUES(location);";
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
	
	public List<Map<String, Property>> searchAgents(String search) {
		String filters = "";
		String query = new String(search);
		//match type		
		Matcher m1 = Pattern.compile("type:([^\\s]*)").matcher(query);
		if (m1.find()) {
			filters += "AND type = '" + m1.group(1) + "' ";
			query = query.replaceAll(m1.group(), "");
		}
		//match status
		Matcher m2 = Pattern.compile("status:([^\\s]*)").matcher(query);
		if (m2.find()) {
			filters += "AND status = '" + m2.group(1) + "' ";
			query = query.replaceAll(m2.group(), "");
		}		
		//match other properties
		String sql = "SELECT * FROM `agents` "
			// exclude hidden
			+ "WHERE hidden = 'false' " + filters
			//TODO move limit to settings
			+ (!query.trim().isEmpty()?"AND label LIKE '%"+query.trim()+"%' OR description LIKE '%"+query.trim()+"%' ":"")+ "LIMIT 5001;";
		
		List<Map<String, Property>> agents = new Vector<Map<String, Property>>();
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			ResultSet result = stm.executeQuery(sql);
			while (result.next()) {
				HashMap<String, Property> properties = new HashMap<String, Property>();
				properties.put(Agent.ID,Property.createProperty(PropertyType.TEXT, Agent.ID, result.getString("id")));
				properties.put(Agent.LABEL,Property.createProperty(PropertyType.TEXT, Agent.LABEL, result.getString("label")));
				properties.put(Agent.DESCRIPTION,Property.createProperty(PropertyType.TEXT, Agent.DESCRIPTION, result.getString("description")));
				properties.put(Agent.STATUS,Property.createProperty(PropertyType.STATUS, Agent.STATUS, result.getString("status")));
				properties.put(Agent.HIDDEN,Property.createProperty(PropertyType.BOOLEAN, Agent.HIDDEN, result.getString("hidden")));
				properties.put(Agent.TYPE,Property.createProperty(PropertyType.TEXT, Agent.TYPE, result.getString("type")));
				properties.put(Agent.LOCATION,Property.createProperty(PropertyType.LOCATION, Agent.LOCATION, result.getString("location")));
				agents.add(properties);
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
		return agents;
	}
}