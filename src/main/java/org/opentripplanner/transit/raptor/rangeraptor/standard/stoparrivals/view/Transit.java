package org.opentripplanner.transit.raptor.rangeraptor.standard.stoparrivals.view;

import org.opentripplanner.transit.raptor.api.transit.RaptorTripSchedule;
import org.opentripplanner.transit.raptor.api.view.ArrivalView;
import org.opentripplanner.transit.raptor.api.view.TransitLegView;
import org.opentripplanner.transit.raptor.rangeraptor.standard.stoparrivals.StopArrivalState;

final class Transit<T extends RaptorTripSchedule>
    extends StopArrivalViewAdapter<T>
    implements TransitLegView<T>
{
    private final StopArrivalState<T> arrival;
    private final StopsCursor<T> cursor;

    Transit(int round, int stop, StopArrivalState<T> arrival, StopsCursor<T> cursor) {
        super(round, stop);
        this.arrival = arrival;
        this.cursor = cursor;
    }

    @Override
    public int arrivalTime() {
        return arrival.transitTime();
    }

    @Override
    public boolean arrivedByTransit() {
        return true;
    }

    @Override
    public TransitLegView<T> transitLeg() {
        return this;
    }

    @Override
    public T trip() {
        return arrival.trip();
    }

    @Override
    public ArrivalView<T> previous() {
        return round() == 1
                ? cursor.access(boardStop(), this)
                : cursor.stop(round()-1, boardStop());
    }

    public int boardTime() {
        return arrival.boardTime();
    }

    private int boardStop() {
        return arrival.boardStop();
    }
}
