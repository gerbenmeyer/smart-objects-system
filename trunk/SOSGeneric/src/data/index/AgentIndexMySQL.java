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
		// TODO only 'type' and 'all' can be used as filter at the moment
		// 'status' is missing
		if (search.trim().equals("all")) {
			query = "";
		} else {
			Matcher m = Pattern.compile("type:([^\\s]*)").matcher(query);
			if (m.find()) {
				filters = "agent_id IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'TEXT' and name = 'Type' and value = '" + m.group(1) + "') AND ";
				query = query.replaceAll(m.group(), "");
			}
		}
		String sql = "SELECT DISTINCT agent_id FROM properties "
			// exclude hidden
			+ "WHERE agent_id NOT IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'BOOLEAN' and name = 'Hidden' and value = 'true') AND " + filters
			// exclude some more propteries
			+ "type = 'TEXT' "
			//TODO move limit to settings
			+ (!query.trim().isEmpty()?"AND value LIKE '%"+query.trim()+"%' ":"")+ "LIMIT 10001;";
		
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

	@Override
	public Set<String> getKeywords() {
		// TODO implement or remove
		return Collections.emptySet();
	}

//	@Override
//	public Vector<String> getAgentIDs() {
//		Vector<String> ids = new Vector<String>();
//		Statement stm = null;
//		try {
//			stm = conn.connection.createStatement();
//			String sql = "SELECT DISTINCT agent_id FROM properties;";
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
//
//	@Override
//	public Vector<String> getAgentTypes() {
//		Vector<String> types = new Vector<String>();
//		Statement stm = null;
//		try {
//			stm = conn.connection.createStatement();
//			String sql = "SELECT DISTINCT value FROM properties WHERE agent_id NOT IN (SELECT DISTINCT agent_id FROM `properties` WHERE type = 'BOOLEAN' and name = 'Hidden' and value = 'true') AND name = 'Type';";
//			ResultSet result = stm.executeQuery(sql);
//			while (result.next()) {
//				types.add(result.getString(1));
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
//		return types;
//	}

	@Override
	public void update(AgentViewable agent) {
		
	}
}