package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation maps a field to a specified databasecolumn.
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBColumn {

    /**
     * The name of the databasecolumn, this field is mapped to.
     * @return The name of the databasecolumn.
     */
    String value();
}
