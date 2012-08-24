package de.draigon.sdf.daos;

import java.sql.SQLException;

import java.util.List;

import de.draigon.sdf.connection.DatabaseUpdater;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.exception.UpdateException;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.mergedelete.MergeMapping;
import de.draigon.sdf.util.DB;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class DeleteDao<T> {

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   object
     *
     * @return
     *
     * @throws  UpdateException
     */
    @SuppressWarnings("unchecked")
    public T perform(T object) throws UpdateException {
        DatabaseUpdater updater = new DatabaseUpdater();

        try {
            object = (T) this.delete(updater, object);
        } catch (DBException e) {
            updater.rollback();
            throw new DBException("delete rolled back", e);
        } catch (UpdateException e) {
            updater.rollback();
            throw new UpdateException("delete is rolled back", e);
        }

        try {
            updater.commit();
        } catch (SQLException e) {
            throw new DBException("could not delete object + " + object, e);
        }

        return object;
    }

    private void clearReferences(DatabaseUpdater updater, MergeMapping mapping)
        throws UpdateException {

        if (mapping.isManyToMany()) {
            String query = "DELETE FROM "+DB.ESCAPE+"" + mapping.getMappingTable() + ""+DB.ESCAPE+" WHERE "+DB.ESCAPE+""
                + mapping.getColumnFrom() + ""+DB.ESCAPE+" = '" + mapping.getValueFrom() + "'";

            if (!updater.execute(query)) {
                throw new UpdateException("unable to insert relation to mappingtable");
            }
        } else if (mapping.isManyToOne()) {
            String query = "UPDATE "+DB.ESCAPE+"" + mapping.getMappingTable() + ""+DB.ESCAPE+" SET "+DB.ESCAPE+""
                + mapping.getColumnFrom() + ""+DB.ESCAPE+" = 'NULL' WHERE "+DB.ESCAPE+"" + mapping.getColumnFrom() + ""+DB.ESCAPE+" = '"
                + mapping.getValueFrom() + "'";

            if (!updater.execute(query)) {
                throw new UpdateException("unable to insert relation to mappingtable");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object delete(DatabaseUpdater updater, Object object) throws UpdateException {

        if(object == null){
            //No entity to delete here, so abort this step.
            return object;
        }
        
        for (ExtendedField field : DaoUtils.getAllMappingsWithSuperclasses(object.getClass())) {

            if (field.isCascadingDelete()) {
                MergeMapping mapping = new MergeMapping(object, field);

                if (mapping.isManyToMany() || mapping.isManyToOne()) {
                    clearReferences(updater, mapping);

                    for (Object subObject : (List<Object>) field.getValue(object)) {
                        mapping.setObjectTo(subObject);
                        delete(updater, subObject);
                    }
                } else {
                    Object subObject = field.getValue(object);
                    delete(updater, subObject);
                }
            }
        }

        deleteSingle(updater, object);

        return object;
    }

    private Object deleteSingle(DatabaseUpdater updater, Object object) throws UpdateException {
        String uuid = DaoUtils.getUuid(object);

        if (uuid == null) {
            throw new UpdateException("no managed entity " + object);
        }

        String tableName = DaoUtils.getTableName(object.getClass());
        String delete = "DELETE FROM "+DB.ESCAPE+"" + tableName + ""+DB.ESCAPE+" WHERE "+DB.ESCAPE+"" + tableName
            + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = '" + DaoUtils.getUuid(object) + "'";

        if (!updater.execute(delete)) {
            throw new UpdateException("entity could not be deleted " + object);
        }

        DaoUtils.resetUuid(object);

        return object;

    }
}
