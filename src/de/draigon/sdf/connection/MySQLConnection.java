package de.draigon.sdf.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.draigon.sdf.exception.DBException;


/**
 * implementation of {@link DBConnection} using com.mysql.jdbc on mysql
 *
 * @author Draigon Development
 * @version 1.0
 */
public class MySQLConnection extends DBConnection {

    private static String SQL_DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static String SQL_DRIVER_URL = "jdbc:mysql://{HOST}:{PORT}/{DB}";

    /** the connection */
    Connection connection;

    /**
     * creates a new instance
     */
    public MySQLConnection() {
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
     * creates a new connection of com.mysql.jdbc
     */
    private void createConnection() {

        try {
            ConnectionProperties properties = ConnectionProperties.get();

            Class.forName(SQL_DRIVER_NAME);

            // Create a connection to the database
            String url = SQL_DRIVER_URL.replace("{HOST}", properties.getHost())
                .replace("{PORT}", properties.getPort())
                .replace("{DB}", properties.getDatabase());


            connection = DriverManager.getConnection(url, properties.getUsername(),
                properties.getPassword());
        } catch (ClassNotFoundException e) {
            throw new DBException("could not connect to database", e);
        }catch (IOException e) {
            throw new DBException("could not connect to database", e);
        } catch (SQLException e) {
            throw new DBException("could not connect to database", e);
        }
    }
}
