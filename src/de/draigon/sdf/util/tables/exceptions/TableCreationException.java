package de.draigon.sdf.util.tables.exceptions;

public class TableCreationException extends RuntimeException{

    private static final long serialVersionUID = 3889323921567052369L;

    public TableCreationException() {
        super();
    }

    public TableCreationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public TableCreationException(String arg0) {
        super(arg0);
    }

    public TableCreationException(Throwable arg0) {
        super(arg0);
    }
    
}
