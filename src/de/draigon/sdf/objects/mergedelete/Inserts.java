package de.draigon.sdf.objects.mergedelete;

/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class Inserts {

    /** FIXME: Javadoc einfuegen */
    String list = "";

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  object
     */
    public void add(String object) {

        if (!list.isEmpty()) {
            list += ", ";
        }

        list += object;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von list
     *
     * @return  Der Wert von list
     */
    public String getList() {
        return list;
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von empty
     *
     * @return  Der Wert von empty
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}
