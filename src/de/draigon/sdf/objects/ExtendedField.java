package de.draigon.sdf.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

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
 * expands a {@link Field} with extra methods by covering the original class
 * 
 * @author Draigon Development
 * @version 1.0
 */
public class ExtendedField {
	private Class<?> clazz;
	private Field field;
	private String tableName;
	private MappingType entityMapping;
	private Class<?> mainObjectClass;

	/**
	 * instanciates a new extendedField
	 * 
	 * @param mainObjectclass
	 *            the subclass invoked for the field
	 * @param clazz
	 *            the cncret class with this field
	 * @param field
	 *            the field
	 */
	public ExtendedField(Class<?> mainObjectclass, Class<?> clazz, Field field) {
		this.tableName = DaoUtils.getTableName(mainObjectclass);
		this.clazz = clazz;
		this.field = field;
		this.mainObjectClass = mainObjectclass;

		field.setAccessible(true);

		buildMapping();
	}

	/**
	 * returns the tablename where the subclass is mapped to
	 * 
	 * @return the tablename where the subclass is mapped to
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * returns the column name which this field is mapped to with db specific
	 * escapes
	 * 
	 * @return the column name which this field is mapped to
	 */
	public String getDBColumnName() {
		return "" + DB.ESCAPE + "" + this.getDBColumnAnnotation().value() + ""
				+ DB.ESCAPE + "";
	}

	/**
	 * returns the column name which this field is mapped to
	 * 
	 * @return the column name which this field is mapped to
	 */
	public String getDBColumnNameWithoutEscape() {
		return this.getDBColumnAnnotation().value();
	}

	/**
	 * returns the full qualified column name which this field is mapped to with
	 * db specific escapes
	 * 
	 * @return the full qualified column name which this field is mapped to
	 */
	public String getDBFullQualifiedName() {
		return "" + DB.ESCAPE + "" + this.tableName + "" + DB.ESCAPE + "."
				+ getDBColumnName();
	}

	/**
	 * returns the entity-mapping (if extsist) on this field.
	 * 
	 * @return null if no object-cross mapping exists, else the annotated {@link MappingType}
	 */
	public MappingType getEntityMapping() {
		return this.entityMapping;
	}

	/**
	 * returns the {@link Field}
	 * 
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * returns the subclass this field is mapped in
	 * 
	 * @return the subclass this field is mapped in
	 */
	public Class<?> getMainObjectClass() {
		return mainObjectClass;
	}

	/**
	 * gets the type of the field. If the field is mapped as {@link ManyToOne}
	 * or {@link ManyToMany}, the mapped field should be {@link List}, but the
	 * returned type will be the generic, this list contains.
	 * 
	 * @return the mapped type
	 */
	public Class<?> getMappedType() {
		if(this.isMappedAsList()){
			if (!"interface java.util.List".equals(this.field.getType()
					.toString())) {
				throw new DBException(
						"for mappings only java.util.List is supported as collectiontype (Field="+this.clazz.getCanonicalName()+"#"+this.field.getName()+")");
			}

			ParameterizedType type = (ParameterizedType) this.field
					.getGenericType();

			return (Class<?>) type.getActualTypeArguments()[0];
		}else{
			return this.getField().getType();
		}
	}

	/**
	 * returns the name of the n/m mappingtable.
	 * 
	 * @return the name of the n/m mappingtable. null, if no {@link ManyToMany} mapping exists on this field.
	 */
	public String getMappingTable() {
		ManyToMany mapping = field.getAnnotation(ManyToMany.class);
		
		if(mapping == null){
			return null;
		}
		
		return mapping.value();
	}

	/**
	 * returns the type, the setter should take as parameter
	 * 
	 * @return the type of the field, or {@link List} if this field is mapped as {@link ManyToMany} or ManyToOne
	 */
	public Class<?> getMethodType() {

		if (this.isMappedAsList()) {

			try {
				return Class.forName("java.util.List");
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("should never happen");
			}
		} else {
			return this.getMappedType();
		}
	}

	/**
	 * returns the field's name
	 * 
	 * @return the name of the field
	 */
	public String getName() {
		return this.field.getName();
	}

	/**
	 * returns the class, this field is specified in
	 * 
	 * @return the class this field is specified in
	 */
	public Class<?> getParentClass() {
		return this.clazz;
	}

	/**
	 * gets the full qualified name of the field in DB with underscore(_) instead of the db accessor (.)
	 * 
	 * @return the full qualified name with an underscore
	 */
	public String getResultFullQualifiedName() {
		return this.tableName + "_" + this.getDBColumnAnnotation().value();
	}

	/**
	 * returns the value of the field to the given object
	 * 
	 * @param instance the instance to invoke
	 * 
	 * @return the value of the field in the instance
	 */
	public Object getValue(Object instance) {

		try {
			return this.getGetter().invoke(instance);
		} catch (Exception e) {
			throw new DBException("unable to invoke getter for property '"
					+ field.getName() + "' + on class '" + clazz + "'");
		}

	}

	/**
	 * returns if the field is mapped to a DB-field
	 * 
	 * @return true, if the field has an entitymapping
	 */
	public boolean hasDBMapping() {
		return this.getDBColumnAnnotation() != null;
	}

	/**
	 * returns if the field has an entitymapping
	 * 
	 * @return true, if the field has an entitymapping
	 */
	public boolean hasEntityMapping() {
		return getEntityMapping() != null;
	}

	/**
	 * returns if the field is cascading delete
	 * 
	 * @return true if the field has {@link Cascade} or {@link CascadeDelete}
	 *         annotation
	 */
	public boolean isCascadingDelete() {
		return ((field.isAnnotationPresent(Cascade.class)) || field
				.isAnnotationPresent(CascadeDelete.class));
	}

	/**
	 * returns if the field is cascading load
	 * 
	 * @return true if the field has {@link Cascade} or {@link CascadeLoad}
	 *         annotation
	 */
	public boolean isCascadingLoad() {
		return ((field.isAnnotationPresent(Cascade.class)) || field
				.isAnnotationPresent(CascadeLoad.class));
	}

	/**
	 * returns if the field is cascading merge
	 * 
	 * @return true if the field has {@link Cascade} or {@link CascadeMerge}
	 *         annotation
	 */
	public boolean isCascadingMerge() {
		return ((field.isAnnotationPresent(Cascade.class)) || field
				.isAnnotationPresent(CascadeMerge.class));
	}

	/**
	 * returns true if this field is mapped as {@link ManyToMany} or
	 * {@link ManyToOne} where a {@link List} is mapped.
	 * 
	 * @return true if this field is mapped as {@link ManyToMany} or
	 *         {@link ManyToOne}
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
	 * sets the new value to the field by reflecting its getter
	 * 
	 * @param value
	 *            the value to set to the field
	 */
	public void setValue(Object instance, Object value) {

		try {
			this.getSetter().invoke(instance, value);
		} catch (Exception e) {
			throw new DBException("unable to invoke setter("+this.getMappedType()+") for property '"
					+ field.getName() + "' + on class '" + clazz + "' with value: " + value + "("+value.getClass().getCanonicalName()+")", e);
		}

	}

	/**
	 * wraps the annotated mapping type to {@link MappingType}
	 */
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
	 * gets the {@link DBColumn} annotation
	 * 
	 * @return die annotation
	 */
	private DBColumn getDBColumnAnnotation() {
		return field.getAnnotation(DBColumn.class);
	}

	/**
	 * returns the getter-method for this field.
	 * 
	 * @return the getter-method
	 */
	private Method getGetter() {
		String getterName = "get" + StringUtils.firstToUpper(field.getName());
		Method getter;

		try {
			getter = this.clazz.getDeclaredMethod(getterName);
			getter.setAccessible(true);
		} catch (Exception e) {
			throw new DBException("no getter '" + getterName + "' for field '"
					+ field.getName() + "' found on class '" + clazz + "'");
		}

		return getter;
	}

	/**
	 * returns the getter-method for this field.
	 * 
	 * @return the getter-method
	 */
	private Method getSetter() {
		String setterName = "set" + StringUtils.firstToUpper(field.getName());
		Method setter;

		try {
			setter = this.clazz.getDeclaredMethod(setterName, getMethodType());
			setter.setAccessible(true);
		} catch (Exception e) {
			throw new DBException("no setter '" + setterName + "' for field '"
					+ field.getName() + "' found on class '" + clazz + "'", e);
		}

		return setter;
	}
}
