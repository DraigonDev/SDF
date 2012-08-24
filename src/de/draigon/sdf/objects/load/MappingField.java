package de.draigon.sdf.objects.load;

import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.util.DB;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class MappingField {
    private String references;
    private String referenced;
    private MappingType type;
    private String mappingTable;
    private ExtendedField field;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von Link.
     *
     * @param  a
     * @param  b
     * @param  type
     */
    public MappingField(ExtendedField field) {
        this(field, null);

        if (MappingType.MANY_TO_MANY.equals(type)) {
            throw new IllegalArgumentException(
                "for Mappingtype MANY_TO_MANY call constructor '(String references, String referenced, MappingType type, String mappingTable)'");
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von Join.
     *
     * @param  references
     * @param  referenced
     * @param  type
     * @param  mappingTable
     */
    public MappingField(ExtendedField field, String mappingTable) {

        this.field = field;
        this.references = DaoUtils.getTableName(field.getParentClass());
        this.referenced = DaoUtils.getTableName(field.getMappedType());
        
        this.mappingTable = mappingTable;
        this.type = field.getEntityMapping();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von DBFull qualified name referencec
     *
     * @return  Der Wert von DBFull qualified name referencec
     */
    public String getDBFullQualifiedNameReference() {

        switch (this.field.getEntityMapping()) {
        case MANY_TO_MANY:
            return ""+DB.ESCAPE+"" + this.mappingTable + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.references + "_UUID"+DB.ESCAPE+"";

        case MANY_TO_ONE:
            return ""+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.references + "_UUID"+DB.ESCAPE+"";

        case ONE_TO_MANY:
            return ""+DB.ESCAPE+"" + this.references + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.referenced + "_UUID"+DB.ESCAPE+"";    
            
        // Fieldnam on referenced.referencs.uuid
        case ONE_TO_ONE:
            return ""+DB.ESCAPE+"" + this.references + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+"";
                // fildname on referebced.uuid

        default:
            throw new IllegalArgumentException("should never happen");
        }
    }
    
    public String getDBFullQualifiedNameReferenceSelect() {

        switch (this.field.getEntityMapping()) {
        case ONE_TO_MANY:
            return ""+DB.ESCAPE+"" + this.references + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+"";    
            
        default:
            return getDBFullQualifiedNameReference();
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von field
     *
     * @return  Der Wert von field
     */
    public ExtendedField getField() {
        return field;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von field name
     *
     * @return  Der Wert von field name
     */
    public String getFieldName() {
        return this.field.getName();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von linking
     *
     * @return  Der Wert von linking
     */
    public String getJoin() {

        switch (this.type) {
        case ONE_TO_ONE:
            return "LEFT OUTER JOIN "+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+" ON "+DB.ESCAPE+"" + this.referenced
                + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = "+DB.ESCAPE+"" + this.references + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+"";
        case MANY_TO_ONE:
            return "LEFT OUTER JOIN "+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+" ON "+DB.ESCAPE+"" + this.references
                + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = "+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.references + "_UUID"+DB.ESCAPE+"";
        case ONE_TO_MANY:
            return "LEFT OUTER JOIN "+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+" ON "+DB.ESCAPE+"" + this.referenced
                + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = "+DB.ESCAPE+"" + this.references + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.referenced + "_UUID"+DB.ESCAPE+"";
        case MANY_TO_MANY:
            return "LEFT OUTER JOIN "+DB.ESCAPE+"" + this.mappingTable + ""+DB.ESCAPE+" ON "+DB.ESCAPE+"" + this.references
                + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = "+DB.ESCAPE+"" + this.mappingTable + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.references + "_UUID"+DB.ESCAPE+""
                + " LEFT OUTER JOIN "+DB.ESCAPE+"" + this.referenced + ""+DB.ESCAPE+" ON "+DB.ESCAPE+"" + this.referenced
                + ""+DB.ESCAPE+"."+DB.ESCAPE+"UUID"+DB.ESCAPE+" = "+DB.ESCAPE+"" + this.mappingTable + ""+DB.ESCAPE+"."+DB.ESCAPE+"" + this.referenced + "_UUID"+DB.ESCAPE+"";
        default:
            throw new IllegalArgumentException("can not happen");
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mainobject class
     *
     * @return  Der Wert von mainobject class
     */
    public Class<?> getMainobjectClass() {
        return this.field.getMainObjectClass();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapped class
     *
     * @return  Der Wert von mapped class
     */
    public Class<?> getMappedClass() {
        return this.field.getType();
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von result full qualified name references
     *
     * @return  Der Wert von result full qualified name references
     */
    public String getResultFullQualifiedNameReferences() {

        switch (this.field.getEntityMapping()) {
        case MANY_TO_MANY:
            return this.mappingTable + "_" + this.references + "_UUID";

        case MANY_TO_ONE:
            return this.referenced + "_" + this.references + "_UUID";

        case ONE_TO_MANY:
            return this.references + "_" + this.referenced + "_UUID";

            
        // Fieldnam on referenced.referencs.uuid
        case ONE_TO_ONE:
            return this.references + "_UUID";
                // fildname on referebced.uuid

        default:
            throw new IllegalArgumentException("should never happen");
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von type
     *
     * @return  Der Wert von type
     */
    public MappingType getType() {
        return this.type;
    }

}
