package org.opentripplanner.transit.raptor.api.view;

import org.opentripplanner.transit.raptor.api.transit.RaptorTripSchedule;

public interface TransitLegView<T extends RaptorTripSchedule> {

  /**
   * Trip used for transit.
   */
  T trip();

}
