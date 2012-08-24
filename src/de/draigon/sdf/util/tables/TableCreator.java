package de.draigon.sdf.util.tables;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.draigon.sdf.connection.ConnectionFactory;
import de.draigon.sdf.connection.DBConnection;
import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.exception.DBException;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.util.DB;
import de.draigon.sdf.util.Loggin;

public class TableCreator {

    public static void main(String[] args) throws ClassNotFoundException,
            IOException {
        Loggin.SHOW_QUERY = true;
        create();
    }

    public static void create() {
        Map<Class<?>, List<Column>> entities = new HashMap<Class<?>, List<Column>>();
        List<ExtendedField> manyToMany = new ArrayList<ExtendedField>();

        for (Class<?> clazz : new EntityReader().getEntities()) {
            for (ExtendedField field : DaoUtils
                    .getAllFieldsWithSuperclasses(clazz)) {
                if (field.hasDBMapping()) {
                    if (!entities.containsKey(clazz)) {
                        entities.put(clazz, new ArrayList<Column>());
                    }
                    entities.get(clazz).add(new Column(field));
                }
                if (field.hasEntityMapping()) {
                    if (MappingType.MANY_TO_ONE
                            .equals(field.getEntityMapping())) {
                        if (!entities.containsKey(field.getType())) {
                            entities.put(field.getType(),
                                    new ArrayList<Column>());
                        }
                        entities.get(field.getType()).add(new Column(field));
                    }
                    if (MappingType.MANY_TO_MANY.equals(field
                            .getEntityMapping())) {
                        manyToMany.add(field);
                    }
                    if (MappingType.ONE_TO_MANY
                            .equals(field.getEntityMapping())) {
                        if (!entities.containsKey(clazz)) {
                            entities.put(clazz,
                                    new ArrayList<Column>());
                        }
                        entities.get(clazz).add(new Column(field));
                    }
                    // No need for one to one
                }
            }
        }
        ;

        DBConnection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.startTransaction();

            List<String> tables = getExistingTable(connection);

            insertTables(connection, entities, tables);
            insertMappingtables(connection, manyToMany, tables);

            connection.commitTransaction();
            connection.close();
        } catch (SQLException e) {
            try{
                connection.rollback();
                connection.close();
            }catch (Exception dumb) {
                
            }
            
            throw new DBException("unable to create tables", e);
        }
    }

    private static void insertMappingtables(DBConnection connection, List<ExtendedField> manyToMany, List<String> tables) throws SQLException {
        for (ExtendedField mapTab : manyToMany) {
            String mapTabName = mapTab.getMappingTable();
            
            Column columnTabA = buildMappingColumn(DaoUtils.getTableName(mapTab.getType()), mapTabName);
            Column columnTabB = buildMappingColumn(mapTab.getTableName(), mapTabName);
            
            if(!tables.contains(mapTabName.toUpperCase())){
                String query = "CREATE TABLE " + DB.ESCAPE + mapTabName + DB.ESCAPE
                    + " ("+columnTabA.getCreateStatement()+","+columnTabB.getCreateStatement()+")";
                Loggin.logQuery(connection, query);
        
                Statement statement = connection.getStatement();
                statement.executeUpdate(query);
                statement.close();
            }else{
                List<ColumnInfo> existing = getColumnsOnTable(connection, mapTabName);

                columnTabA.setExistings(existing);
                if (columnTabA.isAlterNeeded()) {
                    String query = columnTabA.getAlterStatement();
                    Loggin.logQuery(connection, query);
        
                    Statement statement = connection.getStatement();
                    statement.executeUpdate(query);
                    statement.close();
                }
                
                columnTabB.setExistings(existing);
                if (columnTabB.isAlterNeeded()) {
                    String query = columnTabB.getAlterStatement();
                    Loggin.logQuery(connection, query);
        
                    Statement statement = connection.getStatement();
                    statement.executeUpdate(query);
                    statement.close();
                }
            }
        }

    }

    private static Column buildMappingColumn(String forTabName, String mapTabName) {
        ColumnInfo info = new ColumnInfo(forTabName + "_UUID", "VARCHAR", 36);
        return new Column(info, mapTabName);
    }

    private static void insertTables(DBConnection connection,
            Map<Class<?>, List<Column>> entities, List<String> tables)
            throws SQLException {
        for (Entry<Class<?>, List<Column>> entity : entities.entrySet()) {
            // create table if not exists
            createTable(connection, entity.getKey(), tables);
            ;

            // get List of columns of existing table
            List<ColumnInfo> existing = getColumnsOnTable(connection, DaoUtils.getTableName(entity
                    .getKey()));

            for (Column c : entity.getValue()) {
                c.setExistings(existing);
                if (c.isAlterNeeded()) {
                    String query = c.getAlterStatement();
                    Loggin.logQuery(connection, query);
        
                    Statement statement = connection.getStatement();
                    statement.executeUpdate(query);
                    statement.close();
                }
            }
        }
    }

    private static List<ColumnInfo> getColumnsOnTable(DBConnection connection,
            String tableName) throws SQLException {
        List<ColumnInfo> infos = new ArrayList<ColumnInfo>();
        
        DatabaseMetaData dbmd = connection.getMetaData();

        // Specify the type of object; in this case we want columns
        ResultSet resultSet = dbmd.getColumns(null, null, tableName.toUpperCase(), null);

        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String type = resultSet.getString("TYPE_NAME");
            Integer length = resultSet.getInt("COLUMN_SIZE");

            infos.add(new ColumnInfo(name, type, length));
        }

        resultSet.close();        
        
        return infos;
    }

    private static List<String> getExistingTable(DBConnection connection)
            throws SQLException {
        List<String> infos = new ArrayList<String>();
        
        DatabaseMetaData dbmd = connection.getMetaData();

        // Specify the type of object; in this case we want tables
        String[] types = { "TABLE" };
        ResultSet resultSet = dbmd.getTables(null, null, "%", types);

        while (resultSet.next()) {
            infos.add(resultSet.getString(3).toUpperCase());
        }

        resultSet.close();

        return infos;
    }

    private static void createTable(DBConnection connection, Class<?> clazz,
            List<String> tables) throws SQLException {
        String tableName = DaoUtils.getTableName(clazz);
        if (!tables.contains(tableName.toUpperCase())) {
            String query = "CREATE TABLE " + DB.ESCAPE + tableName + DB.ESCAPE
                    + " (UUID VARCHAR(36))";
            Loggin.logQuery(connection, query);

            Statement statement = connection.getStatement();
            statement.executeUpdate(query);
            statement.close();
        }
    }

}
