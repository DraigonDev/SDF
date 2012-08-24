package de.draigon.sdf.objects.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;

import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.util.DB;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class QueryInfos {
    private String mainTable;
    private Class<?> mainClass;
    private List<MappingField> joins = new ArrayList<MappingField>();
    private Map<Class<?>, List<ExtendedField>> fields = new HashMap<Class<?>,
        List<ExtendedField>>();


    /**
     * FIXME: Javadoc einfuegen
     *
     * @param   entity
     *
     * @return
     */
    public static QueryInfos get(Class<?> entity) {
        QueryInfos infos = new QueryInfos();
        infos.setMainTable(DaoUtils.getTableName(entity));
        infos.setMainClass(entity);

        build(infos, entity);

        return infos;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von joins
     *
     * @return  Der Wert von joins
     */
    public List<MappingField> getJoins() {
        return this.joins;
    }


    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von main class
     *
     * @return  Der Wert von main class
     */
    public Class<?> getMainClass() {
        return mainClass;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von mapping
     *
     * @return  Der Wert von mapping
     */
    public String getMapping() {
        String mappings = "";

        for (Entry<Class<?>, List<ExtendedField>> classes : this.fields.entrySet()) {

            for (ExtendedField field : classes.getValue()) {

                if (mappings.length() > 0) {
                    mappings += ", ";
                }

                mappings += field.getDBFullQualifiedName() + " AS "+DB.ESCAPE+""
                    + field.getResultFullQualifiedName() + ""+DB.ESCAPE+"";
            }
        }

        for (MappingField join : this.joins) {

            if (mappings.length() > 0) {
                mappings += ", ";
            }


            mappings += join.getDBFullQualifiedNameReferenceSelect() + " AS "+DB.ESCAPE+""
                + join.getResultFullQualifiedNameReferences() + ""+DB.ESCAPE+"";
        }

        return mappings;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von table with joins
     *
     * @return  Der Wert von table with joins
     */
    public String getTableWithJoins() {
        String joins = this.buildJoins();

        return ""+DB.ESCAPE+"" + this.mainTable + ""+DB.ESCAPE+"" + ((joins.length() > 0) ? " " + joins : "");
    }

    /**
     * FIXME: Javadoc kontrollieren Setzt den neuen Wert von main class
     *
     * @param  mainClass
     */
    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    private static void build(QueryInfos infos, Class<?> entity) {

        for (ExtendedField field : DaoUtils.getAllFieldsWithSuperclasses(entity)) {

            if (field.hasDBMapping()) {
                infos.addField(field);
            }

            if (field.hasEntityMapping() && field.isCascadingLoad()) {
                infos.addJoin(field);
                build(infos, field.getMappedType());
            }
        }
    }

    private void addField(ExtendedField field) {
        List<ExtendedField> fields = this.fields.get(field.getClass());

        if (fields == null) {
            fields = new ArrayList<ExtendedField>();
            this.fields.put(field.getClass(), fields);
        }

        fields.add(field);
    }

    private void addJoin(ExtendedField field) {

        if (MappingType.MANY_TO_MANY.equals(field.getEntityMapping())) {
            String mappingTable = field.getMappingTable();
            this.joins.add(new MappingField(field, mappingTable));
        } else {
            this.joins.add(new MappingField(field));
        }
    }


    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von joins
     *
     * @return  Der Wert von joins
     */
    private String buildJoins() {
        String joinClause = "";

        for (MappingField join : joins) {

            if (joinClause.length() > 0) {
                joinClause += " ";
            }

            joinClause += join.getJoin();
        }

        return joinClause;
    }


    private void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }
}
