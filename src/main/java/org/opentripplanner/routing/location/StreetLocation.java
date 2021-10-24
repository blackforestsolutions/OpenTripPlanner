package org.opentripplanner.routing.location;

import org.locationtech.jts.geom.Coordinate;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.opentripplanner.util.I18NString;

/**
 * Represents a location on a street, somewhere between the two corners. This is used when computing the first and last segments of a trip, for trips
 * that start or end between two intersections. Also for situating bus stops in the middle of street segments.
 */
public class StreetLocation extends StreetVertex {
    private boolean wheelchairAccessible;

    // maybe name should just be pulled from street being split
    public StreetLocation(String id, Coordinate nearestPoint, I18NString name) {
        // calling constructor with null graph means this vertex is temporary
        super(null, id, nearestPoint.x, nearestPoint.y, name);
    }

    private static final long serialVersionUID = 1L;

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    /**
     * FOR TESTING
     * @return
     */
    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public boolean equals(Object o) {
        if (o instanceof StreetLocation) {
            StreetLocation other = (StreetLocation) o;
            return other.getCoordinate().equals(getCoordinate());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getCoordinate().hashCode();
    }
}
