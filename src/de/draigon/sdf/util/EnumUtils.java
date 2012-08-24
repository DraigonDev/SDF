package de.draigon.sdf.util;

import de.draigon.sdf.annotation.DBValue;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class EnumUtils {

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   id
     * @param   type
     *
     * @return
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
     * FIXME: Javadoc kontrollieren Liefert den Wert von id
     *
     * @param   value
     *
     * @return  Der Wert von id
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
