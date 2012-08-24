package de.draigon.sdf.util;

import java.text.SimpleDateFormat;

import java.util.Date;

import de.draigon.sdf.connection.ConnectionFactory;
import de.draigon.sdf.connection.DBConnection;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class Loggin {

    /** FIXME: Javadoc einfuegen */
    public static boolean SHOW_QUERY = false;

    /** FIXME: Javadoc einfuegen */
    public static boolean CONNECTION_FACTORY_INFO = false;
    
    public static boolean TABLE_STRUCTURE_WARNINGS = true;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  string
     */
    public static void logConnectionFactory(String string) {

        if (Loggin.CONNECTION_FACTORY_INFO) {
            System.out.println("[" + formatter.format(new Date()) + "] ["
                + ConnectionFactory.class.getCanonicalName() + "]> " + string);
        }
    }


    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  connection
     * @param  query
     */
    public static void logQuery(DBConnection connection, String query) {

        if (Loggin.SHOW_QUERY) {
            System.out.println("[" + formatter.format(new Date()) + "] [Executionquery on {"
                + connection.toString() + "}]> " + query);
        }
    }


    public static void logFieldWarn(String string) {
        if (Loggin.TABLE_STRUCTURE_WARNINGS) {
            System.err.println("[" + formatter.format(new Date()) + "] [WARNING TABLESTRUCTURE]> " + string);
        }
    }
}
