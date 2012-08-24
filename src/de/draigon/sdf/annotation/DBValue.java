package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation defines, as what a enumvalue is mapped inside the database. Enums always are
 * mapped as Integers, so the enumfield inside the database is int
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBValue {

    /**
     * The mapping-value of the enum-konstant to database
     * @return mappingvalue
     */
    int value();
}
