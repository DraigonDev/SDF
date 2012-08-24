package de.draigon.sdf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.draigon.sdf.Entity;


/**
 * This annotation marks a field to be mapped as ManyToMany relation inside the database. Only
 * {@link Entity} can be mapped. Fields marked with this column need a mappingtable inside the
 * database, names as the value of this annotation. This mappingtable needs 2 columns, <Table1>_UUID
 * and <Table2>_UUID, both set as varchar(36); For Cascadings also see {@link Cascade}, {@link
 * CascadeLoad}, {@link CascadeDelete}, {@link CascadeMerge}.
 *
 * @author   Draigon Development
 * @version  1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {

    /**
     * The name of the mappingtable inside the database
     * @return mappingtablename
     */
    String value();
}
