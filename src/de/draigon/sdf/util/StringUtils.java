package de.draigon.sdf.util;

/**
 * Util class for String operations
 *
 * @author Draigon Development
 * @version 1.0
 */
public class StringUtils {

    /**
     * converts the first letter of a String to uppercase
     *
     * @param   value the string to convert
     *
     * @return value with first letter to upper case
     */
    public static String firstToUpper(String value) {

        if (value.length() == 0) {
            return value;
        }

        if (value.length() == 1) {
            return value.substring(0, 1)
                .toUpperCase();
        }

        return value.substring(0, 1)
            .toUpperCase() + value.substring(1, value.length());
    }

}
