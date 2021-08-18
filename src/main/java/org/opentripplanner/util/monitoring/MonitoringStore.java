package org.opentripplanner.util.monitoring;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This supports the monitoring of various system properties, such as free memory.
 * 
 * Think of it like a logger, except that it can be read from inside the system and it supports
 * tracking max values as well as a list of notes.  The use pattern, when monitoring is expensive,
 * is to check isMonitoring before computing anything.
 * 
 * TODO: allow registering special case monitoring for complex cases like long queries.
 * 
 * @author novalis
 * 
 */
public class MonitoringStore {

    private HashSet<String> monitoring = new HashSet<String>();

    private HashMap<String, Long> longs = new HashMap<String, Long>();

    private ListMultimap<String, String> notes = LinkedListMultimap.create();

    public boolean isMonitoring(String k) {
        return monitoring.contains(k);
    }

    public synchronized void setLongMax(String k, long v) {
        if (!monitoring.contains(k))
            return;
        Long old = longs.get(k);
        if (old == null || old < v) {
            longs.put(k, v);
        }
    }
}
