package de.draigon.sdf.objects.load;

import de.draigon.sdf.Entity;

/**
 * Container r a relation between two {@link Entity}s mapped by their uuid.
 *
 * @author
 */
public class Relation {
    private EntityUniqueIdentifier uuidOfObject;
    private EntityUniqueIdentifier uuidBelongsTo;
    private String fieldName;
    private MappingField mappedField;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von Relation.
     *
     * @param  uuid
     * @param  uuidBelongsTo2
     * @param  mappedfield
     */
    public Relation(EntityUniqueIdentifier uuidOfObject, EntityUniqueIdentifier uuidBelongsTo,
        MappingField mappedfield) {

        if (uuidOfObject == null) {
            throw new IllegalArgumentException("uuid musnt be null");
        }

        if (uuidBelongsTo == null) {
            throw new IllegalArgumentException("uuidBelongsTo musnt be null");
        }

        if (mappedfield == null) {
            throw new IllegalArgumentException("mappedfield musnt be null");
        }

        this.uuidOfObject = uuidOfObject;
        this.uuidBelongsTo = uuidBelongsTo;
        this.fieldName = mappedfield.getFieldName();
        this.mappedField = mappedfield;
    }
    

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von field name
     *
     * @return  Der Wert von field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapped field
     *
     * @return  Der Wert von mapped field
     */
    public MappingField getMappedField() {
        return mappedField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        Relation other = (Relation) obj;

        if (fieldName == null) {

            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;

        if (uuidBelongsTo == null) {

            if (other.uuidBelongsTo != null)
                return false;
        } else if (!uuidBelongsTo.equals(other.uuidBelongsTo))
            return false;

        if (uuidOfObject == null) {

            if (other.uuidOfObject != null)
                return false;
        } else if (!uuidOfObject.equals(other.uuidOfObject))
            return false;

        return true;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von uuid belongs to
     *
     * @return  Der Wert von uuid belongs to
     */
    public EntityUniqueIdentifier getUuidBelongsTo() {
        return uuidBelongsTo;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von uuid of object
     *
     * @return  Der Wert von uuid of object
     */
    public EntityUniqueIdentifier getUuidOfObject() {
        return uuidOfObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((uuidBelongsTo == null) ? 0 : uuidBelongsTo.hashCode());
        result = prime * result + ((uuidOfObject == null) ? 0 : uuidOfObject.hashCode());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Relation [fieldName=" + fieldName + ", uuidBelongsTo=" + uuidBelongsTo
            + ", uuidOfObject=" + uuidOfObject + "]";
    }


}
