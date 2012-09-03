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
 * utilclass to do operations on object extending {@link Entity}
 *
 * @author Draigon Development
 * @version 1.0
 */
public class DaoUtils {

    /** a Date-formatter*/
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * generates a random uuid and sets it to the entity
     *
     * @param  entity the entitiy to set a new uuid
     */
    public static <T> void generateUuid(T entity) {
        setUuid(entity, UUID.randomUUID().toString());
    }

    /**
     * returns all fields in the given class including those from its superclasses
     *
     * @param   clazz the class to inspect
     *
     * @return  the fields
     */
    public static List<ExtendedField> getAllFieldsWithSuperclasses(Class<?> clazz) {

        if (!DaoUtils.isEntity(clazz)) {
            throw new DBException("'" + clazz + "' must extend class '" + Entity.class + "'");
        }

        return getAllFieldsWithSuperclasses(clazz, clazz);
    }

    /**
     * returns all relations in the given class including those from its superclasses
     *
     * @param   clazz the class to inspect
     *
     * @return  the mappings
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
     * the value of the {@link Table} annotation
     *
     * @param   mainObjectclass the class to inspect
     *
     * @return  the mapped table 
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
     * returns the value of the UUID field of an object
     *
     * @param   entity the object
     *
     * @return  the value of UUID
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
     * checks if the class extends {@link Entity}
     *
     * @param   clazz the class to inspect
     *
     * @return  true if the given class extends {@link Entity}
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
     * Sets the value fo uuid
     *
     * @param  entity the entity to set to
     * @param  value the value to set to entity
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
     * returns all fields in the given class including those from its superclasses
     *
     * @param   clazz the class to inspect
     *
     * @return  the fields
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
     * the value of {@link Table} annotation on the class
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
