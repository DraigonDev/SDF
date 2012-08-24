package de.draigon.sdf.daos.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.draigon.sdf.Entity;
import de.draigon.sdf.annotation.Table;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.objects.ExtendedField;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class DaoUtils {

    /** FIXME: Javadoc einfuegen */
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  entity
     */
    public static <T> void generateUuid(T entity) {
        setUuid(entity, UUID.randomUUID().toString());
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von all fields with superclasses
     *
     * @param   clazz
     *
     * @return  Der Wert von all fields with superclasses
     */
    public static List<ExtendedField> getAllFieldsWithSuperclasses(Class<?> clazz) {

        if (!DaoUtils.isEntity(clazz)) {
            throw new DBException("'" + clazz + "' must extend class '" + Entity.class + "'");
        }

        return getAllFieldsWithSuperclasses(clazz, clazz);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von all mappings with superclasses
     *
     * @param   clazz
     *
     * @return  Der Wert von all mappings with superclasses
     */
    public static List<ExtendedField> getAllMappingsWithSuperclasses(Class<?> clazz) {
        List<ExtendedField> fields = new ArrayList<ExtendedField>();

        for (ExtendedField field : getAllFieldsWithSuperclasses(clazz)) {

            if (field.hasEntityMapping()) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von table name from main
     *
     * @param   mainObjectclass
     *
     * @return  Der Wert von table name from main
     */
    public static String getTableName(Class<?> mainObjectclass) {

        if (Entity.class.equals(mainObjectclass)) {
            throw new DBException("no tablemapping for '" + mainObjectclass + "'");
        }

        if (mainObjectclass.isAnnotationPresent(Table.class)) {
            return getTableNameInClass(mainObjectclass);
        }

        return getTableName(mainObjectclass.getSuperclass());
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von uuid
     *
     * @param   entity
     *
     * @return  Der Wert von uuid
     */
    public static <T> String getUuid(T entity) {

        try {
            Class<?> superClass = Entity.class;
            Method method = superClass.getDeclaredMethod("getUuid");
            method.setAccessible(true);

            return (String) method.invoke(entity);
        } catch (Exception e) {
            throw new DBException("Unknown error", e);
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von entity
     *
     * @param   clazz
     *
     * @return  Der Wert von entity
     */
    public static boolean isEntity(Class<?> clazz) {

        if (Object.class.equals(clazz)) {
            return false;
        }

        if (Entity.class.equals(clazz.getSuperclass())) {
            return true;
        }

        return isEntity(clazz.getSuperclass());
    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  entity
     */
    public static <T> void resetUuid(T entity) {
        setUuid(entity, "");
    }

    /**
     * FIXME: Javadoc kontrollieren Setzt den neuen Wert von uuid
     *
     * @param  entity
     * @param  value
     */
    public static void setUuid(Object entity, String value) {

        try {
            Class<?> superClass = Entity.class;
            Method method = superClass.getDeclaredMethod("setUuid", String.class);
            method.setAccessible(true);

            method.invoke(entity, value);
        } catch (Exception e) {
            throw new DBException("Unknown error", e);
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von all fields with superclasses
     *
     * @param   clazz
     *
     * @return  Der Wert von all fields with superclasses
     */
    private static List<ExtendedField> getAllFieldsWithSuperclasses(Class<?> clazz,
        Class<?> mainClass) {
        List<ExtendedField> fields = new ArrayList<ExtendedField>();

        for (Field field : clazz.getDeclaredFields()) {
            fields.add(new ExtendedField(mainClass, clazz, field));
        }

        if (!Object.class.equals(clazz.getSuperclass())) {
            fields.addAll(getAllFieldsWithSuperclasses(clazz.getSuperclass(), mainClass));
        }

        return fields;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von table name
     *
     * @param   clazz
     *
     * @return  Der Wert von table name
     */
    private static String getTableNameInClass(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);

        if (table == null) {
            throw new DBException("missing table declaration for class '"
                + clazz + "'");
        }

        return table.value();
    }
}
