package data.locations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import main.SOSServer;
import model.agent.property.properties.LocationProperty;
import util.db.MySQLConnection;
import util.enums.GoogleLocationType;

public class LocationCollectionStorageMySQL extends LocationCollectionStorage {

	private MySQLConnection conn = null;
	private PreparedStatement getLocationsStatement = null;

	/**
	 * Construct a new LocationCollectionStorageMySQL object.
	 */
	public LocationCollectionStorageMySQL() {
		super();
		this.conn = MySQLConnection.getInstance();
		try {
			getLocationsStatement = conn.getConnection().prepareStatement("SELECT * FROM `locations`;");
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\nfailed to prepare statements");
		} finally {
			try {
				getLocationsStatement.clearParameters();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public LocationProperty getLocation(String address) {
		LocationProperty lp = null;
		String a = address;//.replaceAll("'", "\\\\'");
		PreparedStatement getLocationStatement = null;
		try {
			getLocationStatement = conn.getConnection().prepareStatement("SELECT * FROM `locations` WHERE `address` = ?;");
			getLocationStatement.setString(1, a);
			ResultSet result = getLocationStatement.executeQuery();
			if (result.first()) {
				lp = new LocationProperty("");
				lp.setAddress(address);
				lp.setLatitude(result.getDouble("latitude"));
				lp.setLongitude(result.getDouble("longitude"));
				lp.setLocationType(GoogleLocationType.valueOf(result.getString("precision")));
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: address: '"+a+"'");
		} finally {
			try { if (getLocationStatement != null) { getLocationStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
		return lp;
	}

	@Override
	public Collection<LocationProperty> getLocations() {
		Vector<LocationProperty> locations = new Vector<LocationProperty>();
		try {
			ResultSet result = getLocationsStatement.executeQuery();
			while (result.next()) {
				LocationProperty lp = new LocationProperty("");
				lp.setAddress(result.getString("address"));
				lp.setLatitude(result.getDouble("latitude"));
				lp.setLongitude(result.getDouble("longitude"));
				lp.setLocationType(GoogleLocationType.valueOf(result.getString("precision")));
				locations.add(lp);
			}
			result.close();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'");
		}
		return locations;
	}

	@Override
	public void putLocation(LocationProperty location) {
		String a = location.getAddress();//.replaceAll("'", "\\\\'");
		PreparedStatement putLocationStatement = null;
		try {
			putLocationStatement = conn.getConnection().prepareStatement("INSERT INTO `locations` (`address`,`latitude`,`longitude`,`precision`) VALUES "
					+ "(?,?,?,?);");
			putLocationStatement.setString(1, a);
			putLocationStatement.setDouble(2, location.getLatitude());
			putLocationStatement.setDouble(3, location.getLongitude());
			putLocationStatement.setString(4, location.getLocationType().toString());
			putLocationStatement.executeUpdate();
		} catch (SQLException e) {
			SOSServer.getDevLogger().severe("SQL exception: '"+e.toString()+"'\noriginal parameters: address: '"+a+"', latitude: '"+location.getLatitude()+"', longitude: '"+location.getLongitude()+"', precision: '"+location.getLocationType().toString()+"'");
		} finally {
			try { if (putLocationStatement != null) { putLocationStatement.close(); } } catch (SQLException e) { SOSServer.getDevLogger().warning("SQL exception: '"+e.toString()+"'"); }
		}
	}
}