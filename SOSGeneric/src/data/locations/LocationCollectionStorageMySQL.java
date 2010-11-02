package data.locations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Vector;

import model.agent.property.properties.LocationProperty;
import util.db.MySQLConnection;

public class LocationCollectionStorageMySQL extends LocationCollectionStorage {

	private MySQLConnection conn = null;

	/**
	 * Construct a new LocationCollectionStorageMySQL object.
	 */
	public LocationCollectionStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
	}

	@Override
	public LocationProperty getLocation(String address) {
		LocationProperty lp = null;
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "SELECT * FROM `locations` WHERE address = '"+address.replaceAll("'", "\\\\'")+"';";
			ResultSet result = stm.executeQuery(sql);
			if (result.first()) {
				lp = new LocationProperty("");
				lp.setAddress(address);
				lp.setLatitude(result.getDouble("latitude"));
				lp.setLongitude(result.getDouble("longitude"));
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
		return lp;
	}

	@Override
	public Collection<LocationProperty> getLocations() {
		Vector<LocationProperty> locations = new Vector<LocationProperty>();

		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "SELECT * FROM `locations`;";
			ResultSet result = stm.executeQuery(sql);
			while (result.next()) {
				LocationProperty lp = new LocationProperty("");
				lp.setAddress(result.getString("address"));
				lp.setLatitude(result.getDouble("latitude"));
				lp.setLongitude(result.getDouble("longitude"));
				locations.add(lp);
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
		
		return locations;
	}

	@Override
	public void putLocation(LocationProperty location) {
		Statement stm = null;
		try {
			stm = conn.getConnection().createStatement();
			String sql = "INSERT INTO `locations` (address,latitude,longitude) VALUES "
				+ "('"+location.getAddress().replaceAll("'", "\\\\'")
				+ "',"+location.getLatitude()+","+location.getLongitude()+");";
			stm.executeUpdate(sql);
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