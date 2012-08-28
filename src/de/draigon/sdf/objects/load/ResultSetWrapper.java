package de.draigon.sdf.objects.load;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Delegator,covering the {@link java.sql.ResultSet} to get Objects and not native datatypes
 * 
 * @author   Draigon Development
 * @version  1.0
 */
public class ResultSetWrapper {
	ResultSet result;

	public ResultSetWrapper(ResultSet result) {
		this.result = result;
	}
	
	public String getString(String column) throws SQLException{
		return result.getString(column);
	}
	
	public Boolean getBoolean(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getBoolean(column);
	}
	
	public Integer getInteger(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getInt(column);
	}
	
	public Double getDouble(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getDouble(column);
	}
	
	public Float getFloat(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getFloat(column);
	}
	
	public Date getDate(String column) throws SQLException{
		return result.getDate(column);
	}
	
	public Integer getLong(String column) throws SQLException{
		String value = result.getString(column);
		if(value == null){
			return null;
		}
		return result.getInt(column);
	}
	
	public boolean next() throws SQLException {
		return result.next();
	}
	
	public void close() throws SQLException{
		result.close();
	}
}
