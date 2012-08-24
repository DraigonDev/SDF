package de.draigon.sdf.objects.load;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;

import de.draigon.sdf.daos.util.DaoUtils;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class DBObjectMap {
   /** FIXME: Javadoc einfuegen */
    Map<Relation, Object> referenceTypes = new HashMap<Relation, Object>();

    /** FIXME: Javadoc einfuegen */
    Map<EntityUniqueIdentifier, Object> maintypes = new HashMap<EntityUniqueIdentifier, Object>();

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  uuid
     * @param  object
     */
    public boolean addMainObject(Object object) {
        String uuid = DaoUtils.getUuid(object);

        if (uuid == null) {
            return false;
        }

        EntityUniqueIdentifier id = new EntityUniqueIdentifier(object.getClass(), uuid);

        if (maintypes.containsKey(id)) {
            return false;
        } else {
            maintypes.put(id, object);

            return true;
        }
    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  object
     * @param  mappingField
     * @param  uuidBelongsTo
     */
    public boolean addMappedObject(Object object, MappingField mappingField, String uuidBelongsTo) {
        String uuid = DaoUtils.getUuid(object);

        if (uuid == null) {
            return false;
        }

        EntityUniqueIdentifier me = new EntityUniqueIdentifier(object.getClass(), uuid);
        EntityUniqueIdentifier belongsTo = new EntityUniqueIdentifier(mappingField
            .getMainobjectClass(), uuidBelongsTo);
        
        Relation relation = new Relation(me, belongsTo, mappingField);

        if (referenceTypes.containsKey(relation)) {
            return false;
        } else {
            referenceTypes.put(relation, object);

            return true;
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von maintypes
     *
     * @return  Der Wert von maintypes
     */
    public Collection<Object> getMaintypes() {
        return maintypes.values();
    }

    /**
     * FIXME: Javadoc einfuegen
     */
    @SuppressWarnings("unchecked")
    public void transformReferences() {

        for (Entry<Relation, Object> entry : this.referenceTypes.entrySet()) {
            Object belongsTo = this.getObject(entry.getKey().getUuidBelongsTo());

            if (entry.getKey().getMappedField().getField().isMappedAsList()) {
                List<Object> liste = (List<Object>) entry.getKey()
                    .getMappedField()
                    .getField()
                    .getValue(belongsTo);

                liste.add(entry.getValue());
            } else {
                entry.getKey()
                    .getMappedField()
                    .getField()
                    .setValue(belongsTo, entry.getValue());
            }
        }
    }

    private Object getObject(EntityUniqueIdentifier belongsTo) {
        //System.out.println("BelongsTo: " + belongsTo);
        if (this.maintypes.containsKey(belongsTo)) {
            return this.maintypes.get(belongsTo);
        } else {

            for (Relation key : this.referenceTypes.keySet()) {
                //System.out.println(" -> " + key.getUuidOfObject());
                if (key.getUuidOfObject().equals(belongsTo)) {
                    return this.referenceTypes.get(key);
                }
            }
        }
        System.out.println("belongs to: " + belongsTo);
        throw new IllegalArgumentException(
            "cause of only grabbing existing references, it should never happen to get here");
    }
}
