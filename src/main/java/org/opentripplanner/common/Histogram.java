package org.opentripplanner.common;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.stream.IntStream;

/**
 * For design and debugging purposes, a simple class that tracks the frequency of different numbers.
 */
public class Histogram {

    private String title;
    private TIntIntMap bins = new TIntIntHashMap();
    private int maxBin = Integer.MIN_VALUE;
    private int minBin = Integer.MAX_VALUE;
    private long count = 0;
    private int maxVal;

    public Histogram (String title) {
        this.title = title;
    }

    public void add (int i) {
        count++;
        int binVal = bins.adjustOrPutValue(i, 1, 1);

        if (binVal > maxVal)
            maxVal = binVal;

        if (i > maxBin) {
            maxBin = i;
        }

        if (i < minBin) {
            minBin = i;
        }
    }

    public void displayHorizontal () {
        System.out.println("--- Histogram: " + title + " ---");

        // TODO: horizontal scale
        double vscale = 30d / maxVal;

        for (int i = 0; i < 30; i++) {
            StringBuilder row = new StringBuilder(maxBin - minBin + 1);

            int minValToDisplayThisRow = (int) ((30 - i) / vscale);
            for (int j = minBin; j <= maxBin; j++) {
                if (bins.get(j) > minValToDisplayThisRow)
                    row.append('#');
                else
                    row.append(' ');
            }

            System.out.println(row);
        }

        // put a mark at zero and at the ends
        if (minBin < 0 && maxBin > 0) {
            StringBuilder ticks = new StringBuilder();
            for (int i = minBin; i < 0; i++)
                ticks.append(' ');
            ticks.append('|');
            System.out.println(ticks);
        }

        StringBuilder row = new StringBuilder();
        for (int i = minBin; i < maxBin; i++) {
            row.append(' ');
        }

        String start = new Integer(minBin).toString();
        row.replace(0, start.length(), start);
        String end = new Integer(maxBin).toString();
        row.replace(row.length() - end.length(), row.length(), end);
        System.out.println(row);
    }

    public int mean() {
        long sum = 0;
        for (TIntIntIterator it = bins.iterator(); it.hasNext();) {
            it.advance();

            sum += it.key() * it.value();
        }

        return (int) (sum / count);
    }

    public static void main (String... args) {
        System.out.println("Testing histogram store with normal distribution, mean 0");
        Histogram h = new Histogram("Normal");

        MersenneTwister mt = new MersenneTwister();

        IntStream.range(0, 1000000).map(i -> (int) Math.round(mt.nextGaussian() * 20 + 2.5)).forEach(h::add);

        h.displayHorizontal();
        System.out.println("mean: " + h.mean());
    }
}
