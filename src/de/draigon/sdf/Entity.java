package de.draigon.sdf;

import de.draigon.sdf.annotation.DBColumn;
import de.draigon.sdf.annotation.Table;


/**
 * Abstract class to defince an Entity for the {@link AbstractDao}. Any Object, defined as Entity
 * (via implementing this class) needs a {@link Table} annotation, and the referenced table need
 * minimum one column named UUID of type varchar(36). It should be declared as PrimaryKey.
 *
 * @author   Draigon Development
 * @version  1.0
 */
public abstract class Entity {

    // This field will be acessed via reflection. Its nessesary, to make databaseentries unique
    // and give them a fixed id
    @DBColumn("UUID")
    private String uuid;

    @SuppressWarnings("unused")
    private String getUuid() {
        return uuid;
    }

    @SuppressWarnings("unused")
    private void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
