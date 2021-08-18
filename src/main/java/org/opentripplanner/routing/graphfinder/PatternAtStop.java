package org.opentripplanner.routing.graphfinder;

import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.TripPattern;
import org.opentripplanner.model.TripTimeShort;
import org.opentripplanner.routing.RoutingService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * A reference to a pattern at a specific stop.
 *
 * TODO Is this the right package for this?
 */
public class PatternAtStop {

  public String id;
  public Stop stop;
  public TripPattern pattern;

  public PatternAtStop(Stop stop, TripPattern pattern) {
    this.id = toId(stop, pattern);
    this.stop = stop;
    this.pattern = pattern;
  }

  /**
   * Converts the ids of the pattern and stop to an opaque id, which can be supplied to the users
   * to be used for refetching the combination.
   */
  private static String toId(Stop stop, TripPattern pattern) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(stop.getId().toString().getBytes(StandardCharsets.UTF_8)) + ";" +
        encoder.encodeToString(pattern.getId().toString().getBytes(StandardCharsets.UTF_8));
  }
}
