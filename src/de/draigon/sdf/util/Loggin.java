package de.draigon.sdf.util;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;

import de.draigon.sdf.connection.ConnectionFactory;
import de.draigon.sdf.connection.ConnectionProperties;
import de.draigon.sdf.connection.DBConnection;
import de.draigon.sdf.exception.DBException;

/**
 * Loggin for the Framework
 * 
 * @TODO: should be modified to use commons.loggin
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class Loggin {

	/** parameter if loggin querys */
	private static boolean SHOW_QUERY;

	/** parameter if showing informations abour the connection factory */
	private static boolean CONNECTION_FACTORY_INFO;
	/**
	 * paraeter to tell if structure warning on table creater should be
	 * published
	 */
	private static boolean TABLE_STRUCTURE_WARNINGS;

	/** formatter for loggin */
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");

	static{
		//initialize from properties
		try {
			ConnectionProperties properties = new ConnectionProperties();
			SHOW_QUERY = properties.isShowQuery();
			CONNECTION_FACTORY_INFO = properties.isConnectionFactoryInfo();
			TABLE_STRUCTURE_WARNINGS = properties.isTableStructureWarning();
			
		} catch (IOException e) {
			throw new DBException("cant read properties file...", e);
		}
	}
	
	/**
	 * log messages from the connection factory
	 * 
	 * @param string
	 *            the message to log
	 */
	public static void logConnectionFactory(String string) {

		if (Loggin.CONNECTION_FACTORY_INFO) {
			System.out.println("[" + formatter.format(new Date()) + "] ["
					+ ConnectionFactory.class.getCanonicalName() + "]> "
					+ string);
		}
	}

	/**
	 * logs a query to the log-screen
	 * 
	 * @param connection
	 *            the connection to execute the query
	 * @param query
	 *            the query to execute
	 */
	public static void logQuery(DBConnection connection, String query) {

		if (Loggin.SHOW_QUERY) {
			System.out.println("[" + formatter.format(new Date())
					+ "] [Executionquery on {" + connection.toString() + "}]> "
					+ query);
		}
	}

	/**
	 * prints a table-structure error on the log-screen
	 * 
	 * @param string
	 *            the table structure error
	 */
	public static void logFieldWarn(String string) {
		if (Loggin.TABLE_STRUCTURE_WARNINGS) {
			System.err.println("[" + formatter.format(new Date())
					+ "] [WARNING TABLESTRUCTURE]> " + string);
		}
	}
}
