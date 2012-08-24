package de.draigon.sdf.util;

import de.draigon.sdf.connection.ConnectionProperties;

public class DB {
    public static String ESCAPE = "`";
    
    static{
        if (ConnectionProperties.get().isEmbedded()) {
            DB.ESCAPE = "";
        } else {
            DB.ESCAPE = "`";
        }
    }
}
