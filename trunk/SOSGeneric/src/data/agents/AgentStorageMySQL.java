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
	
	private PreparedStatement getPropertyStatement = null;
	private PreparedStatement getPropertyValueStatement = null;
	private PreparedStatement getPropertiesStatement = null;
	private PreparedStatement getPropertiesKeySetStatement = null;
	private PreparedStatement putPropertyStatement = null;
	private PreparedStatement removePropertyStatement = null;
	private PreparedStatement deleteStatement = null;

	/**
	 * Construct a new AgentStorageMySQL object.
	 */
	public AgentStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
		try {
			getPropertyStatement = conn.getConnection().prepareStatement("SELECT `type`,`value` FROM `properties` WHERE `agent_id` = ? AND `name` = ? LIMIT 1;");
			getPropertyValueStatement = conn.getConnection().prepareStatement("SELECT `value` FROM `properties` WHERE `agent_id` = ? AND `name` = ? LIMIT 1;");
			getPropertiesStatement = conn.getConnection().prepareStatement("SELECT `name`,`value`,`type` FROM `properties` WHERE `agent_id` = ?;");
			getPropertiesKeySetStatement = conn.getConnection().prepareStatement("SELECT `name` FROM `properties` WHERE `agent_id` = ?;");
			putPropertyStatement = conn.getConnection().prepareStatement("INSERT INTO `properties` (`agent_id`,`type`,`name`,`value`) VALUES "
					+ "(?,?,?,?) ON DUPLICATE KEY UPDATE `value`=VALUES(`value`),`type`=VALUES(`type`);");
			removePropertyStatement = conn.getConnection().prepareStatement("DELETE FROM `properties` WHERE `agent_id` = ? AND `name` = ?;");
			deleteStatement = conn.getConnection().prepareStatement("DELETE FROM `properties` WHERE `agent_id` = ?;");
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\nfailed to prepare statements");
		}
	}
	
	@Override
	public synchronized Property getProperty(String id, String name) {
		Property p = null;
		try {
			getPropertyStatement.setString(1, id);
			getPropertyStatement.setString(2, name);
			ResultSet propertyResult = getPropertyStatement.executeQuery();
			if(propertyResult.next()) {
				p = Property.createProperty(PropertyType.valueOf(propertyResult.getString(1)), name, propertyResult.getString(2));
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		}
		return p;
	}
	
	@Override
	public synchronized String getPropertyValue(String id, String name){
		String value = "";
		try {
			getPropertyValueStatement.setString(1, id);
			getPropertyValueStatement.setString(2, name);
			ResultSet propertyResult = getPropertyValueStatement.executeQuery();
			if(propertyResult.first()) {
				value = propertyResult.getString("value");
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		}
		return value;		
	}
	
	@Override
	public synchronized HashMap<String, Property> getProperties(String id) {
		HashMap<String, Property> map = new HashMap<String, Property>();
		try {
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
		}
		return map;
	}
	
	@Override
	public synchronized Set<String> getPropertiesKeySet(String id) {
		Set<String> set = new HashSet<String>();
		try {
			getPropertiesKeySetStatement.setString(1, id);
			ResultSet propertyResult = getPropertiesKeySetStatement.executeQuery();
			while (propertyResult.next()) {
				set.add(propertyResult.getString("name"));
			}
			propertyResult.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
		}
		return set;
	}
	
	@Override
	public synchronized void putProperty(String id, Property p) {
		String value = p.toString();
		try {
			putPropertyStatement.setString(1, id);
			putPropertyStatement.setString(2, p.getPropertyType().toString());
			putPropertyStatement.setString(3, p.getName());
			putPropertyStatement.setString(4, value);
			putPropertyStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id; '"+id+"', type: '"+p.getPropertyType().toString()+"', name: '"+p.getName()+"', value: '"+value+"'");
		}
	}
	
	@Override
	public synchronized void putProperties(String id, HashMap<String, Property> properties) {
		if (properties.isEmpty()) return;
		String value = "", type = "", name = "";
		try {
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
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id; '"+id+"', type: '"+type+"', name: '"+name+"', value: '"+value+"'");
		} 
	}
	
	@Override
	public synchronized void removeProperty(String id, String name) {
		try {
			removePropertyStatement.setString(1, id);
			removePropertyStatement.setString(2, name);
			removePropertyStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"', name: '"+name+"'");
		}
	}
	
	@Override
	public synchronized boolean delete(String id) {
		try {
			deleteStatement.setString(1, id);
			deleteStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: id: '"+id+"'");
			return false;
		}
		return true;
	}
}