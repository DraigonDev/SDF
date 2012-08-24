package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation gives the opportunity do modify the length of a field in database, if using the db-creator class.
 * Default values are
 * varchar: 50
 * 
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBFieldLength {
    
    /**
     * The length of the field in the database.
     * @return length of the field
     */
    int value();
}
