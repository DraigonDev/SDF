package de.draigon.sdf.objects.load;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Delegator,covering the {@link java.sql.ResultSet} to fetch Objects and not
 * native datatypes
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class ResultSetWrapper {
	ResultSet result;

	public ResultSetWrapper(ResultSet result) {
		this.result = result;
	}
	
	/**
	 * Fetches a value from the resultset as {@link String}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public String getString(String column) throws SQLException{
		return result.getString(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Boolean}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Boolean getBoolean(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getBoolean(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Integer}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Integer getInteger(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getInt(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Double}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Double getDouble(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getDouble(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Float}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Float getFloat(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getFloat(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Date}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Date getDate(String column) throws SQLException{
		return result.getDate(column);
	}
	
	/**
	 * Fetches a value from the resultset as {@link Long}
	 * 
	 * @param column the key to the value
	 * @return the value, null if the database value is null
	 * @throws SQLException is an error occurs
	 */
	public Long getLong(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getLong(column);
	}
	
	/**
	 * Delegates to {@link ResultSet#next()}
	 */
	public boolean next() throws SQLException {
		return result.next();
	}
	
	/**
	 * Delegates to {@link ResultSet#close()}
	 */
	public void close() throws SQLException{
		result.close();
	}
}
