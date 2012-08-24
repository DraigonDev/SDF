package de.draigon.sdf;

import java.lang.reflect.ParameterizedType;

import java.util.List;

import de.draigon.sdf.annotation.Cascade;
import de.draigon.sdf.annotation.CascadeDelete;
import de.draigon.sdf.annotation.CascadeLoad;
import de.draigon.sdf.daos.DeleteDao;
import de.draigon.sdf.daos.LoadDao;
import de.draigon.sdf.daos.MergeDao;
import de.draigon.sdf.exception.UpdateException;


/**
 * DataAccessObject to perform operations for an object on the database. You can extend this object
 * for your implementation, to get standard acess mechanism (delete, load, update, insert) of
 * databaseoperations for your entity. DAOs can only be created for classes extending the {@link
 * Entity}.
 *
 * @author   Draigon Development
 * @version  1.0
 */
public abstract class AbstractDao<T extends Entity> {
    private DeleteDao<T> deleteDao = new DeleteDao<T>();
    private LoadDao<T> loadDao = new LoadDao<T>();
    private MergeDao<T> mergeDao = new MergeDao<T>();

    /**
     * Performs a delete-action on the given object. Mappings that are annotated as {@link Cascade}
     * or {@link CascadeDelete} will be followed to delete.
     *
     * @param   object  The object to delete from database
     *
     * @return  The deleted object without referentation to the database
     *
     * @throws  UpdateException  if the object couldnt be deleted for some reason
     */
    public T delete(T object) throws UpdateException {
        return this.deleteDao.perform(object);
    }

    /**
     * Performs a load-action on the database. If the object is != null, the where-clause of the
     * select statement will be generated from any field in this object, that is not set to "null".
     * Mappings that are annotated as {@link Cascade} or {@link CascadeLoad} will be followed to
     * load.
     *
     * @param   patternObject  The pattern-object for loading restrictions
     *
     * @return  A list of found objects about the pattern
     */
    public List<T> load(T patternObject) {
        return this.loadDao.perform(patternObject, getClassOfGeneric());
    }

    /**
     * Loads all objects from the database and returns them as list.
     *
     * @return  All objects found from this entity in the database.
     */
    public List<T> loadAll() {
        return load(null);
    }

    /**
     * Performs a merge-action on the database. If the object does not exists inside the database,
     * it will be inserted. Otherwise an update will be performed. Mappings that are annotated as
     * {@link Cascade} or {@link CascadeDelete} will be followed to delete.
     *
     * @return  The merged object
     *
     * @throws  UpdateException  if the object couldnt be merged for some reason
     */
    public T merge(T object) throws UpdateException {
        return this.mergeDao.perform(object);
    }

    private Class<?> getClassOfGeneric() {
        return (Class<?>)
            ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }
}
