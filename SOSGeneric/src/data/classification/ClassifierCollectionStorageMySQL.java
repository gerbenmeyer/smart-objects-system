package data.classification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.SOSServer;
import model.agent.classification.Classifier;
import util.db.MySQLConnection;
import weka.classifiers.trees.LMT;

public class ClassifierCollectionStorageMySQL extends ClassifierCollectionStorage {
	
	private MySQLConnection conn = null;

	/**
	 * Construct a new ClassifierCollectionStorageMySQL object.
	 */
	public ClassifierCollectionStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
	}

	@Override
	public Classifier get(String key) {
		Classifier clas = null;
		PreparedStatement getStatement = null;
		try {
			getStatement = conn.getConnection().prepareStatement("SELECT * FROM `classification` WHERE `agent_type_hash` = ?;");
			getStatement.setString(1, key);
			ResultSet result = getStatement.executeQuery();
			if (result.first()) {
				String agentType = result.getString("agent_type_hash");
				agentType = agentType.substring(0, agentType.lastIndexOf("_"));
				String arffAttributes = result.getString("attributes");
				String arffData = result.getString("data");
				LMT lmt = null;
				byte[] buf = result.getBytes("lmt");
			    if (buf != null) {
			    	ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			    	lmt = (LMT) objectIn.readObject();
					clas =  new Classifier(agentType, arffAttributes, arffData, lmt);
			    } else {
			    	clas =  new Classifier(agentType, arffAttributes, arffData);
			    }
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: key: '"+key+"'");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try { if (getStatement != null) { getStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return clas;
	}

	@Override
	public void put(Classifier classifier) {
		String key = classifier.getAgentType() + "_" + Integer.toString(classifier.getArffAttributes().hashCode());
		PreparedStatement putStatement = null;
		try {
			putStatement = conn.getConnection().prepareStatement("INSERT INTO `classification` (`agent_type_hash`,`attributes`,`data`,`lmt`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `data`=VALUES(`data`),`lmt`=VALUES(`lmt`);");
			putStatement.setString(1, key);
			putStatement.setString(2, classifier.getArffAttributes());
			putStatement.setString(3, classifier.getArffData());
			putStatement.setObject(4, classifier.getDecisionAlgorithm());
			putStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: key: '"+key+"', attributes: '"+classifier.getArffAttributes()+"', data: '"+classifier.getArffData()+"', lmt: '"+classifier.getDecisionAlgorithm()+"'");
		} finally {
			try { if (putStatement != null) { putStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
}