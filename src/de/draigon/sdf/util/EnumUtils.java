package de.draigon.sdf.util;

import de.draigon.sdf.annotation.DBValue;


/**
 * Utilclass to access enums with {@link DBValue} annotation
 *
 * @author Draigon Development
 * @version 1.0
 */
public class EnumUtils {

    /**
     * returns an enum-value to an ID
     *
     * @param   id the id to fetch
     * @param   type the class to convert to
     *
     * @return the enum-value mapped by the id in the specified class. null if id is null.
     */
    public static <I> I fromId(Class<I> type, Integer id) {

        if (id == null) {
            return null;
        }

        I[] types = type.getEnumConstants();

        for (I t : types) {

            try {
                DBValue dbValue = type.getField(t.toString())
                    .getAnnotation(DBValue.class);

                if (dbValue.value() == id) {
                    return t;
                }

            } catch (Exception e) {
                // Error, so no found value - shit happens, assertion-error will follow
            }
        }

        throw new AssertionError("id-mapping '" + id + "' not found on '" + type + "'");
    }

    /**
     * returns the id to an enum-value
     *
     * @param   value the enum value
     *
     * @return  the id mapped on this value
     */
    public static Integer getId(Object value) {

        if (value == null) {
            return null;
        }

        try {
            DBValue dbValue = value.getClass()
                .getField(value.toString())
                .getAnnotation(DBValue.class);

            return dbValue.value();

        } catch (Exception e) {
            // Error, so no found value - shit happens, assertion-error will follow
        }

        throw new AssertionError("no id found for '" + value + "' on class '" + value.getClass()
            + "'");
    }

}
