package org.opentripplanner.model;

/**
 * A company which is responsible for operating public transport services.
 * The operator will often operate under contract with an Authority (Agency).
 * <p/>
 * Netex ONLY. Operators are available only if the data source is Netex, not GTFS.
 *
 * @see Agency
 */
public class Operator extends TransitEntity<FeedScopedId> {

    private static final long serialVersionUID = 1L;

    private FeedScopedId id;

    private String name;

    private String url;

    private String phone;


    @Override
    public FeedScopedId getId() {
        return id;
    }

    public void setId(FeedScopedId id) {
        this.id = id;
    }

    public String toString() {
        return "<Operator " + this.id + ">";
    }
}
