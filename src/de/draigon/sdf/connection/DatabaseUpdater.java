package de.draigon.sdf.connection;

import java.sql.SQLException;
import java.sql.Statement;

import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.util.Loggin;


/**
 * Manages the acess for updates/inserts/deletes to database.
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class DatabaseUpdater {

    /** The connection for executions */
    private DBConnection connection;

    /**
     * creates a new databaseUpdater and requests a connection from the {@link ConnectionFactory}.
     * Note that till commiting the Updater this connection will be blocked.
     */
    public DatabaseUpdater() {
        connection = ConnectionFactory.getConnection();
        connection.startTransaction();
    }

    /**
     * Committes the updates and clears the connection
     *
     * @throws  SQLException
     */
    public void commit() throws SQLException {

        try {
            connection.commitTransaction();
        } catch (SQLException e) {
            throw e;
        } finally {
            connection.close();
            connection = null;
        }
    }

    /**
     * Executes a query on the DB-Connection and returns the result.
     *
     * @param   query the query to execute
     * 
     * @return true if the update was sucessful, false if an error occured.
     */
    public boolean execute(String query) {
        Loggin.logQuery(connection, query);
        if(connection == null){
        	throw new DBException("updater is only usable for one transaction. after committing create a new updater.");
        }

        try {
            Statement statement = connection.getStatement();
            int result = statement.executeUpdate(query);

            statement.close();

            return Statement.EXECUTE_FAILED != result;
        } catch (SQLException e) {
            throw new DBException("error executing query: '" + query + "'", e);
        }
    }

    /**
     * rolls back all executions done on this updater and clears the connection.
     */
    public void rollback() {
        connection.rollback();
        connection.close();
        connection = null;
    }
}
