package org.opentripplanner.transit.raptor._shared;

import org.opentripplanner.transit.raptor.api.view.ArrivalView;
import org.opentripplanner.transit.raptor.api.view.TransitLegView;

public class Bus extends AbstractStopArrival implements TransitLegView<TestRaptorTripSchedule> {
    private final TestRaptorTripSchedule trip;

    public Bus(
            int round,
            int stop,
            int arrivalTime,
            TestRaptorTripSchedule trip,
            ArrivalView<TestRaptorTripSchedule> previous
    ) {
        super(round, stop, arrivalTime, 1000, previous);
        this.trip = trip;
    }
    @Override public boolean arrivedByTransit() { return true; }
    @Override public TransitLegView<TestRaptorTripSchedule> transitLeg() { return this; }
    @Override public TestRaptorTripSchedule trip() { return trip; }
}
