package org.opentripplanner.transit.raptor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * This class is used to collect data and print a summary after some period of time.
 *
 * <pre>
 *       METHOD CALLS DURATION |              SUCCESS               |         FAILURE
 *                             |  Min   Max  Avg     Count   Total  | Average  Count   Total
 *       AvgTimer:main t1      | 1345  3715 2592 ms     50  129,6 s | 2843 ms      5   14,2 s
 *       AvgTimer:main t2      |   45   699  388 ms     55   21,4 s |    0 ms      0    0,0 s
 *       AvgTimer:main t3      |    4   692  375 ms    110   41,3 s |    0 ms      0    0,0 s
 * </pre>
 */
public abstract class AvgTimer {
    private static boolean NOOP = true;
    private static final String RESULT_TABLE_TITLE = "METHOD CALLS DURATION";

    /**
     * Keep a list of methods in the order they are added, so that we can list all timers in the same
     * order for printing at the end of the program. This more or less will resemble the call stack.
     */
    private static List<String> methods = new ArrayList<>();
    private static Map<String, AvgTimer> allTimers = new HashMap<>();


    protected final String method;
    private long startTime = 0;
    private long lapTime = 0;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = -1;
    private long totalTimeSuccess = 0;
    private long totalTimeFailed = 0;
    private int counterSuccess = 0;
    private int counterFailed = 0;


    private AvgTimer(String method) {
        this.method = method;
    }

    /**
     * @param method Use: <SimpleClassName>:<methodName>
     */
    public static AvgTimer timerMilliSec(final String method) {
        return timer(method, AvgTimerMilliSec::new);
    }

    /**
     * @param method Use: <SimpleClassName>:<methodName>
     */
    public static AvgTimer timerMicroSec(final String method) {
        return timer(method, AvgTimerMicroSec::new);
    }

    /**
     * FOR TESTING
     * This method MUST be called by the main() method BEFORE any timers are created. {@link
     * AvgTimer}s are normally created when a class is loaded by the classloader; Hence to turn on
     * performance timers, this method must be called before the class is loaded.
     * <p>
     * Performance Timers are off by default, use this method to enable the timers(turn it on).
     * <p>
     * If NOT enabled a NO-OP timer is used. The JIT compiler will inline the empty (no-op) methods
     * with no overhead to the performance.
     */
    public static void enableTimers(boolean enable) {
        NOOP = !enable;
    }

    private static AvgTimer timer(final String method, final Function<String, ? extends AvgTimer> factory) {
        return allTimers.computeIfAbsent(method, methid -> {
            methods.add(methid);
            return NOOP ? new NoopAvgTimer(method) : factory.apply(methid);
        });
    }

    public static List<String> listResults() {
        final int width = Math.max(
                RESULT_TABLE_TITLE.length(),
                allTimers().mapToInt(it -> it.method.length()).max().orElse(0)
        );
        final List<String> result = new ArrayList<>();
        result.add(header1(width));
        result.add(header2(width));
        for (String method : methods) {
            AvgTimer timer = allTimers.get(method);
            if(timer.used()) {
                result.add(timer.toString(width));
            }
        }
        return result;
    }

    /**
     * FOR TESTING
     * If you want to "warm up" your code then start timing,
     * you may call this method after the warm up is done.
     */
    public static void resetAll() {
        allTimers().forEach(AvgTimer::reset);
    }

    /**
     * FOR TESTING
     */
    private void reset() {
        startTime = 0;
        lapTime = 0;
        minTime = Long.MAX_VALUE;
        maxTime = -1;
        totalTimeSuccess = 0;
        totalTimeFailed = 0;
        counterSuccess = 0;
        counterFailed = 0;
    }

    public void start() {
        startTime = currentTime();
    }

    public void stop() {
        if (startTime == 0) {
            throw new IllegalStateException("Timer not started!");
        }
        lapTime = currentTime() - startTime;
        minTime = Math.min(minTime, lapTime);
        maxTime = Math.max(maxTime, lapTime);
        if (lapTime < 1_000_000) {
            totalTimeSuccess += lapTime;
        }
        ++counterSuccess;
        startTime = 0;
    }

    /**
     * If not started, do nothing and return.
     * Else assume the request failed and collect data.
     */
    public void failIfStarted() {
        if (startTime == 0) {
            return;
        }
        lapTime = currentTime() - startTime;
        totalTimeFailed += lapTime;
        ++counterFailed;
        startTime = 0;
    }

    public void time(Runnable body) {
        try {
            start();
            body.run();
            stop();
        } finally {
            failIfStarted();
        }
    }

    /**
     * FOR TESTING
     * @return
     */
    public long lapTime() {
        return lapTime;
    }

    /**
     * FOR TESTING
     * @return
     */
    public long avgTime() {
        return average(totalTimeSuccess, counterSuccess);
    }

    /**
     * FOR TESTING
     * @return
     */
    public String totalTimeInSeconds() {
        return toSec(totalTimeSuccess + totalTimeFailed);
    }

    /**
     * FOR TESTING
     * @return
     */
    private boolean used() {
        return counterSuccess != 0 || counterFailed != 0;
    }

    /**
     * FOR TESTING
     * @param width
     * @return
     */
    private String toString(int width) {
        return formatLine(
                method,
                width,
                formatResultAvg(totalTimeSuccess, counterSuccess),
                formatResult(totalTimeFailed, counterFailed)
        );
    }

    /**
     * FOR TESTING
     * @return
     */
    private static Stream<AvgTimer> allTimers() {
        return allTimers.values().stream();
    }

    /**
     * FOR TESTING
     * @param width
     * @return
     */
    private static String header1(int width) {
        return formatLine(
                RESULT_TABLE_TITLE,
                width,
                "             SUCCESS",
                "        FAILURE"
        );
    }

    /**
     * FOR TESTING
     * @param width
     * @return
     */
    private static String header2(int width) {
        return formatLine(
                "",
                width,
                columnHeaderAvg(), columnFailureHeader()
        );
    }

    /**
     * FOR TESTING
     * @param time
     * @param count
     * @return
     */
    private String formatResultAvg(long time, int count) {
        return String.format(
                "%4s %5s %4s %s %6s %6s s",
                str(minTime()),
                str(maxTime),
                str(average(time, count)),
                unit(),
                str(count),
                toSec(time)
        );
    }

    /**
     * FOR TESTING
     * @param value
     * @return
     */
    private String str(long value) {
        return value < 10_000 ? Long.toString(value) : (value/1000) + "'";
    }

    /**
     * FOR TESTING
     * @return
     */
    private static String columnHeaderAvg() {
        return " Min   Max  Avg     Count   Total";
    }

    /**
     * FOR TESTING
     * @param time
     * @param count
     * @return
     */
    private String formatResult(long time, int count) {
        return String.format("%4d %s %6d %6s s", average(time, count), unit(), count, toSec(time));
    }

    /**
     * FOR TESTING
     * @return
     */
    private static String columnFailureHeader() {
        return "Average  Count   Total";
    }

    /**
     * FOR TESTING
     * @param label
     * @param labelWidth
     * @param column1
     * @param column2
     * @return
     */
    private static String formatLine(String label, int labelWidth, String column1, String column2) {
        return String.format("%-" + labelWidth + "s | %-35s| %-24s", label, column1, column2);
    }

    /**
     * FOR TESTING
     * @param total
     * @param count
     * @return
     */
    private static long average(long total, int count) {
        return count == 0 ? 0 : total / count;
    }

    /**
     * FOR TESTING
     * @return
     */
    private long minTime() {
        return minTime == Long.MAX_VALUE ? -1 : minTime;
    }

    abstract long currentTime();

    /**
     * FOR TESTING
     * @return
     */
    abstract String unit();

    /**
     * FOR TESTING
     * @param time
     * @return
     */
    abstract String toSec(long time);


    public static final class AvgTimerMilliSec extends AvgTimer {
        private AvgTimerMilliSec(String method) {
            super(method);
        }

        @Override
        long currentTime() {
            return System.currentTimeMillis();
        }

        @Override
        String unit() {
            return "ms";
        }

        @Override
        String toSec(long time) {
            return String.format("%.2f", time / 1000d);
        }
    }

    public static final class AvgTimerMicroSec extends AvgTimer {
        private AvgTimerMicroSec(String method) {
            super(method);
        }

        @Override
        long currentTime() {
            return System.nanoTime() / 1000L;
        }

        @Override
        String unit() {
            return "µs";
        }

        @Override
        String toSec(long time) {
            return String.format("%.2f", time / 1_000_000d);
        }
    }

    private static final class NoopAvgTimer extends AvgTimer {
        private NoopAvgTimer(String name) { super(name); }
        @Override public void start() { }
        @Override public void stop() { }
        @Override public void failIfStarted() { }
        @Override public void time(Runnable body) { body.run(); }
        @Override public long lapTime() { return 0; }
        @Override long currentTime() { return 0; }
        @Override String unit() { return "ms"; }
        @Override String toSec(long time) { return "0"; }
    }
}
