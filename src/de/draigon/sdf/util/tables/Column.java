package de.draigon.sdf.util.tables;

import java.util.List;

import de.draigon.sdf.daos.util.DaoUtils;
import de.draigon.sdf.objects.ExtendedField;
import de.draigon.sdf.objects.MappingType;
import de.draigon.sdf.util.DB;
import de.draigon.sdf.util.Loggin;

public class Column {

    private ColumnInfo fieldInfo;
    private String tablename;

    private Status status = null;

    public Column(ExtendedField field) {
        this.fieldInfo = new ColumnInfo(field);
        if(MappingType.MANY_TO_ONE.equals(field.getEntityMapping())){
            this.tablename = DaoUtils.getTableName(field.getType());
        }else{
            this.tablename = field.getTableName();
        }
    }
    
    public Column(ColumnInfo info, String tablename){
        this.fieldInfo = info;
        this.tablename = tablename;
    }

    public String getAlterStatement() {
        if (isAlterNeeded()) {
            switch (status) {
            case NON_EXISTENT:
                return "ALTER TABLE " + DB.ESCAPE + this.tablename + DB.ESCAPE
                        + " ADD " + this.getCreateStatement();
            case WRONG:
                break;
            default:
                break;
            }
        }
        throw new IllegalStateException(
                "no alter needed - use method isAlterNeeded() to prevent this error");
    }

    public String getName() {
        return this.fieldInfo.getName();
    }
    
    public void setExistings(List<ColumnInfo> existing) {
        for (ColumnInfo info : existing) {
            if(info.equals(this.fieldInfo)){
                status = Status.EXISTS;
                return;
            } else if(info.getName().equalsIgnoreCase(this.fieldInfo.getName())){
                Loggin.logFieldWarn("found field '" + this.fieldInfo.getName() + 
                        "' of type " +info.getType() + info.getLengthAsInsert() +   
                        ": expected "+this.fieldInfo.getType() + this.fieldInfo.getLengthAsInsert());
                status = Status.WRONG;
                return;
            }
        }
        status = Status.NON_EXISTENT;
    }

    public boolean isAlterNeeded() {
        if (status == null) {
            throw new IllegalStateException(
                    "this method must be called after setExistings()");
        }

        return Status.NON_EXISTENT.equals(status);
    }

    private enum Status {
        EXISTS, NON_EXISTENT, WRONG;
    }

    public ColumnInfo getColumnInfo() {
         return this.fieldInfo;
    }

    public String getCreateStatement() {
        return DB.ESCAPE + this.fieldInfo.getName()
        + DB.ESCAPE + " " + this.fieldInfo.getType()
        + this.fieldInfo.getLengthAsInsert() + "";
    }
}
