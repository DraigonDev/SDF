package de.draigon.sdf.connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.draigon.sdf.exception.DBException;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class MySQLConnection implements DBConnection {

    private static String SQL_DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static String SQL_DRIVER_URL = "jdbc:mysql://{HOST}:{PORT}/{DB}";

    /** FIXME: Javadoc einfuegen */
    Connection connection;

    private boolean use = false;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von Connection.
     */
    public MySQLConnection() {
        this.createConnection();
    }

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#close()
     */
    public synchronized void close() {
        this.use = false;
        ConnectionFactory.notifyFreeConnection(this);
    }

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#commitTransaction()
     */
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

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#getStatement()
     */
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

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#kill()
     */
    public void kill() {

        try {
            this.connection.close();
        } catch (SQLException e) {
            // Close it
        }
    }

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#rollback()
     */
    public void rollback() {

        try {
            this.connection.rollback();
        } catch (SQLException e2) {
            throw new DBException("error rolling back transaction", e2);
        }
    }

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#setInUse()
     */
    public void setInUse() {
        this.use = true;
    }

    /* (non-Javadoc)
     * @see de.draigon.sdf.connection.DBConnection#startTransaction()
     */
    public void startTransaction() {

        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DBException("error starting transaction", e);
        }
    }

    @Override
    public String toString() {
        return "Connection{" + super.toString()
            .split("@")[1] + "}";
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von in use
     *
     * @return  Der Wert von in use
     */
    public synchronized boolean isInUse() {
        return this.use;
    }

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
        } catch (SQLException e) {
            throw new DBException("could not connect to database", e);
        }
    }
}
