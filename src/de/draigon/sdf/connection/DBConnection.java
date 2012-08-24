package de.draigon.sdf.connection;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public interface DBConnection {

    /**
     * FIXME: Javadoc einfuegen
     */
    public abstract void close();

    /**
     * FIXME: Javadoc einfuegen
     *
     * @throws  SQLException
     */
    public abstract void commitTransaction() throws SQLException;

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   query
     *
     * @return
     */
    public abstract Statement getStatement();

    /**
     * FIXME: Javadoc einfuegen
     */
    public abstract void kill();

    /**
     * FIXME: Javadoc einfuegen
     */
    public abstract void rollback();

    /**
     * FIXME: Javadoc kontrollieren Setzt den neuen Wert von in use
     */
    public abstract void setInUse();

    /**
     * FIXME: Javadoc einfuegen
     */
    public abstract void startTransaction();

    public abstract boolean isInUse();

    public abstract DatabaseMetaData getMetaData();
}