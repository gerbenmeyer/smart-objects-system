package data.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.agent.AgentViewable;
import util.db.MySQLConnection;

public class AgentIndexMySQL extends AgentIndex {

	private MySQLConnection conn = null;

	public AgentIndexMySQL() {
		conn = MySQLConnection.getInstance();
	}
	
	@Override
	public Vector<String> searchAgents(String search) {
		
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
		String sql = "SELECT DISTINCT id FROM `agents` "
			// exclude hidden
			+ "WHERE hidden != 1 " + filters
			//TODO move limit to settings
			+ (!query.trim().isEmpty()?"AND label LIKE '%"+query.trim()+"%' OR description LIKE '%"+query.trim()+"%' ":"")+ "LIMIT 5001;";
		
		Vector<String> ids = new Vector<String>();
		Statement stm = null;
		try {
			stm = conn.connection.createStatement();
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
	
//	@Override
//	public Vector<String> searchAgents(String search) {
//		
//		String filters = "";
//		String query = new String(search);
//		//match type		
//		Matcher m1 = Pattern.compile("type:([^\\s]*)").matcher(query);
//		if (m1.find()) {
//			filters += "agent_id IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'TEXT' and name = 'Type' and value = '" + m1.group(1) + "') AND ";
//			query = query.replaceAll(m1.group(), "");
//		}
//		//match status
//		Matcher m2 = Pattern.compile("status:([^\\s]*)").matcher(query);
//		if (m2.find()) {
//			filters += "agent_id IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'STATUS' and name = 'Status' and value = '" + m2.group(1) + "') AND ";
//			query = query.replaceAll(m2.group(), "");
//		}		
//		//match other properties
//		String sql = "SELECT DISTINCT agent_id FROM properties "
//			// exclude hidden
//			+ "WHERE agent_id NOT IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'BOOLEAN' and name = 'Hidden' and value = 'true') AND " + filters
//			// exclude some more propteries
//			+ "type = 'TEXT' "
//			//TODO move limit to settings
//			+ (!query.trim().isEmpty()?"AND value LIKE '%"+query.trim()+"%' ":"")+ "LIMIT 5001;";
//		
//		Vector<String> ids = new Vector<String>();
//		Statement stm = null;
//		try {
//			stm = conn.connection.createStatement();
//			ResultSet result = stm.executeQuery(sql);
//			while (result.next()) {
//				ids.add(result.getString(1));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//		    if (stm != null) {
//		        try {
//		        	stm.close();
//		        } catch (SQLException sqlEx) { }
//		        stm = null;
//		    }
//		}
//		return ids;
//	}

	@Override
	public Set<String> getKeywords() {
		// TODO implement or remove
		return Collections.emptySet();
	}

	@Override
	public void update(AgentViewable agent) {
		
	}

}