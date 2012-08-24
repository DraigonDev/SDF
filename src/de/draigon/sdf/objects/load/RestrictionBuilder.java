package de.draigon.sdf.objects.load;

import java.util.Date;
import java.util.List;

import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.util.EnumUtils;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class RestrictionBuilder {

    /** FIXME: Javadoc einfuegen */
    Restriction restriction;

    /**
     * FIXME: Javadoc kontrollieren Erstellt eine neue Instanz von RestrictionBuilder.
     *
     * @param  patternObject
     * @param  clazz
     */
    public RestrictionBuilder(Object patternObject, Class<?> clazz) {
        restriction = buildRestriction(patternObject, clazz);
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von restriction
     *
     * @return  Der Wert von restriction
     */
    public String getRestriction() {
        return restriction.getRestriction();
    }

    @SuppressWarnings("unchecked")
    private void addEntityRestrictions(Restriction query, ExtendedField field,
        Object patternObject) {

        Restriction subPart = null;

        if (field.isMappedAsList()) {
            List<Object> liste = (List<Object>) field.getValue(patternObject);

            if (liste != null) {
                subPart = new Restriction();

                for (Object object : liste) {
                    Restriction restriction = this.buildRestriction(object, field.getType());

                    if (!restriction.isEmpty()) {
                        subPart.addOr();
                        subPart.add(restriction);
                    }

                }


            }
        } else {
            subPart = this.buildRestriction(field.getValue(patternObject), field.getType());
        }

        if (subPart != null && !subPart.isEmpty()) {
            query.addAnd();
            query.add(subPart);
        }
    }

    private Restriction buildRestriction(Object patternObject, Class<?> clazz) {
        Restriction query = new Restriction();

        if (patternObject != null) {

            for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(clazz)) {

                if (field.hasDBMapping()) {
                    Object value = field.getValue(patternObject);

                    if (value != null) {
                        query.addAnd();
                        query.add(whereRestriction(field.getDBFullQualifiedName(), value));
                    }
                }

                if (field.hasEntityMapping()) {
                    this.addEntityRestrictions(query, field, patternObject);
                }
            }
        }

        return query;
    }

    private String whereRestriction(String dbColumnName, Object value) {

        if (value instanceof Date) {
            return dbColumnName + "='" + DaoUtils.DATE_FORMATTER.format(value)
                + "'";
        } else if (value instanceof String) {
            return dbColumnName + " LIKE '" + value + "'";
        } else if (value.getClass().isEnum()) {
            return dbColumnName + "='" + EnumUtils.getId(value) + "'";
        } else {
            value = value.toString()
                .replaceAll("'", "''");

            return dbColumnName + "='" + value + "'";
        }
    }
}
