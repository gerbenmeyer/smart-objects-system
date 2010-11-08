package data.classification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		Classifier classifier = null;
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "SELECT * FROM `classification` WHERE agent_type_hash = '"+key+"';";
			ResultSet result = stm.executeQuery(sql);
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
					classifier = new Classifier(agentType, arffAttributes, arffData, lmt);
			    } else {
					classifier = new Classifier(agentType, arffAttributes, arffData);
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
		    if (stm != null) {
		        try {
		        	stm.close();
		        } catch (SQLException sqlEx) { }
		        stm = null;
		    }
		}
		return classifier;
	}

	@Override
	public void put(Classifier classifier) {
		PreparedStatement pstm = null;
		String key = classifier.getAgentType() + "_" + Integer.toString(classifier.getArffAttributes().hashCode());
		try {
			pstm = conn.getConnection().prepareStatement("INSERT INTO `classification` (agent_type_hash,attributes,data,lmt) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE data=VALUES(data),lmt=VALUES(lmt);");
			pstm.setString(1, key);
			pstm.setString(2, classifier.getArffAttributes());
			pstm.setString(3, classifier.getArffData());
			pstm.setObject(4, classifier.getDecisionAlgorithm());
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    if (pstm != null) {
		        try {
		        	pstm.close();
		        } catch (SQLException sqlEx) { }
		        pstm = null;
		    }
		}
	}
}