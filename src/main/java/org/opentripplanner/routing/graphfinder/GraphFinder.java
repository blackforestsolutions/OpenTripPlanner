package org.opentripplanner.routing.graphfinder;

import org.opentripplanner.routing.graph.Graph;

import java.util.List;

/**
 * Common interface between different types of GraphFinders, currently two types exist, one which
 * traverses the street network, and one which doesn't.
 */
public interface GraphFinder {

  /**
   * Search closest stops from a given coordinate, extending up to a specified max radius.
   *
   * @param lat Origin latitude
   * @param lon Origin longitude
   * @param radiusMeters Search radius from the origin in meters
   */
  List<StopAtDistance> findClosestStops(double lat, double lon, double radiusMeters);

  /**
   * Get a new GraphFinder instance depending on whether the graph includes a street network or not.
   */
  static GraphFinder getInstance(Graph graph) {
    return graph.hasStreets ? new StreetGraphFinder(graph) : new DirectGraphFinder(graph);
  }
}
