package de.draigon.sdf.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * ConnectionProperties object created from the db.properties file places in
 * Project-Root, Src-Folder
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class ConnectionProperties {

	/** name of the properties file */
	private static final String PROPERTIES_NAME = "db.properties";

	/** Instance for singleton */
	private static ConnectionProperties properties;

	private String host;
	private String username;
	private String password;
	private String database;
	private String port;
	private boolean embedded = false;

	public int poolsizeMin;

	/** default value for max poolsize */
	public int poolsizeMax;

	/** default value for the poolsize buffer */
	public int poolsizeBuffer;

	/**
	 * private constructor to prevent initialisation
	 */
	private ConnectionProperties() {

	}

	/**
	 * Returns the instance of the singleton. If not exists, a new instance is
	 * created
	 * 
	 * @return instance of the singleton
	 * @throws IOException
	 *             if properties cant be load
	 */
	public static ConnectionProperties get() throws IOException {

		if (properties == null) {
			properties = new ConnectionProperties();

			try {
				properties.reload();
			} catch (IOException e) {
				throw new IOException("No databaseproperties could be load.", e);
			}
		}

		return properties;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public boolean isEmbedded() {
		return embedded;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public int getPoolsizeBuffer() {
		return poolsizeBuffer;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public int getPoolsizeMax() {
		return poolsizeMax;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public int getPoolsizeMin() {
		return poolsizeMin;
	}

	/**
	 * reloads the properties from the file
	 * 
	 * @throws IOException
	 *             if properties cant be load
	 */
	private void reload() throws IOException {

		try {
			Properties properties = new Properties();

			File propFile = new File(PROPERTIES_NAME);

			if (propFile.exists()) {
				properties.load(new FileInputStream(propFile));
			} else {
				InputStream propFileStream = Thread.currentThread()
						.getContextClassLoader()
						.getResourceAsStream(PROPERTIES_NAME);
				if (propFileStream != null) {
					properties.load(propFileStream);
				} else {
					throw new IOException(
							"no properties for databaseconnection found: "
									+ PROPERTIES_NAME);
				}
			}

			this.host = properties.getProperty("host");
			this.database = properties.getProperty("database");
			this.username = properties.getProperty("username");
			this.password = properties.getProperty("password");
			this.port = properties.getProperty("port");
			this.embedded = "true".equalsIgnoreCase(properties
					.getProperty("embedded"));

			this.poolsizeMin = getPropertyInt(properties, "poolsize_min",
					ConnectionFactory.DEFAULT_POOLSIZE_MIN);
			this.poolsizeMax = getPropertyInt(properties, "poolsize_max",
					ConnectionFactory.DEFAULT_POOLSIZE_MAX);
			this.poolsizeBuffer = getPropertyInt(properties, "poolsize_buffer",
					ConnectionFactory.DEFAULT_POOLSIZE_BUFFER);

		} catch (IOException e) {
			throw new IOException(
					"could not load properties for databaseconnection from: "
							+ PROPERTIES_NAME);
		}

	}

	/**
	 * fetches a value for a key from a properties object and returns it as int.
	 * If no int is fetchable, detfaultValue is returned
	 * 
	 * @param properties
	 *            the properties object
	 * @param key
	 *            the key to fetch
	 * @param defaultValue
	 *            the default value, if no int can be fetched
	 * @return the fethed value or defaultValue if not set
	 */
	private int getPropertyInt(Properties properties, String key,
			int defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
