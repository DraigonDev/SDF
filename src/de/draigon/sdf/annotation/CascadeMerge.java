package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation marks a field, to be cascaded in merge actions. It can be only used with {@link
 * ManyToMany} {@link OneToOne} or {@link ManyToOne} relations.
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CascadeMerge {

}
