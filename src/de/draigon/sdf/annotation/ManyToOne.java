package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.draigon.sdf.Entity;


/**
 * This annotation marks a field to be mapped as OneToMany relation inside the database. Only {@link
 * Entity} can be mapped. The table, mapped as "many" needs a column, <Table1>_UUID defined as
 * varchar(36); For Cascadings also see {@link Cascade}, {@link CascadeLoad}, {@link CascadeDelete},
 * {@link CascadeMerge}.
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {

}
