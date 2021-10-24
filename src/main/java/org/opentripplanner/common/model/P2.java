package org.opentripplanner.common.model;

/**
 * An ordered pair of objects of the same type
 *
 * @param <E>
 */
public class P2<E> extends T2<E, E> {

    private static final long serialVersionUID = 1L;

    public P2(E first, E second) {
        super(first, second);
    }


    public String toString() {
        return "P2(" + first + ", " + second + ")";
    }
}
