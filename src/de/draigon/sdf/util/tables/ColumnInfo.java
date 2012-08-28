package de.draigon.sdf.util.tables;

import java.util.Date;

import de.draigon.sdf.annotation.DBFieldLength;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.util.Loggin;

public class ColumnInfo {
	private String name;
	private String type;
	private Integer length;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Integer getLength() {
		return length;
	}

	public ColumnInfo(ExtendedField field) {
		this.name = getFieldName(field);
		this.type = getType(field);
		this.length = getLength(field);
	}

	public ColumnInfo(String name2, String type2, Integer length2) {
		this.name = name2;
		this.type = type2;
		this.length = length2;

		if ("INTEGER".equalsIgnoreCase(this.type)) {
			this.type = "INT";
		}
	}

	private String getFieldName(ExtendedField field) {
		try {
			if (field.hasDBMapping()) {
				return field.getDBColumnNameWithoutEscape();
			}
			if (field.hasEntityMapping()
					&& MappingType.MANY_TO_ONE.equals(field.getEntityMapping())) {
				return field.getTableName() + "_UUID";
			}

			if (field.hasEntityMapping()
					&& MappingType.ONE_TO_MANY.equals(field.getEntityMapping())) {
				return DaoUtils.getTableName(field.getType()) + "_UUID";
			}
		} catch (NullPointerException e) {
			throw new DBException("cant get FieldName for field: " + field.getName() + " in class " + field.getMainObjectClass().getCanonicalName() , e);
		}

		throw new IllegalArgumentException("should never occure");
	}

	private String getType(ExtendedField field) {

		if (field.hasEntityMapping()) {
			return "VARCHAR"; // UUID
		}

		try {
			Class<?> clazzOfField = field.getType();

			if (String.class.equals(clazzOfField)) {
				return "VARCHAR";
			}

			if (Long.class.equals(clazzOfField)) {
				return "BIGINT";
			}

			if (Integer.class.equals(clazzOfField)) {
				return "INT";
			}

			if (Boolean.class.equals(clazzOfField)) {

				return "TINYINT";
			}

			if (Date.class.equals(clazzOfField)) {
				return "DATETIME";
			}

			if (Float.class.equals(clazzOfField)) {
				return "FLOAT";
			}

			if (Double.class.equals(clazzOfField)) {
				return "DOUBLE";
			}

			// Check for Enum
			if (clazzOfField.isEnum()) {
				return "SMALLINT";
			}
		} catch (IllegalArgumentException e) {
			throw new DBException("unable to alter table type for field '"
					+ field.getName() + "' to type "
					+ field.getMainObjectClass(), e);
		}
		throw new DBException("unsupported type for field '" + field.getName()
				+ "' on class " + field.getMainObjectClass());
	}

	private Integer getLength(ExtendedField field) {
		if ("UUID".equalsIgnoreCase(field.getName())) {
			return 36; // Foreighn_Key
		}
		if (field.hasEntityMapping()) {
			return 36; // UUID
		}

		try {
			Class<?> clazzOfField = field.getType();

			if (String.class.equals(clazzOfField)) {
				if (field.getField().isAnnotationPresent(DBFieldLength.class)) {
					return ((DBFieldLength) field.getField().getAnnotation(
							DBFieldLength.class)).value();
				} else {
					Loggin.logFieldWarn("no defined length for field '"
							+ field.getName() + "' on type "
							+ field.getMainObjectClass().getCanonicalName()
							+ ": taking default length (50).");
					return 50;
				}
			}
		} catch (IllegalArgumentException e) {
			throw new DBException("unable to alter table type for field '"
					+ field.getName() + "' to type "
					+ field.getMainObjectClass(), e);
		}

		return null;
	}

	public String getLengthAsInsert() {
		if (this.length == null) {
			return "";
		} else {
			return "(" + this.length + ")";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnInfo other = (ColumnInfo) obj;

		if ("VARCHAR".equalsIgnoreCase(type)) {
			if (length == null) {
				if (other.length != null)
					return false;
			} else if (!length.equals(other.length))
				return false;
		}

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equalsIgnoreCase(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ColumnInfo [length=" + length + ", name=" + name + ", type="
				+ type + "]";
	}

}
