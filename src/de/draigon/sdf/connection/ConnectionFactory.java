package de.draigon.sdf.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.util.Loggin;

/**
 * Factory for managing a pool of database connections.
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class ConnectionFactory {

	/** default value for min poolsize */
	public static int DEFAULT_POOLSIZE_MIN = 1;

	/** default value for max poolsize */
	public static int DEFAULT_POOLSIZE_MAX = 10;

	/** default value for the poolsize buffer */
	public static int DEFAULT_POOLSIZE_BUFFER = 4;

	/** default value, how long to wait for connecton to place a warning */
	public static int WARN_TIME = 60;

	/** default value, how long to wait for connection to place an error */
	public static int ERROR_TIME = 120;

	/** property if to use the embedded database or mysql */
	private static boolean embedded = false;

	/** the connectionpool */
	private static List<DBConnection> connections;

	/** indicates wheather a request for a connection is pending or not */
	private static boolean waiting = false;

	/** the minimal poolsize */
	private static int poolsizeMin;

	/** the maximal poolsize */
	private static int poolsizeMax;

	/** the poolsize buffer */
	private static int poolsizeBuffer;

	// TODO if all works, delete this rows
	// @SuppressWarnings("unused")
	// private static ConnectionFactory destructor = new ConnectionFactory();

	/**
	 * //Will be called, if factory is destroyed, so on system exit. Then in
	 * case of embadded database usage, the derby database is stopped.
	 */
	@Override
	protected void finalize() throws Throwable {
		if (ConnectionFactory.embedded) {
			EmbeddedConnection.shutdown();
		}

	}

	/**
	 * Initiate the database pool.
	 */
	static {
		try {
			ConnectionProperties properties = new ConnectionProperties();

			ConnectionFactory.embedded = properties.isEmbedded();
			ConnectionFactory.poolsizeMin = properties.getPoolsizeMin() != null ? properties
					.getPoolsizeMin() : ConnectionFactory.DEFAULT_POOLSIZE_MIN;

			ConnectionFactory.poolsizeMax = properties.getPoolsizeMax() != null ? properties
					.getPoolsizeMax() : ConnectionFactory.DEFAULT_POOLSIZE_MAX;

			ConnectionFactory.poolsizeBuffer = properties.getPoolsizeBuffer() != null ? properties
					.getPoolsizeBuffer()
					: ConnectionFactory.DEFAULT_POOLSIZE_BUFFER;

			if (ConnectionFactory.poolsizeMin < 1) {
				throw new DBException("min poolsize must be minimum 1 (value="
						+ ConnectionFactory.poolsizeMin + ")");
			}
			if (ConnectionFactory.poolsizeMax < 1) {
				throw new DBException("min poolsize must be minimum 1 (value="
						+ ConnectionFactory.poolsizeMax + ")");
			}
			if (ConnectionFactory.poolsizeBuffer < 0) {
				throw new DBException("poolsizebuffer must be positive (value="
						+ ConnectionFactory.poolsizeBuffer + ")");
			}

		} catch (IOException e) {
			throw new DBException("could not connect to database", e);
		}

		if (ConnectionFactory.embedded) {
			Loggin.logConnectionFactory("using embedded database (org.apache.derby)");
		} else {
			Loggin.logConnectionFactory("using MySQL database (default)");
		}

		connections = new ArrayList<DBConnection>();

		for (int i = 0; i < poolsizeMin; i++) {
			connections.add(buildConnection());
		}

		Loggin.logConnectionFactory("initialized a pool of "
				+ connections.size() + " connections.");
	}

	/**
	 * Returns a {@link DBConnection} from the pool. If no connection available,
	 * this method will wait to get one.
	 * 
	 * @return Der Wert von connection
	 */
	public static synchronized DBConnection getConnection() {
		String serviceId = UUID.randomUUID().toString().substring(0, 6);
		Loggin.logConnectionFactory("connection requested by ID{" + serviceId
				+ "}");

		DBConnection connection = searchConnection();
		Loggin.logConnectionFactory(connection + " returned to ID{" + serviceId
				+ "}");

		return connection;
	}

	/**
	 * Determinates the connection pool to actual sizes (relative to default
	 * values).
	 */
	public static synchronized void notifyFreeConnection(DBConnection released) {
		Loggin.logConnectionFactory(released + " released");

		if (connections.size() > ConnectionFactory.poolsizeMin) {

			while (getFreeConnectionCount() > ConnectionFactory.poolsizeBuffer) {
				DBConnection connection = getFreeConnection();
				connection.kill();
				connections.remove(connection);
			}
		}
		waiting = false;
	}

	/**
	 * Returns a free connection from the pool
	 * 
	 * @return a free connection from the pool
	 */
	private static DBConnection getFreeConnection() {

		for (DBConnection conn : connections) {

			if (!conn.isInUse()) {
				return conn;
			}
		}

		return null;
	}

	/**
	 * Counts the number of free connections
	 * 
	 * @return the number of free connections
	 */
	private static int getFreeConnectionCount() {
		int free = 0;

		for (DBConnection conn : connections) {

			if (!conn.isInUse()) {
				free++;
			}
		}

		return free;
	}

	/**
	 * fetches a connections, and if no available, it creates a new one. If
	 * max-number of connections is arrived, it will wait for another to get
	 * free.
	 * 
	 * @return a DBConnection from the pool
	 */
	private static DBConnection searchConnection() {

		while (true) {

			DBConnection conn = getFreeConnection();

			if (conn != null) {
				conn.setInUse();

				return conn;
			}
			// no free connection found

			if (connections.size() < ConnectionFactory.poolsizeMax) {
				conn = buildConnection();
				connections.add(conn);
				conn.setInUse();

				Loggin.logConnectionFactory("poolsize increased to "
						+ connections.size());

				return conn;
			} else {
				Loggin.logConnectionFactory("pending for connection: max poolsize of '"
						+ ConnectionFactory.poolsizeMax + "' reached");

				waitForConnection();
			}
		}

	}

	/**
	 * Builds a new DBConnection in association to embeddded or mysql database.
	 * 
	 * @return
	 */
	private static DBConnection buildConnection() {
		if (ConnectionFactory.embedded) {
			return new EmbeddedConnection();
		} else {
			return new MySQLConnection();
		}
	}

	/**
	 * Waits for a free connection
	 * 
	 * @return the free connection.
	 */
	private static void waitForConnection() {
		// waiting will be set true through notifyfreeconnection
		waiting = true;

		double delay = 0;

		while (waiting) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Nothing
			}

			delay += 0.1;

			if (delay == WARN_TIME) {
				System.err
						.println("client is pending for databseconnection for more than "
								+ WARN_TIME
								+ " seconds now. - check if all connections are closed correctly");
			}

			if (delay == ERROR_TIME) {
				throw new DBException(
						"client is pending for databseconnection for more than "
								+ ERROR_TIME
								+ " seconds now. - check if all connections are closed correctly");
			}
		}
	}
}
