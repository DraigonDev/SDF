package de.draigon.sdf.objects;

import de.draigon.sdf.annotation.ManyToMany;
import de.draigon.sdf.annotation.ManyToOne;
import de.draigon.sdf.annotation.OneToMany;
import de.draigon.sdf.annotation.OneToOne;

/**
 * enum for relationtypes
 *
 * @author Draigon Development
 * @version 1.0
 */
public enum MappingType {
	/** relation mypped by {@link OneToOne} */
    ONE_TO_ONE, 
	/** relation mypped by {@link ManyToOne} */
    MANY_TO_ONE, 
	/** relation mypped by {@link ManyToMany} */
    MANY_TO_MANY, 
	/** relation mypped by {@link OneToMany} */
    ONE_TO_MANY;
}
