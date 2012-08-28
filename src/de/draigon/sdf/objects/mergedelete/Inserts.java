package de.draigon.sdf.objects.mergedelete;

/**
 * List of Insert-Strings. (Decorator for java.uti.list)
 *
 * @author Draigon Development
 * @version 1.0
 */
public class Inserts {

    /** The List */
    String list = "";

    /**
     * Adds a insert to the list
     *
     * @param  the insert to add
     */
    public void add(String object) {

        if (!list.isEmpty()) {
            list += ", ";
        }

        list += object;
    }

    /**
     * returns the list of inserts
     *
     * @return  the inserts
     */
    public String getList() {
        return list;
    }

    /**
     * getter if the list is empty
     *
     * @return  true if list is empty
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}
