package org.opentripplanner.model;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * Location corresponding to a location where riders may request pickup or drop off, defined in the
 * GTFS bundle.
 */

public class FlexStopLocation extends TransitEntity<FeedScopedId> implements StopLocation {
  private static final long serialVersionUID = 1L;

  private FeedScopedId id;

  private String name;

  private Geometry geometry;

  @Override
  public FeedScopedId getId() {
    return id;
  }

  public void setId(FeedScopedId id) {
    this.id = id;
  }

  /**
   * Defines the name of the location. The name should be the same, which is used in customer
   * communication, eg. the name of the village where the service stops.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Short text or a number that identifies the location for riders. These codes are often used in
   * phone-based reservation systems to make it easier for riders to specify a particular location.
   * The stop_code can be the same as id if it is public facing. This field should be left empty for
   * locations without a code presented to riders.
   */
  @Override
  public String getCode() {
    return null;
  }

  /**
   * Returns the centroid of this location.
   */
  @Override
  public WgsCoordinate getCoordinate() {
    Point centroid = geometry.getCentroid();
    return new WgsCoordinate(centroid.getY(), centroid.getX());
  }
}
