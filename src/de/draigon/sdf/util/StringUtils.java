package de.draigon.sdf.util;

/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class StringUtils {

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   value
     *
     * @return
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
