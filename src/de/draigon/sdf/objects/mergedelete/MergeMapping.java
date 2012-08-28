package de.draigon.sdf.objects.mergedelete;

import de.draigon.sdf.annotation.ManyToMany;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;


/**
 * Mappinginformtions for {@link ManyToMany} mapping-realation
 *
 * @author Draigon Development
 * @version 1.0
 */
public class MergeMapping {

    /** object that maps */
    Object objectFrom;

    /** object that is mapped */
    Object objectTo;
    private MappingType mappingType;

    /** tablename where relation is stored (in n/m = defined name, in mTO1 = table that is mapped)*/
    private String mappingTable;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von MergeMapping.
     *
     * @param  objectFrom object that maps
     * @param  field field that mapped the other class
     */
    public MergeMapping(Object objectFrom, ExtendedField field) {
        this.mappingType = field.getEntityMapping();
        this.objectFrom = objectFrom;

        if (isManyToMany()) {
            this.mappingTable = field.getMappingTable();
        }

        if (isManyToOne()) {
            this.mappingTable = DaoUtils.getTableName(field.getMappedType());
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von column from
     *
     * @return  Der Wert von column from
     */
    public String getColumnFrom() {
        return DaoUtils.getTableName(objectFrom.getClass()) + "_UUID";
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von column to
     *
     * @return  Der Wert von column to
     */
    public String getColumnTo() {
        return DaoUtils.getTableName(objectTo.getClass()) + "_UUID";
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapping table
     *
     * @return  Der Wert von mapping table
     */
    public String getMappingTable() {
        return mappingTable;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von value from
     *
     * @return  Der Wert von value from
     */
    public String getValueFrom() {
        return DaoUtils.getUuid(objectFrom);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von value to
     *
     * @return  Der Wert von value to
     */
    public String getValueTo() {
        return DaoUtils.getUuid(objectTo);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von many to many
     *
     * @return  Der Wert von many to many
     */
    public boolean isManyToMany() {
        return MappingType.MANY_TO_MANY.equals(mappingType);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von one to many
     *
     * @return  Der Wert von one to many
     */
    public boolean isManyToOne() {
        return MappingType.MANY_TO_ONE.equals(mappingType);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von one to many
     *
     * @return  Der Wert von one to many
     */
    public boolean isOneToMany() {
        return MappingType.ONE_TO_MANY.equals(mappingType);
    }
    
    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von one to one
     *
     * @return  Der Wert von one to one
     */
    public boolean isOneToOne() {
        return MappingType.ONE_TO_ONE.equals(mappingType);
    }

    /**
     * FIXME: Javadoc kontrollieren Setzt den neuen Wert von object to
     *
     * @param  objectTo
     */
    public void setObjectTo(Object objectTo) {
        this.objectTo = objectTo;
    }
}
