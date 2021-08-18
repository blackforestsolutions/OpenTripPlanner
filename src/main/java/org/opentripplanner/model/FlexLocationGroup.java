package org.opentripplanner.model;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import org.opentripplanner.common.geometry.GeometryUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * A group of stopLocations, which can share a common Stoptime
 */
public class FlexLocationGroup extends TransitEntity<FeedScopedId> implements StopLocation {

  private static final long serialVersionUID = 1L;

  private FeedScopedId id;

  private String name;

  private final Set<StopLocation> stopLocations = new HashSet<>();

  private GeometryCollection geometry = new GeometryCollection(null, GeometryUtils.getGeometryFactory());

  @Override
  public void setId(FeedScopedId id) {
    this.id = id;
  }

  @Override
  public FeedScopedId getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getCode() {
    return null;
  }

  /**
   * Returns the centroid of all stops and areas belonging to this location group.
   */
  @Override
  public WgsCoordinate getCoordinate() {
    Point centroid = geometry.getCentroid();
    return new WgsCoordinate(centroid.getY(), centroid.getX());
  }
}