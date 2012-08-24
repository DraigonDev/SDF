package de.draigon.sdf.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import de.draigon.sdf.annotation.Cascade;
import de.draigon.sdf.annotation.CascadeDelete;
import de.draigon.sdf.annotation.CascadeLoad;
import de.draigon.sdf.annotation.CascadeMerge;
import de.draigon.sdf.annotation.DBColumn;
import de.draigon.sdf.annotation.ManyToMany;
import de.draigon.sdf.annotation.ManyToOne;
import de.draigon.sdf.annotation.OneToMany;
import de.draigon.sdf.annotation.OneToOne;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.util.DB;
import de.draigon.sdf.util.StringUtils;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class ExtendedField {
    private Class<?> clazz;
    private Field field;
    private String tableName;
    private MappingType entityMapping;
    private Class<?> mainObjectClass;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von ExtendedField.
     *
     * @param  clazz
     * @param  field
     */
    public ExtendedField(Class<?> mainObjectclass, Class<?> clazz, Field field) {
        this.tableName = DaoUtils.getTableName(mainObjectclass);
        this.clazz = clazz;
        this.field = field;
        this.mainObjectClass = mainObjectclass;

        field.setAccessible(true);

        buildMapping();
    }
    
    public String getTableName() {
        return tableName;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von DBColumn name
     *
     * @return  Der Wert von DBColumn name
     */
    public String getDBColumnName() {
        return ""+DB.ESCAPE+"" + this.getDBColumnAnnotation()
            .value() + ""+DB.ESCAPE+"";
    }
    
    public String getDBColumnNameWithoutEscape() {
        return this.getDBColumnAnnotation()
            .value();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von DBFull qualified name
     *
     * @return  Der Wert von DBFull qualified name
     */
    public String getDBFullQualifiedName() {
        return ""+DB.ESCAPE+"" + this.tableName + ""+DB.ESCAPE+"." + getDBColumnName();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von entity mapping
     *
     * @return  Der Wert von entity mapping
     */
    public MappingType getEntityMapping() {
        return this.entityMapping;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von field
     *
     * @return  Der Wert von field
     */
    public Field getField() {
        return field;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von main object class
     *
     * @return  Der Wert von main object class
     */
    public Class<?> getMainObjectClass() {
        return mainObjectClass;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapped tyoe
     *
     * @return  Der Wert von mapped tyoe
     */
    public Class<?> getMappedType() {

        switch (this.getEntityMapping()) {
        case ONE_TO_ONE:
        case ONE_TO_MANY:
            return this.getType();
        case MANY_TO_ONE:
        case MANY_TO_MANY:

            if (!"interface java.util.List".equals(this.field.getType().toString())) {
                throw new DBException(
                    "for mappings only java.util.List is supported as collectiontype");
            }

            ParameterizedType type = (ParameterizedType) this.field.getGenericType();

            return (Class<?>) type.getActualTypeArguments()[0];

        default:
            throw new IllegalArgumentException("can not happen");
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapping table
     *
     * @return  Der Wert von mapping table
     */
    public String getMappingTable() {
        ManyToMany mapping = field.getAnnotation(ManyToMany.class);

        return mapping.value();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von fulle qualified type
     *
     * @return  Der Wert von fulle qualified type
     */
    public Class<?> getMethodType() {

        if (this.isMappedAsList()) {

            try {
                return Class.forName("java.util.List");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("should never happen");
            }
        } else {
            return this.getType();
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von name
     *
     * @return  Der Wert von name
     */
    public String getName() {
        return this.field.getName();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von parent class
     *
     * @return  Der Wert von parent class
     */
    public Class<?> getParentClass() {
        return this.clazz;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von result full qualified name
     *
     * @return  Der Wert von result full qualified name
     */
    public String getResultFullQualifiedName() {
        return this.tableName + "_" + this.getDBColumnAnnotation()
            .value();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von type
     *
     * @return  Der Wert von type
     */
    public Class<?> getType() {

        if (this.isMappedAsList()) {
            return this.getMappedType();
        } else {
            return this.getField()
                .getType();
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von value
     *
     * @param   patternObject
     *
     * @return  Der Wert von value
     */
    public Object getValue(Object instance) {

        try {
            return this.getGetter()
                .invoke(instance);
        } catch (Exception e) {
            throw new DBException("unable to invoke getter for property '"
                + field.getName() + "' + on class '" + clazz + "'");
        }

    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @return
     */
    public boolean hasDBMapping() {
        return this.getDBColumnAnnotation() != null;
    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @return
     */
    public boolean hasEntityMapping() {
        return getEntityMapping() != null;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von cascading delete
     *
     * @return  Der Wert von cascading delete
     */
    public boolean isCascadingDelete() {
        return ((field.isAnnotationPresent(Cascade.class))
            || field.isAnnotationPresent(CascadeDelete.class));
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von cascading load
     *
     * @return  Der Wert von cascading load
     */
    public boolean isCascadingLoad() {
        return ((field.isAnnotationPresent(Cascade.class))
            || field.isAnnotationPresent(CascadeLoad.class));
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von cascading merge
     *
     * @return  Der Wert von cascading merge
     */
    public boolean isCascadingMerge() {
        return ((field.isAnnotationPresent(Cascade.class))
            || field.isAnnotationPresent(CascadeMerge.class));
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapped as list
     *
     * @return  Der Wert von mapped as list
     */
    public boolean isMappedAsList() {

        if (getEntityMapping() == null) {
            return false;
        } else {

            switch (getEntityMapping()) {
            case MANY_TO_MANY:
            case MANY_TO_ONE:
                return true;

            default:
                return false;
            }
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Setzt den neuen Wert von value
     *
     * @param  value
     */
    public void setValue(Object instance, Object value) {

        try {
            this.getSetter()
                .invoke(instance, value);
        } catch (Exception e) {
            throw new DBException("unable to invoke setter for property '"
                + field.getName() + "' + on class '" + clazz + "'", e);
        }

    }

    private void buildMapping() {

        if (field.isAnnotationPresent(OneToOne.class)) {
            this.entityMapping = MappingType.ONE_TO_ONE;
        }

        if (field.isAnnotationPresent(ManyToOne.class)) {
            this.entityMapping = MappingType.MANY_TO_ONE;
        }

        if (field.isAnnotationPresent(ManyToMany.class)) {
            this.entityMapping = MappingType.MANY_TO_MANY;
        }
        
        if (field.isAnnotationPresent(OneToMany.class)) {
            this.entityMapping = MappingType.ONE_TO_MANY;
        }

    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von DBColumn annotation
     *
     * @return  Der Wert von DBColumn annotation
     */
    private DBColumn getDBColumnAnnotation() {
        return field.getAnnotation(DBColumn.class);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von getter
     *
     * @return  Der Wert von getter
     */
    private Method getGetter() {
        String getterName = "get"
            + StringUtils.firstToUpper(field.getName());
        Method getter;

        try {
            getter = this.clazz.getDeclaredMethod(getterName);
            getter.setAccessible(true);
        } catch (Exception e) {
            throw new DBException("no getter '" + getterName
                + "' for field '" + field.getName()
                + "' found on class '" + clazz + "'");
        }

        return getter;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von setter
     *
     * @return  Der Wert von setter
     */
    private Method getSetter() {
        String setterName = "set" + StringUtils.firstToUpper(field.getName());
        Method setter;

        try {
            setter = this.clazz.getDeclaredMethod(setterName, getMethodType());
            setter.setAccessible(true);
        } catch (Exception e) {
            throw new DBException("no setter '" + setterName
                + "' for field '" + field.getName()
                + "' found on class '" + clazz + "'", e);
        }

        return setter;
    }
}
