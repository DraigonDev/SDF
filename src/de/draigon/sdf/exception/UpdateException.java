package de.draigon.sdf.exception;


/**
 * Exception, that is thrown if errors occur on delete or merge processes.
 *
 * @author   Draigon Development
 * @version  1.0
 */
public class UpdateException extends Exception {

    private static final long serialVersionUID = 1878567935952056098L;

    /**
     * Erstellt eine neue Instanz von MergeException.
     */
    public UpdateException() {
        super();
    }

    /**
     * Erstellt eine neue Instanz von MergeException.
     *
     * @param  arg0
     */
    public UpdateException(String arg0) {
        super(arg0);
    }

    /**
     * Erstellt eine neue Instanz von MergeException.
     *
     * @param  arg0
     */
    public UpdateException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Erstellt eine neue Instanz von MergeException.
     *
     * @param  arg0
     * @param  arg1
     */
    public UpdateException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
