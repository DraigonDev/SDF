package de.draigon.sdf.daos;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.draigon.sdf.connection.ConnectionFactory;
import de.draigon.sdf.connection.DBConnection;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.load.DBObjectMap;
import de.draigon.sdf.objects.load.MappingField;
import de.draigon.sdf.objects.load.ResultSetWrapper;
import de.draigon.sdf.objects.load.QueryInfos;
import de.draigon.sdf.objects.load.RestrictionBuilder;
import de.draigon.sdf.util.EnumUtils;
import de.draigon.sdf.util.Loggin;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class LoadDao<T> {

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   patternObject
     *
     * @return
     */
    public List<T> perform(T patternObject, Class<?> clazz) {
        RestrictionBuilder restriction = new RestrictionBuilder(patternObject, clazz);

        QueryInfos queryInfos = QueryInfos.get(clazz);

        return this.loadFromQuery("SELECT " + queryInfos.getMapping() + " FROM "
            + queryInfos.getTableWithJoins() + restriction.getRestriction(), queryInfos);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von UUIDBelongs to
     *
     * @param   resultSet
     * @param   mappedfield
     * @param   object
     *
     * @return  Der Wert von UUIDBelongs to
     */
    private String getUUIDBelongsTo(ResultSetWrapper resultSet, MappingField mappedfield) {

        try {
            return resultSet.getString(mappedfield.getResultFullQualifiedNameReferences());
        } catch (SQLException e) {
            throw new DBException("could not find referencing UUID for object.");
        }
    }

    private Object instantiate(Class<?> clazz) {

        try {
            Object instance = clazz.newInstance();

            for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(clazz)) {

                if (field.hasEntityMapping() && field.isCascadingLoad()) {

                    switch (field.getEntityMapping()) {
                    case MANY_TO_MANY:
                    case MANY_TO_ONE:
                        field.setValue(instance, new ArrayList<Object>());

                        break;

                    default:

                        // Nothing to do here
                        break;
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new DBException("could not create instance of class '"
                + clazz + "'", e);
        }
    }

    private List<T> loadFromQuery(String query, QueryInfos infos) {
        DBConnection connection = null;

        try {
            connection = ConnectionFactory.getConnection();

            Statement statement = connection.getStatement();

            Loggin.logQuery(connection, query);

            ResultSetWrapper resultset = new ResultSetWrapper(statement.executeQuery(query));
            

            List<T> results = this.resultsToList(resultset, infos);
            resultset.close();
            statement.close();

            return results;
        } catch (SQLException e) {
            throw new DBException("error executing query: '" + query + "'", e);
        } finally {

            if (connection != null) {
                connection.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<T> resultsToList(ResultSetWrapper resultSet, QueryInfos infos) throws SQLException {
        DBObjectMap dataFromDB = new DBObjectMap();

        while (resultSet.next()) {
            this.resultToObjects(dataFromDB, resultSet, infos);
        }

        dataFromDB.transformReferences();

        List<T> all = new ArrayList<T>();

        for (Object obj : dataFromDB.getMaintypes()) {
            all.add((T) obj);
        }

        return all;
    }


    /**
     * Erstellt ein Objekt vom Typ T, und befüllt es mit den werten aus dem resultset
     *
     * @param  result
     */
    private Object resultToObject(ResultSetWrapper result, Class<?> clazz) {
        Object instance = instantiate(clazz);

        for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(clazz)) {

            if (field.hasDBMapping()) {

                try {
                    setValue(instance, result, field);

                } catch (SQLException e) {
                    throw new DBException("no databasecolumn '"
                        + field.getDBFullQualifiedName() + "' for field '" + field.getName()
                        + "' on class '" + clazz + "' of type '" + field.getClass() + "'");
                }

            }
        }

        return instance;
    }

    /**
     * Erstellt ein Objekt vom Typ T, und befüllt es mit den werten aus dem resultset
     *
     * @param  result
     */
    private void resultToObject(ResultSetWrapper result, MappingField mappingField,
        DBObjectMap dataFromDB) {


        Object instance = this.resultToObject(result, mappingField.getMappedClass());

        //DEBUG System.out.println("Referencing: " + instance.getClass() + " to " + this.getUUIDBelongsTo(result, mappingField));
        
        dataFromDB.addMappedObject(instance, mappingField,
            this.getUUIDBelongsTo(result, mappingField));
    }

    private void resultToObjects(DBObjectMap dataFromDB, ResultSetWrapper resultSet, QueryInfos infos) {

        // Mainclass
        dataFromDB.addMainObject(this.resultToObject(resultSet, infos.getMainClass()));

        // JoinedClasses
        for (MappingField mappingField : infos.getJoins()) {
            this.resultToObject(resultSet, mappingField, dataFromDB);
        }
    }

    private void setValue(Object instance, ResultSetWrapper result, ExtendedField field)
        throws SQLException {
    	
        try {
            Class<?> clazzOfField = field.getType();

            if (String.class.equals(clazzOfField)) {
                field.setValue(instance, result.getString(field.getResultFullQualifiedName()));

                return;
            }

            if (Long.class.equals(clazzOfField)) {
                field.setValue(instance, result.getLong(field.getResultFullQualifiedName()));

                return;
            }

            if (Integer.class.equals(clazzOfField)) {
                field.setValue(instance, result.getInteger(field.getResultFullQualifiedName()));

                return;
            }

            if (Boolean.class.equals(clazzOfField)) {
                field.setValue(instance, result.getBoolean(field.getResultFullQualifiedName()));

                return;
            }

            if (Date.class.equals(clazzOfField)) {
                field.setValue(instance, result.getDate(field.getResultFullQualifiedName()));

                return;
            }

            if (Float.class.equals(clazzOfField)) {
                field.setValue(instance, result.getFloat(field.getResultFullQualifiedName()));

                return;
            }

            if (Double.class.equals(clazzOfField)) {
                field.setValue(instance, result.getDouble(field.getResultFullQualifiedName()));

                return;
            }

            // Check for Enum
            if (clazzOfField.isEnum()) {
                field.setValue(instance,
                    EnumUtils.fromId(clazzOfField,
                        result.getInteger(field.getResultFullQualifiedName())));
            }
        } catch (IllegalArgumentException e) {
            throw new DBException("unable to add value from column '"
                + field.getDBFullQualifiedName() + "' to instance", e);
        }
    }
}
