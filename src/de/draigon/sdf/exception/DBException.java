package de.draigon.sdf.exception;


/**
 * Kind of {@link RuntimeException} that is thrown if any error occurs, normally as a result of
 * wrong mapping inside the entities.
 *
 * @author   Draigon Development
 * @version  1.0
 */
public class DBException extends RuntimeException {

    private static final long serialVersionUID = 3772223097402912928L;

    /**
     * Erstellt eine neue Instanz von DBException.
     */
    public DBException() {
        super();
    }

    /**
     * Erstellt eine neue Instanz von DBException.
     *
     * @param  arg0
     */
    public DBException(String arg0) {
        super(arg0);
    }

    /**
     * Erstellt eine neue Instanz von DBException.
     *
     * @param  arg0
     */
    public DBException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Erstellt eine neue Instanz von DBException.
     *
     * @param  arg0
     * @param  arg1
     */
    public DBException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
