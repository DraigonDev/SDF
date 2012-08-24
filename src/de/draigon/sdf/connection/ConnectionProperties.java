package de.draigon.sdf.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import de.draigon.sdf.exception.DBException;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class ConnectionProperties {

    private static final String PROPERTIES_NAME = "db.properties";

    private static ConnectionProperties properties;
    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean embedded = false;

    private ConnectionProperties() {

    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @return
     */
    public static ConnectionProperties get() {

        if (properties == null) {
            properties = new ConnectionProperties();

            try {
                properties.reload();
            } catch (SQLException e) {
                throw new DBException("No databaseproperties could be load.", e);
            }
        }

        return properties;
    }
    
    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von database
     *
     * @return  Der Wert von database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von host
     *
     * @return  Der Wert von host
     */
    public String getHost() {
        return host;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von passwort
     *
     * @return  Der Wert von passwort
     */
    public String getPassword() {
        return password;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von port
     *
     * @return  Der Wert von port
     */
    public String getPort() {
        return port;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von username
     *
     * @return  Der Wert von username
     */
    public String getUsername() {
        return username;
    }

    private void reload() throws SQLException {

        try {

            Properties properties = new Properties();

            File propFile = new File(PROPERTIES_NAME);
            
            if (propFile.exists()) {
                properties.load(new FileInputStream(propFile));
            }else{
                InputStream propFileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_NAME);
                if(propFileStream != null){
                    properties.load(propFileStream);
                }else{
                    throw new SQLException("no properties for databaseconnection found: "
                            + PROPERTIES_NAME);
                }
            }

            this.host = properties.getProperty("host");
            this.database = properties.getProperty("database");
            this.username = properties.getProperty("username");
            this.password = properties.getProperty("password");
            this.port = properties.getProperty("port");
            this.embedded = "true".equalsIgnoreCase(properties.getProperty("embedded"));

        } catch (FileNotFoundException e) {

            // Should be checked and never happen
            throw new DBException(e);
        } catch (IOException e) {
            throw new SQLException("could not load properties for databaseconnection from: "
                + PROPERTIES_NAME);
        }

    }
}
