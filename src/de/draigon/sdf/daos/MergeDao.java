package de.draigon.sdf.daos;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.draigon.sdf.connection.DatabaseUpdater;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.exception.UpdateException;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.objects.mergedelete.Inserts;
import de.draigon.sdf.objects.mergedelete.MergeMapping;
import de.draigon.sdf.util.DB;
import de.draigon.sdf.util.EnumUtils;

/**
 * FIXME: Javadoc einfuegen
 * 
 * @author
 */
public class MergeDao<T> {

	/**
	 * FIXME: Javadoc einfuegen
	 * 
	 * @param object
	 * 
	 * @return
	 * 
	 * @throws UpdateException
	 */
	@SuppressWarnings("unchecked")
	public T perform(T object) throws UpdateException {
		DatabaseUpdater updater = new DatabaseUpdater();

		try {
			object = (T) this.merge(updater, object, null);
		} catch (DBException e) {
			updater.rollback();
			throw new DBException("update rolled back", e);
		} catch (UpdateException e) {
			updater.rollback();
			throw new UpdateException("update is rolled back", e);
		}

		try {
			updater.commit();
		} catch (SQLException e) {
			throw new DBException("could not merge object + " + object, e);
		}

		return object;
	}

	private void clearReferences(DatabaseUpdater updater, MergeMapping mapping)
			throws UpdateException {

		if (mapping.isManyToMany()) {
			String query = "DELETE FROM " + DB.ESCAPE + ""
					+ mapping.getMappingTable() + "" + DB.ESCAPE + " WHERE "
					+ DB.ESCAPE + "" + mapping.getColumnFrom() + "" + DB.ESCAPE
					+ " = '" + mapping.getValueFrom() + "'";

			if (!updater.execute(query)) {
				throw new UpdateException(
						"unable to insert relation to mappingtable");
			}
		} else if (mapping.isManyToOne()) {
			String query = "UPDATE " + DB.ESCAPE + ""
					+ mapping.getMappingTable() + "" + DB.ESCAPE + " SET "
					+ DB.ESCAPE + "" + mapping.getColumnFrom() + "" + DB.ESCAPE
					+ " = 'NULL' WHERE " + DB.ESCAPE + ""
					+ mapping.getColumnFrom() + "" + DB.ESCAPE + " = '"
					+ mapping.getValueFrom() + "'";

			if (!updater.execute(query)) {
				throw new UpdateException(
						"unable to insert relation to mappingtable");
			}
		}
	}

	private String getInsertValue(Object value) {

		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return "'" + DaoUtils.DATE_FORMATTER.format(value) + "'";
		} else if (value.getClass().isEnum()) {
			return "" + EnumUtils.getId(value) + "";
		} else if (value instanceof String) {
			value = value.toString().replaceAll("'", "''");

			return "'" + value + "'";
		} else {
			// All types of Numbers
			return "" + value.toString() + "";
		}
	}

	private String getUpdateSetClause(String dbColumnName, Object value) {

		if (value instanceof Date) {
			return dbColumnName + "='" + DaoUtils.DATE_FORMATTER.format(value)
					+ "'";
		} else if (value.getClass().isEnum()) {
			return dbColumnName + "=" + EnumUtils.getId(value) + "";
		} else if (value instanceof String) {
			value = value.toString().replaceAll("'", "''");

			return dbColumnName + "='" + value + "'";
		} else {
			// All types of Numbers
			return dbColumnName + "=" + value.toString() + "";
		}
	}

	/**
	 * FIXME: Javadoc einfuegen
	 * 
	 * @param object
	 * @param object2
	 * @param mapping
	 * 
	 * @return
	 * 
	 * @throws UpdateException
	 */
	private Object insert(DatabaseUpdater updater, Object object,
			MergeMapping mapping) throws UpdateException {

		if (object == null) {
			throw new NullPointerException("object to insert mustn't be null");
		}

		Inserts columns = new Inserts();
		Inserts values = new Inserts();

		Class<?> clazz = object.getClass();
		String tableName = DaoUtils.getTableName(clazz);

		if (mapping != null && mapping.isOneToOne()) {
			DaoUtils.setUuid(object, mapping.getValueFrom());
		} else {
			DaoUtils.generateUuid(object);
		}

		for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(clazz)) {

			if (field.hasDBMapping()) {
				Object value = field.getValue(object);

				// add insertPair
				columns.add(field.getDBColumnName());
				values.add(getInsertValue(value));
			}

			if (field.hasEntityMapping()
					&& MappingType.ONE_TO_MANY.equals(field.getEntityMapping())) {
				Object subObject = field.getValue(object);

				MergeMapping nextMapping = new MergeMapping(object, field);
				nextMapping.setObjectTo(subObject);

				subObject = merge(updater, subObject, nextMapping);
				field.setValue(object, subObject); // should not e nessesary

				// now get the UUID for the insert
				Object value = DaoUtils.getUuid(subObject);

				// add insertPair
				columns.add(DaoUtils.getTableName(field.getMappedType()) + "_UUID");
				values.add(getInsertValue(value));
			}
		}

		if (mapping != null && mapping.isManyToOne()) {
			columns.add("" + DB.ESCAPE + "" + mapping.getColumnFrom() + ""
					+ DB.ESCAPE + "");
			values.add("'" + mapping.getValueFrom() + "'");
		}

		if (!columns.isEmpty()) {
			String update = "INSERT INTO " + DB.ESCAPE + "" + tableName + ""
					+ DB.ESCAPE + " ( " + columns.getList() + " ) VALUES ( "
					+ values.getList() + " )";

			if (!updater.execute(update)) {
				throw new UpdateException("could not insert entity " + object);
			}
		}

		return object;
	}

	@SuppressWarnings("unchecked")
	private Object merge(DatabaseUpdater updater, Object object,
			MergeMapping mapping) throws UpdateException {
		String uuid = DaoUtils.getUuid(object);

		if (uuid == null) {
			object = this.insert(updater, object, mapping);

		} else {
			object = this.update(updater, object, mapping);

		}

		if (mapping != null && mapping.isManyToMany()) {
			String query = "INSERT INTO " + DB.ESCAPE + ""
					+ mapping.getMappingTable() + "" + DB.ESCAPE + " ("
					+ DB.ESCAPE + "" + mapping.getColumnFrom() + "" + DB.ESCAPE
					+ ", " + DB.ESCAPE + "" + mapping.getColumnTo() + ""
					+ DB.ESCAPE + ") VALUES ('" + mapping.getValueFrom()
					+ "','" + mapping.getValueTo() + "')";

			if (!updater.execute(query)) {
				throw new UpdateException(
						"unable to insert relation to mappingtable");
			}
		}

		for (ExtendedField field : DaoUtils
				.getAllMappingsWithSuperclasses(object.getClass())) {

			if (field.isCascadingMerge()) {
				MergeMapping nextMapping = new MergeMapping(object, field);

				if (nextMapping.isManyToMany() || nextMapping.isManyToOne()) {
					clearReferences(updater, nextMapping);

					List<Object> values = (List<Object>) field.getValue(object);
					if (values != null) { //if null, then ignore it - no list set then, so nothing to insert
						for (Object subObject : values) {
							nextMapping.setObjectTo(subObject);
							merge(updater, subObject, nextMapping);
						}
					}
				} else if (nextMapping.isOneToOne()) {
					Object subObject = field.getValue(object);
					nextMapping.setObjectTo(subObject);
					merge(updater, subObject, nextMapping);
				}
				// OneToMany des nothing. this merge will be performed in
				// update/insert, becasue it needs ti be in db before mainobject
			}
		}

		return object;
	}

	/**
	 * FIXME: Javadoc einfuegen
	 * 
	 * @param object
	 * @param mapping
	 * @param object2
	 * 
	 * @return
	 * 
	 * @throws UpdateException
	 */
	private Object update(DatabaseUpdater updater, Object object,
			MergeMapping mapping) throws UpdateException {

		if (object == null) {
			throw new NullPointerException("object to insert mustn't be null");
		}

		Inserts query = new Inserts();
		Class<?> clazz = object.getClass();
		String tableName = DaoUtils.getTableName(clazz);

		for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(clazz)) {

			if (field.hasDBMapping()) {
				Object value = field.getValue(object);

				query.add(getUpdateSetClause(field.getDBColumnName(), value));
			}

			if (field.hasEntityMapping()
					&& MappingType.ONE_TO_MANY.equals(field.getEntityMapping())) {
				Object subObject = field.getValue(object);

				MergeMapping nextMapping = new MergeMapping(object, field);
				nextMapping.setObjectTo(subObject);

				subObject = merge(updater, subObject, nextMapping);
				field.setValue(object, subObject); // should not e nessesary

				// now get the UUID for the insert
				Object value = DaoUtils.getUuid(subObject);

				// add insertPair
				query.add(getUpdateSetClause(
						DaoUtils.getTableName(field.getMappedType()) + "_UUID", value));
			}
		}

		if (mapping != null && mapping.isManyToOne()) {
			query.add(getUpdateSetClause(
					"" + DB.ESCAPE + "" + mapping.getColumnFrom() + ""
							+ DB.ESCAPE + "", mapping.getValueFrom()));
		}

		if (!query.isEmpty()) {
			String update = "UPDATE " + DB.ESCAPE + "" + tableName + ""
					+ DB.ESCAPE + " SET " + query.getList() + " WHERE "
					+ DB.ESCAPE + "" + tableName + "" + DB.ESCAPE + "."
					+ DB.ESCAPE + "UUID" + DB.ESCAPE + " = '"
					+ DaoUtils.getUuid(object) + "'";

			if (!updater.execute(update)) {
				throw new UpdateException(
						"no entry found in database for object: " + object);
			}
		}

		return object;
	}
}
