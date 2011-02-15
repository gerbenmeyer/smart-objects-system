package data.agents;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import main.SOSServer;
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
		PreparedStatement getPropertyStatement = null;
		try {
			getPropertyStatement = conn.getConnection().prepareStatement("SELECT `type`,`value` FROM `properties` WHERE `agent_id` = ? AND `name` = ? LIMIT 1;");
			getPropertyStatement.setString(1, id);
			getPropertyStatement.setString(2, name);
			ResultSet propertyResult = getPropertyStatement.executeQuery();
			if(propertyResult.next()) {
				p = Property.createProperty(PropertyType.valueOf(propertyResult.getString(1)), name, propertyResult.getString(2));
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		} finally {
			try { if (getPropertyStatement != null) { getPropertyStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return p;
	}
	
	public String getPropertyValue(String id, String name){
		String value = "";
		PreparedStatement getPropertyValueStatement = null;
		try {
			getPropertyValueStatement = conn.getConnection().prepareStatement("SELECT `value` FROM `properties` WHERE `agent_id` = ? AND `name` = ? LIMIT 1;");
			getPropertyValueStatement.setString(1, id);
			getPropertyValueStatement.setString(2, name);
			ResultSet propertyResult = getPropertyValueStatement.executeQuery();
			if(propertyResult.first()) {
				value = propertyResult.getString("value");
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		} finally {
			try { if (getPropertyValueStatement != null) { getPropertyValueStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return value;		
	}
	
	public HashMap<String, Property> getProperties(String id) {
		HashMap<String, Property> map = new HashMap<String, Property>();
		PreparedStatement getPropertiesStatement = null;
		try {
			getPropertiesStatement = conn.getConnection().prepareStatement("SELECT `name`,`value`,`type` FROM `properties` WHERE `agent_id` = ?;");
			getPropertiesStatement.setString(1, id);
			ResultSet propertyResult = getPropertiesStatement.executeQuery();
			while (propertyResult.next()) {
				String propertyName = propertyResult.getString("name");
				PropertyType propertyType = PropertyType.valueOf(propertyResult.getString("type"));
				String propertyValue = propertyResult.getString("value");
				Property p = Property.createProperty(propertyType, propertyName, propertyValue);
				map.put(propertyName, p);
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
		} finally {
			try { if (getPropertiesStatement != null) { getPropertiesStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return map;
	}
	
	public Set<String> getPropertiesKeySet(String id) {
		Set<String> set = new HashSet<String>();
		PreparedStatement getPropertiesKeySetStatement = null;
		try {
			getPropertiesKeySetStatement = conn.getConnection().prepareStatement("SELECT `name` FROM `properties` WHERE `agent_id` = ?;");
			getPropertiesKeySetStatement.setString(1, id);
			ResultSet propertyResult = getPropertiesKeySetStatement.executeQuery();
			while (propertyResult.next()) {
				set.add(propertyResult.getString("name"));
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
		} finally {
			try { if (getPropertiesKeySetStatement != null) { getPropertiesKeySetStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return set;
	}
	
	
	public void putProperty(String id, Property p) {
		String value = p.toString();//.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
		PreparedStatement putPropertyStatement = null;
		try {
			putPropertyStatement = conn.getConnection().prepareStatement("INSERT INTO `properties` (`agent_id`,`type`,`name`,`value`) VALUES "
					+ "(?,?,?,?) ON DUPLICATE KEY UPDATE `value`=VALUES(`value`),`type`=VALUES(`type`);");
			putPropertyStatement.setString(1, id);
			putPropertyStatement.setString(2, p.getPropertyType().toString());
			putPropertyStatement.setString(3, p.getName());
			putPropertyStatement.setString(4, value);
			putPropertyStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id; '"+id+"', type: '"+p.getPropertyType().toString()+"', name: '"+p.getName()+"', value: '"+value+"'");
		} finally {
			try { if (putPropertyStatement != null) { putPropertyStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
	
	public void putProperties(String id, HashMap<String, Property> properties) {
		if (properties.isEmpty()) return;
		String value = "", type = "", name = "";
		PreparedStatement putPropertyStatement = null;
		boolean prevAutoCommit = true;
		try {
			prevAutoCommit = conn.getConnection().getAutoCommit();
			conn.getConnection().setAutoCommit(false);
			putPropertyStatement = conn.getConnection().prepareStatement("INSERT INTO `properties` (`agent_id`,`type`,`name`,`value`) VALUES "
					+ "(?,?,?,?) ON DUPLICATE KEY UPDATE `value`=VALUES(`value`),`type`=VALUES(`type`);");
			for (Property p : properties.values()) {
				value = p.toString();//.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'");
				type = p.getPropertyType().toString();
				name = p.getName();
				putPropertyStatement.setString(1, id);
				putPropertyStatement.setString(2, type);
				putPropertyStatement.setString(3, name);
				putPropertyStatement.setString(4, value);
				putPropertyStatement.executeUpdate();
			}
			conn.getConnection().commit();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id; '"+id+"', type: '"+type+"', name: '"+name+"', value: '"+value+"'");
		} finally {
			try { conn.getConnection().setAutoCommit(prevAutoCommit); if (putPropertyStatement != null) { putPropertyStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
	
	public void removeProperty(String id, String name) {
		PreparedStatement removePropertyStatement = null;
		try {
			removePropertyStatement = conn.getConnection().prepareStatement("DELETE FROM `properties` WHERE `agent_id` = ? AND `name` = ?;");
			removePropertyStatement.setString(1, id);
			removePropertyStatement.setString(2, name);
			removePropertyStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		} finally {
			try { if (removePropertyStatement != null) { removePropertyStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
	
	@Override
	public boolean delete(String id) {
		PreparedStatement deleteStatement = null;
		try {
			deleteStatement = conn.getConnection().prepareStatement("DELETE FROM `properties` WHERE `agent_id` = ?;");
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