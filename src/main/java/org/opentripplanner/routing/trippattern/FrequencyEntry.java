package org.opentripplanner.routing.trippattern;

import org.opentripplanner.common.MavenVersion;
import org.opentripplanner.model.Frequency;

import java.io.Serializable;

import static org.opentripplanner.routing.trippattern.TripTimes.formatSeconds;

/**
 * Uses a TripTimes to represent multiple trips following the same template at regular intervals.
 * (see GTFS frequencies.txt)
 */
public class FrequencyEntry implements Serializable {

    private static final long serialVersionUID = MavenVersion.VERSION.getUID();

    public final int startTime; // sec after midnight
    public final int endTime;   // sec after midnight
    public final int headway;   // sec
    public final boolean exactTimes;
    public final TripTimes tripTimes;

    public FrequencyEntry(Frequency freq, TripTimes tripTimes) {
        this.startTime  = freq.getStartTime();
        this.endTime    = freq.getEndTime();
        this.headway    = freq.getHeadwaySecs();
        this.exactTimes = freq.getExactTimes() != 0;
        this.tripTimes  = tripTimes;
    }

    /*
        The TripTimes getDepartureTime / getArrivalTime methods do not care when the search is happening.
        The Frequency equivalents need to know when the search is happening, and need to be able to say
        no trip is possible. Therefore we need to add another specialized method.

        Fortunately all uses of the TripTimes itself in traversing edges use relative times,
        so we can fall back on the underlying TripTimes.
     */

    @Override
    public String toString() {
        return String.format("FreqEntry: trip %s start %s end %s headway %s", tripTimes.trip, formatSeconds(startTime), formatSeconds(endTime), formatSeconds(headway));
    }

    /** @return the minimum time in seconds since midnight at which a trip may depart on this frequency definition. */
    public int getMinDeparture() {
        // this is simple: the earliest this trip could depart is the time at which it starts plus the dwell at the first stop
        return tripTimes.getDepartureTime(0) - tripTimes.getArrivalTime(0) + startTime;
    }

    /** @return the maximum time in seconds since midnight at which a trip may arrive on this frequency definition. */
    public int getMaxArrival() {
        // The latest this trip could arrive is its last arrival time minus its first arrival time (the length of the trip),
        // plus the end time (the latest it could have arrived at the initial stop)
        return tripTimes.getArrivalTime(tripTimes.getNumStops() - 1) - tripTimes.getArrivalTime(0) + endTime;
    }

}
