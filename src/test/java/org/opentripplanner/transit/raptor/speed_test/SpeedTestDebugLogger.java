package org.opentripplanner.transit.raptor.speed_test;


import org.opentripplanner.transit.raptor.api.debug.DebugEvent;
import org.opentripplanner.transit.raptor.api.debug.DebugLogger;
import org.opentripplanner.transit.raptor.api.debug.DebugTopic;
import org.opentripplanner.transit.raptor.api.path.Path;
import org.opentripplanner.transit.raptor.api.transit.RaptorTripSchedule;
import org.opentripplanner.transit.raptor.api.view.ArrivalView;
import org.opentripplanner.transit.raptor.rangeraptor.transit.TripTimesSearch;
import org.opentripplanner.transit.raptor.util.PathStringBuilder;

import java.text.NumberFormat;
import java.util.Locale;

import static org.opentripplanner.transit.raptor.util.TimeUtils.timeToStrCompact;

class SpeedTestDebugLogger<T extends RaptorTripSchedule> implements DebugLogger {
    private static final int NOT_SET = Integer.MIN_VALUE;

    private final boolean enableDebugLogging;
    private final NumberFormat numFormat = NumberFormat.getInstance(Locale.FRANCE);

    private int lastIterationTime = NOT_SET;
    private int lastRound = NOT_SET;
    private boolean pathHeader = true;

    SpeedTestDebugLogger(boolean enableDebugLogging) {
        this.enableDebugLogging = enableDebugLogging;
    }

    void stopArrivalLister(DebugEvent<ArrivalView<T>> e) {
        System.err.println("Test");
    }

    void pathFilteringListener(DebugEvent<Path<T>> e) {
        if (pathHeader) {
            pathHeader = false;
        }
    }

    @Override
    public boolean isEnabled(DebugTopic topic) {
        return enableDebugLogging;
    }

    @Override
    public void debug(DebugTopic topic, String message) {
        if(enableDebugLogging) {
            // We log to info - since debugging is controlled by the application
            if(message.contains("\n")) {
                System.err.printf("%s\n%s", topic, message);
            }
            else {
                System.err.printf("%-16s | %s%n", topic, message);
            }
        }
    }


    /* private methods */

    private void printIterationHeader(int iterationTime) {
        if (iterationTime == lastIterationTime) return;
        lastIterationTime = iterationTime;
        lastRound = NOT_SET;
        pathHeader = true;
        System.err.println("\n**  RUN RAPTOR FOR MINUTE: " + timeToStrCompact(iterationTime) + "  **");
    }

    private void print(ArrivalView<?> a, String action, String optReason) {
        String pattern = a.arrivedByTransit() ? a.transitLeg().trip().pattern().debugInfo() : "";
    }

    private static String details(String action, String optReason, String element) {
        return concat(optReason,  action + "ed element: " + element);
    }

    private static String path(ArrivalView<?> a) {
        return path(a, new PathStringBuilder()).toString()  + " (cost: " + a.cost() + ")";
    }

    private static PathStringBuilder path(ArrivalView<?> a, PathStringBuilder buf) {
        if (a.arrivedByAccessLeg()) {
            return buf.walk(legDuration(a)).sep().stop(a.stop());
        }
        // Recursively call this method to insert arrival in front of this arrival
        path(a.previous(), buf);

        buf.sep();

        if (a.arrivedByTransit()) {
            if(a.previous().arrivalTime() > a.arrivalTime()) {
                throw new IllegalStateException("TODO: Add support for REVERSE search!");
            }
            TripTimesSearch.BoarAlightTimes b = TripTimesSearch.findTripForwardSearch(a);
            buf.transit(a.transitLeg().trip().pattern().debugInfo(), b.boardTime, a.arrivalTime());
        } else {
            buf.walk(legDuration(a));
        }
        return buf.sep().stop(a.stop());
    }

    /**
     * The absolute time duration in seconds of a trip.
     */
    private static int legDuration(ArrivalView<?> a) {
        if(a.arrivedByAccessLeg()) {
            return a.accessLeg().access().durationInSeconds();
        }
        if(a.arrivedByTransfer()) {
            return a.transferLeg().durationInSeconds();
        }
        if(a.arrivedAtDestination()) {
            return a.egressLeg().egress().durationInSeconds();
        }
        throw new IllegalStateException("Unsuported type: " + a.getClass());
    }

    private void printRoundHeader(int round) {
        if (round == lastRound) return;
        lastRound = round;
    }

    private static String concat(String s, String t) {
        if(s == null || s.isEmpty()) {
            return t == null ? "" : t;
        }
        return s + ", " + (t == null ? "" : t);
    }

    private String legType(ArrivalView<?> a) {
        if (a.arrivedByAccessLeg()) { return "Access"; }
        if (a.arrivedByTransit()) { return "Transit"; }
        // We use Walk instead of Transfer so it is easier to distinguish from Transit
        if (a.arrivedByTransfer()) { return "Walk"; }
        if (a.arrivedAtDestination()) { return "Egress"; }
        throw new IllegalStateException("Unknown mode for: " + this);
    }
}
