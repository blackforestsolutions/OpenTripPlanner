package org.opentripplanner.routing.algorithm.raptor.transit.request;

import com.conveyal.r5.otp2.api.transit.TripPatternInfo;
import org.opentripplanner.routing.algorithm.raptor.transit.TripPattern;
import org.opentripplanner.routing.algorithm.raptor.transit.TripSchedule;

import java.util.List;

/**
 * A collection of all the TripSchedules active on a range of consecutive days. The outer list of tripSchedules
 * refers to days in order.
 */
public class TripPatternForDates implements TripPatternInfo<TripSchedule> {
    // TODO - This fails for when switching between summer/winter time
    private static final int SECONDS_OF_DAY = 86400;

    private final TripPattern tripPattern;

    private final List<List<TripSchedule>> tripSchedules;

    private final int numberOfTripPatterns;

    TripPatternForDates(TripPattern tripPattern, List<List<TripSchedule>> tripSchedulesPerDay) {
        this.tripPattern = tripPattern;
        this.tripSchedules = tripSchedulesPerDay;
        this.numberOfTripPatterns = tripSchedules.stream().mapToInt(List::size).sum();
    }

    public TripPattern getTripPattern() {
        return tripPattern;
    }

    @Override public int stopIndex(int stopPositionInPattern) {
        return this.tripPattern.stopIndex(stopPositionInPattern);
    }

    @Override public int numberOfStopsInPattern() {
        return tripPattern.getStopIndexes().length;
    }

    List<List<TripSchedule>> getTripSchedules() {
        return this.tripSchedules;
    }

    @Override public TripSchedule getTripSchedule(int index) {
        int dayOffset = -1; // Start at yesterday to account for trips that cross midnight.
        for (List<TripSchedule> tripScheduleList : tripSchedules) {
            if (index < tripScheduleList.size()) {
                return new TripScheduleWithOffset(
                        tripScheduleList.get(index),dayOffset * SECONDS_OF_DAY
                );
            }
            index -= tripScheduleList.size();
            dayOffset++;
        }
        throw new IndexOutOfBoundsException("Index out of bound: " + index);
    }

    @Override public int numberOfTripSchedules() {
        return numberOfTripPatterns;
    }
}
