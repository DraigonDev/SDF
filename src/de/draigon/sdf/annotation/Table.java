package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.draigon.sdf.Entity;


/**
 * This annotation marks a class to be mapped to a database table. Only classes extending {@link
 * Entity} can be mapped. The table needs a field called UUID defined as varchar(36);
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * The nam of the table, this class is mapped to
     * @return tablename
     */
    String value();
}
