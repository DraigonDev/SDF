package de.draigon.sdf.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.draigon.sdf.exception.DBException;


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

	private String host;
	private String username;
	private String password;
	private String database;
	private String port;
	private boolean embedded = false;

	public Integer poolsizeMin;

	/** default value for max poolsize */
	public Integer poolsizeMax;

	/** default value for the poolsize buffer */
	public Integer poolsizeBuffer;

	private boolean showQuery;

	private boolean connectionFactoryInfo;

	private boolean tableStructureWarning;

	/**
	 * private constructor to prevent initialisation
	 * @throws IOException 
	 */
	public ConnectionProperties() throws IOException {
		this.reload();
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
	public Integer getPoolsizeBuffer() {
		return poolsizeBuffer;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public Integer getPoolsizeMax() {
		return poolsizeMax;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public Integer getPoolsizeMin() {
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
			

			this.showQuery = "true".equalsIgnoreCase(properties
					.getProperty("show_query"));

			this.connectionFactoryInfo = "true".equalsIgnoreCase(properties
					.getProperty("connection_factory_info"));

			this.tableStructureWarning = "true".equalsIgnoreCase(properties
					.getProperty("table_structure_warning"));

			this.poolsizeMin = loadPropertyInt(properties, "poolsize_min");
			
			this.poolsizeMax = loadPropertyInt(properties, "poolsize_max");
			
			this.poolsizeBuffer = loadPropertyInt(properties, "poolsize_buffer");

		} catch (Exception e) {
			throw new IOException(
					"could not load properties for databaseconnection from: "
							+ PROPERTIES_NAME, e);
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
	private Integer loadPropertyInt(Properties properties, String key) {
		String value = properties.getProperty(key);

		if (value == null) {
			return null;
		}
		if(!value.matches("[0-9]+")){
			throw new DBException(properties+"-value from properties file must be an integer value (0-9) but is '"+value+"'");
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public boolean isShowQuery() {
		return this.showQuery;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public boolean isConnectionFactoryInfo() {
		return this.connectionFactoryInfo;
	}

	/**
	 * Getter for property
	 * 
	 * @return value of the property
	 */
	public boolean isTableStructureWarning() {
		return this.tableStructureWarning;
	}
}
