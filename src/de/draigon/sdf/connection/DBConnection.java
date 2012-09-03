package de.draigon.sdf.connection;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Abstact class to define a DBConnection for the {@link ConnectionFactory}
 * 
 * @author Draigon Development
 * @version 1.0
 */
public abstract class DBConnection {

	/** is the connection in use, or pending */
    private boolean use = false;
    
    /**
     * sets this connection to be free again and notifies the connectionfactory
     */
    public synchronized void close() {
        this.use = false;
        ConnectionFactory.notifyFreeConnection(this);
    }

    /**
     * returns if a connection is in use
     * 
     * @return true if the connection is still in use
     */
    public synchronized boolean isInUse() {
        return this.use;
    }
    
    /**
     * determinates that this connection is in use
     */
    public void setInUse() {
        this.use = true;
    }
    
    /**
     * commits the transaction
     *
     * @throws  SQLException if an error occurs
     */
    public abstract void commitTransaction() throws SQLException;

    /**
     * returns the connection statement
     *
     * @return the statement to execute querys
     */
    public abstract Statement getStatement();

    /**
     * closes the connection to the database
     */
    public abstract void kill();

    /**
     * rolls back the last executions
     */
    public abstract void rollback();

    /**
     * starts a new transaction
     */
    public abstract void startTransaction();

    /**
     * returns the database meta data
     * @return
     */
    public abstract DatabaseMetaData getMetaData();
    
    /**
     * returns a list of mappings, an java Object is Mapped to the DB-types
     * 
     * @return a mapping list class to Datadype
     */
    public abstract Map<Class<?>, String> getDatatypeMappings();
}