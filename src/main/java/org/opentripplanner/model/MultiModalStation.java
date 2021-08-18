package org.opentripplanner.model;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * The next level grouping of stops above Station. Equivalent to NeTEx multimodal StopPlace. As
 * a Station (NeTEx StopPlace) only supports a single transit mode, you are required to group
 * several Stations together using a MultiModalStation in order to support several modes. This
 * entity is not part of GTFS.
 */
public class MultiModalStation extends TransitEntity<FeedScopedId> implements StopCollection {
    private static final long serialVersionUID = 1L;

    private FeedScopedId id;

    private Collection<Station> childStations;

    private String name;

    private WgsCoordinate coordinate;

    private String code;

    private String description;

    private String url;

    @Override
    public FeedScopedId getId() {
        return id;
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

    public Collection<Station> getChildStations() {
        return this.childStations;
    }

    @Override
    public String toString() {
        return "<MultiModal station " + this.id + ">";
    }
}
