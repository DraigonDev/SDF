package de.draigon.sdf.objects.load;

/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class EntityUniqueIdentifier {
    private Class<?> clazz;
    private String uuid;

    
    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von BelongsTo.
     *
     * @param  clazz
     * @param  uuid
     */
    public EntityUniqueIdentifier(Class<?> clazz, String uuid) {

        if (clazz == null) {
            throw new IllegalArgumentException("clazz musnt be null");
        }

        if (uuid == null) {
            throw new IllegalArgumentException("uuid musnt be null");
        }

        this.clazz = clazz;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        EntityUniqueIdentifier other = (EntityUniqueIdentifier) obj;

        if (clazz == null) {

            if (other.clazz != null)
                return false;
        } else if (!clazz.equals(other.clazz))
            return false;

        if (uuid == null) {

            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;

        return true;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von clazz
     *
     * @return  Der Wert von clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von uuid
     *
     * @return  Der Wert von uuid
     */
    public String getUuid() {
        return uuid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return "EntityUniqueIdentifier [clazz=" + clazz + ", uuid=" + uuid
                + "]";
    }


}
