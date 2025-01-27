package org.opentripplanner.transit.raptor.rangeraptor.debug;

import org.opentripplanner.transit.raptor.util.AvgTimer;

public class WorkerPerformanceTimers {
    // Variables to track time spent
    private final AvgTimer timerRoute;
    private final AvgTimer timerByMinuteScheduleSearch;
    private final AvgTimer timerByMinuteTransfers;

    public WorkerPerformanceTimers(String namePrefix) {
        timerRoute = AvgTimer.timerMilliSec(namePrefix + ":route");
        timerByMinuteScheduleSearch = AvgTimer.timerMicroSec(namePrefix + ":runRaptorForMinute Transit");
        timerByMinuteTransfers = AvgTimer.timerMicroSec(namePrefix + ":runRaptorForMinute Transfers");
    }

    public AvgTimer timerRoute() {
        return timerRoute;
    }

    public AvgTimer timerByMinuteScheduleSearch() {
        return timerByMinuteScheduleSearch;
    }

    public AvgTimer timerByMinuteTransfers() {
        return timerByMinuteTransfers;
    }
}
