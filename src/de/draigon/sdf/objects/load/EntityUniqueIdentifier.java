package de.draigon.sdf.objects.load;

/**
 * stores a relation of class to uuid as for the object relation mapping by loading the results to objects
 *
 * @author   Draigon Development
 * @version  1.0
 */
public class EntityUniqueIdentifier {
    private Class<?> clazz;
    private String uuid;

    
    /**
     * creates an instance of {@link EntityUniqueIdentifier}.
     *
     * @param  clazzhe class mapped to
     * @param  uuid the uuid mapped to this class
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
     * returns the given class
     *
     * @return  the class
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * returns the value of uuid
     *
     * @return  value of uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EntityUniqueIdentifier [clazz=" + clazz + ", uuid=" + uuid
                + "]";
    }


}
