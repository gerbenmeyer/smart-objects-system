package data.agents;

import java.sql.PreparedStatement;
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

import main.SOSServer;
import model.agent.Agent;
import model.agent.agents.SearchAgent;
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
	
	PreparedStatement getSizeStatement = null;
	PreparedStatement getTypesStatement = null;
	PreparedStatement getIDsStatement = null;

	/**
	 * Construct a new AgentCollectionStorageMySQL object.
	 */
	public AgentCollectionStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
		try {
			getSizeStatement = conn.getConnection().prepareStatement("SELECT COUNT(DISTINCT `id`) AS `ids` FROM `agents`;");
			getTypesStatement = conn.getConnection().prepareStatement("SELECT DISTINCT `type` FROM `agents` WHERE hidden = 'false';");
			getIDsStatement = conn.getConnection().prepareStatement("SELECT DISTINCT `id` FROM `agents`;");
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\nfailed to prepare statements");
		}
	}

	@Override
	public boolean containsKey(String id) {
		boolean res = false;
		PreparedStatement containsKeyStatement = null;
		try {
			containsKeyStatement = conn.getConnection().prepareStatement("SELECT `id` FROM `agents` WHERE `id` = ? LIMIT 1;");
			containsKeyStatement.setString(1, id);
			ResultSet result = containsKeyStatement.executeQuery();
			res = result.first();
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
		} finally {
			try { if (containsKeyStatement != null) { containsKeyStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return res;
	}

	@Override
	public Map<String, Property> get(String id) {
		Map<String, Property> map = new HashMap<String, Property>();
		PreparedStatement getSingleStatement = null;
		try {
			getSingleStatement = conn.getConnection().prepareStatement("SELECT * FROM `agents` WHERE `id` = ? LIMIT 1;");
			getSingleStatement.setString(1, id);
			ResultSet result = getSingleStatement.executeQuery();
			if (result.first()) {
				map = processAgentResult(result);
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
		} finally {
			try { if (getSingleStatement != null) { getSingleStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return map;
	}

	@Override
	public List<Map<String, Property>> get(List<String> ids) {
		Vector<Map<String, Property>> agents = new Vector<Map<String, Property>>();
		
	    Iterator<String> iter = ids.iterator();
	    StringBuffer buffer = new StringBuffer("'"+iter.next()+"'");
	    while (iter.hasNext()) { 
	    	buffer.append(',').append("'"+iter.next()+"'");
	    }
		
		Statement stm = null;
		String sql = "SELECT * FROM `agents` WHERE `id` IN ("+buffer.toString()+") LIMIT "+ids.size()+";";
		try {
			stm = conn.getConnection().createStatement();
			ResultSet result = stm.executeQuery(sql);
			while(result.next()){
				agents.add(processAgentResult(result));
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal SQL: '"+sql+"'");
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
	
	private Map<String, Property> processAgentResult(ResultSet result) throws SQLException {
		Map<String, Property> properties = new HashMap<String, Property>();
		Property id = Property.createProperty(PropertyType.TEXT, Agent.ID, result.getString("id"));
		if (id != null){
			properties.put(Agent.ID,id);
		}
		Property label = Property.createProperty(PropertyType.TEXT, Agent.LABEL, result.getString("label"));
		if (label != null){
			properties.put(Agent.LABEL,label);
		}
		Property description = Property.createProperty(PropertyType.TEXT, Agent.DESCRIPTION, result.getString("description"));
		if (description != null){
			properties.put(Agent.DESCRIPTION, description);
		}
		Property status = Property.createProperty(PropertyType.STATUS, Agent.STATUS, result.getString("status"));
		if (status != null){
			properties.put(Agent.STATUS,status);
		}
		Property hidden = Property.createProperty(PropertyType.BOOLEAN, Agent.HIDDEN, result.getString("hidden"));
		if (hidden != null){
			properties.put(Agent.HIDDEN,hidden);
		}
		Property type = Property.createProperty(PropertyType.TEXT, Agent.TYPE, result.getString("type"));
		if (type != null){
			properties.put(Agent.TYPE,type);
		}
		Property location = Property.createProperty(PropertyType.LOCATION, Agent.LOCATION, result.getString("location"));
		if (location != null){
			properties.put(Agent.LOCATION,location);
		}
		return properties;
	}

	@Override
	public int getSize() {
		int count = 0;
		try {
			ResultSet result = getSizeStatement.executeQuery();
			if (result.first()) {
				count = result.getInt("ids");
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'");
		}
		return count;
	}

	@Override
	public List<String> getTypes() {
		Vector<String> types = new Vector<String>();
		try {
			ResultSet result = getTypesStatement.executeQuery();
			while (result.next()) {
				try{
					types.add(result.getString("type"));
				} catch (NullPointerException e){
					SOSServer.getDevLogger().severe("Null pointer exception: '"+e.toString()+"'");
				}
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'");
		}
		return types;
	}

	@Override
	public List<String> getIDs() {
		Vector<String> ids = new Vector<String>();
		try {
			ResultSet result = getIDsStatement.executeQuery();
			while (result.next()) {
				try{
					ids.add(result.getString("id"));
				} catch (NullPointerException e){
					SOSServer.getDevLogger().severe("Null pointer exception: '"+e.toString()+"'");
				}
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'");
		}
		return ids;
	}

	@Override
	public void putAgent(Agent agent) {
		String label = agent.get(Agent.LABEL);//.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
		String description = agent.get(Agent.DESCRIPTION);//.replaceAll("\\\\'", "'").replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
		String location = agent.get(Agent.LOCATION);//.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
		PreparedStatement putAgentStatement = null;
		try {
			putAgentStatement = conn.getConnection().prepareStatement("INSERT INTO `agents` (`id`,`label`,`description`,`status`,`hidden`,`type`,`location`) VALUES "
					+ "(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
					+ "`label`=VALUES(`label`),`description`=VALUES(`description`),`status`=VALUES(`status`),`hidden`=VALUES(`hidden`),`type`=VALUES(`type`),`location`=VALUES(`location`);");
			putAgentStatement.setString(1, agent.getID());
			putAgentStatement.setString(2, label);
			putAgentStatement.setString(3, description);
			putAgentStatement.setString(4, (agent.get(Agent.STATUS).isEmpty() ? AgentStatus.UNKNOWN.toString() : agent.get(Agent.STATUS)));
			putAgentStatement.setString(5, (agent.get(Agent.HIDDEN).isEmpty() ? Boolean.toString(false) : agent.get(Agent.HIDDEN)));
			putAgentStatement.setString(6, agent.get(Agent.TYPE));
			putAgentStatement.setString(7, location);
			putAgentStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+agent.getID()+"', label; '"+label+"', description: '"+description+"', status: '"+(agent.get(Agent.STATUS).isEmpty() ? AgentStatus.UNKNOWN.toString() : agent.get(Agent.STATUS))+"', hidden: '"+(agent.get(Agent.HIDDEN).isEmpty() ? Boolean.toString(false) : agent.get(Agent.HIDDEN))+"', type: '"+agent.get(Agent.TYPE)+"', location: '"+location+"'");
		} finally {
			try { if (putAgentStatement != null) { putAgentStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
	
	public List<Map<String, Property>> searchAgents(String search) {
		return searchAgents(search, "ORDER BY id", SearchAgent.MAX_AGENTS);
	}
	
	public List<Map<String, Property>> searchAgents(String search, String sort, int limit) {
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
			+ (!query.trim().isEmpty()?"AND label LIKE '%"+query.trim()+"%' OR description LIKE '%"+query.trim()+"%' ":"")+ " "+sort+" LIMIT "+limit+";";
		
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
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal SQL: '"+sql+"'");
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
	public boolean delete(String id) {
		PreparedStatement deleteStatement = null;
		try {
			deleteStatement = conn.getConnection().prepareStatement("DELETE FROM `agents` WHERE `id` = ?;");
			deleteStatement.setString(1, id);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
			return false;
		} finally {
			try { if (deleteStatement != null) { deleteStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return true;
	}
}