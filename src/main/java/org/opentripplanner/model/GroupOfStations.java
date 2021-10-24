package org.opentripplanner.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A grouping that can contain a mix of Stations and MultiModalStations. It can be used to link
 * several StopPlaces into a hub. It can be a grouping of major stops within a city or a cluster
 * of stops that naturally belong together.
 */
public class GroupOfStations extends TransitEntity<FeedScopedId> implements StopCollection {
    private static final long serialVersionUID = 1L;

    private FeedScopedId id;

    private String name;

    // TODO Map from NeTEx
    private PurposeOfGrouping purposeOfGrouping;

    private WgsCoordinate coordinate;

    private Set<StopCollection> childStations = new HashSet<>();

    @Override
    public FeedScopedId getId() {
        return id;
    }

    @Override
    public void setId(FeedScopedId id) {
        this.id = id;
    }

    public double getLat() {
        return coordinate.latitude();
    }

    public double getLon() {
        return coordinate.longitude();
    }

    public Collection<Stop> getChildStops() {
        return this.childStations.stream()
                .flatMap(s -> s.getChildStops().stream())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String toString() {
        return "<GroupOfStations " + this.id + ">";
    }

    /**
     * Categorization for the grouping
     */
    public enum PurposeOfGrouping {
        /**
         * Group of prominent stop places within a town or city(centre)
         */
        GENERALIZATION,
        /**
         * Stop places in proximity to each other which have a natural geospatial- or
         * public transport related relationship.
         */
        CLUSTER;
    }
}