package de.draigon.sdf.util;

import java.io.IOException;

import de.draigon.sdf.connection.ConnectionProperties;
import de.draigon.sdf.exception.DBException;

/**
 * DB properties. this class accesses the escape sequence for embedded or not
 * embedded databases. the embadded database cant work with keys escaped via "`"
 * - on normal databases it should be used, to prevent function usage from key
 * accesses
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class DB {
	/**
	 * the database specific escape string
	 */
	public static String ESCAPE = "`";

	/**
	 * initiates the key from the properties embedded value
	 */
	static {
		try {
			if (new ConnectionProperties().isEmbedded()) {
				DB.ESCAPE = "";
			} else {
				DB.ESCAPE = "`";
			}
		} catch (IOException e) {
			throw new DBException("unable to access properties for DB-decision");
		}
	}
}
