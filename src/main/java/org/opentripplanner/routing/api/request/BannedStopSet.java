package org.opentripplanner.routing.api.request;

import java.util.HashSet;

public class BannedStopSet extends HashSet<Integer> {
    private static final long serialVersionUID = 3020705171076939160L;

    public static BannedStopSet ALL = new BannedStopSet();
}
