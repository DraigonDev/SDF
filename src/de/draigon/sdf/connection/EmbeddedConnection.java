package de.draigon.sdf.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.util.Loggin;


/**
 * implementation of {@link DBConnection} using org.apache.derby
 *
 * @author Draigon Development
 * @version 1.0
 */
public class EmbeddedConnection extends DBConnection {

    private static final String SHUTDOWN_URL = "jdbc:derby:;shutdown=true";
    private static final String DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DRIVER_URL = "jdbc:derby:{DB};create=true";

    /** the connection */
    Connection connection;


    /**
     * creates a new instance.
     */
    public EmbeddedConnection() {
        this.createConnection();
    }

 

    /**
     * {@inheritDoc}
     */
    @Override
    public void commitTransaction() throws SQLException {

        try {
            this.connection.commit();
        } catch (SQLException e) {

            try {
                this.connection.rollback();
            } catch (SQLException e2) {
                throw new DBException("error rolling back transaction", e2);
            }

            throw new SQLException("could not commit transaction - a rollback is performed");
        }

        try {
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DBException("error stopping transaction", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement getStatement() {
        
        if (connection == null) {
            throw new DBException("database connection lost.");
        }

        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new DBException("unable to create statement for database", e);
        }

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseMetaData getMetaData(){
        if (connection == null) {
            throw new DBException("database connection lost.");
        }

        try {
            return this.connection.getMetaData();
        } catch (SQLException e) {
            throw new DBException("unable to create MetaData for database", e);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void kill() {

        try {
            this.connection.close();
        } catch (SQLException e) {
            // Close it
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() {

        try {
            this.connection.rollback();
        } catch (SQLException e2) {
            throw new DBException("error rolling back transaction", e2);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startTransaction() {

        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DBException("error starting transaction", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Connection{" + super.toString()
            .split("@")[1] + "}";
    }

    /**
     * creates a new connection of org.apache.derby
     */
    private void createConnection() {

        try {
            ConnectionProperties properties = new ConnectionProperties();

            Class.forName(DRIVER_NAME);

            // Create a connection to the database
            String url = DRIVER_URL.replace("{DB}", properties.getDatabase());


            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            throw new DBException("could not connect to database", e);
        } catch (IOException e) {
            throw new DBException("could not connect to database", e);
        } catch (SQLException e) {
            throw new DBException("could not connect to database", e);
		}
    }

    /**
     * shuts down org.apache.derby
     */
    public static void shutdown() {
        boolean gotSQLExc = false;
        try {
            try{
            Class.forName(DRIVER_NAME);
            }catch (Exception e) {
                //Nothing
            }
            DriverManager.getConnection(SHUTDOWN_URL);
        } catch (SQLException se)  { 
           if ( se.getSQLState().equals("XJ015") ) {     
              gotSQLExc = true;
           }
        }
        if (!gotSQLExc) {
           Loggin.logConnectionFactory("embedded database did not shut down normally");
        }  else  {
           Loggin.logConnectionFactory("embedded database shut down normally");    
        }  
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public Map<Class<?>, String> getDatatypeMappings() {
		Map<Class<?>, String> mappings = new HashMap<Class<?>, String>();
		
		mappings.put(String.class, "VARCHAR");
		mappings.put(Long.class, "BIGINT");
		mappings.put(Integer.class, "INTEGER");
		mappings.put(Boolean.class, "BOOLEAN");
		mappings.put(Date.class, "TIMESTAMP");
		mappings.put(Float.class, "DOUBLE");
		mappings.put(Double.class, "DOUBLE");
		mappings.put(de.draigon.sdf.objects.Enum.class, "INTEGER");
		
		return mappings;
	}
}
