package de.draigon.sdf.connection;

import java.sql.SQLException;
import java.sql.Statement;

import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.util.Loggin;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class DatabaseUpdater {

    /** FIXME: Javadoc einfuegen */
    DBConnection connection;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von DatabaseUpdater.
     */
    public DatabaseUpdater() {
        connection = ConnectionFactory.getConnection();
        connection.startTransaction();
    }

    /**
     * FIXME: Javadoc einfuegen
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
        }
    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   query
     *
     * @return
     */
    public boolean execute(String query) {
        Loggin.logQuery(connection, query);

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
     * FIXME: Javadoc einfuegen
     */
    public void rollback() {
        connection.rollback();
        connection.close();
    }
}
