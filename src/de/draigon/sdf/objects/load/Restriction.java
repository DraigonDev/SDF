package de.draigon.sdf.objects.load;


/**
 * FIXME: Javadoc einfuegen
 *
 * @author
 */
public class Restriction {
    private String restriction = "";

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  whereRestriction
     */
    public void add(String whereRestriction) {
        this.restriction += whereRestriction;
    }

    /**
     * FIXME: Javadoc einfuegen
     *
     * @param  restriction
     */
    public void add(Restriction restriction) {
        this.add(restriction.getPart());
    }

    /**
     * FIXME: Javadoc einfuegen
     */
    public void addAnd() {

        if (!restriction.isEmpty()) {
            restriction += " AND ";
        }
    }

    /**
     * FIXME: Javadoc einfuegen
     */
    public void addOr() {

        if (!restriction.isEmpty()) {
            restriction += " OR ";
        }
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von part
     *
     * @return  Der Wert von part
     */
    public String getPart() {
        return isEmpty() ? "" : ("(" + this.restriction + ")");
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von restriction
     *
     * @return  Der Wert von restriction
     */
    public String getRestriction() {
        return !isEmpty() ? (" WHERE " + restriction) : "";
    }

    /**
     * FIXME: Javadoc kontrollieren Liefert den Wert von empty
     *
     * @return  Der Wert von empty
     */
    public boolean isEmpty() {
        return restriction.isEmpty();
    }
}
