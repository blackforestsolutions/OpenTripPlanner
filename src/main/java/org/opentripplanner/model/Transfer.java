/* This file is based on code copied from project OneBusAway, see the LICENSE file for further information. */
package org.opentripplanner.model;

import java.io.Serializable;

public final class Transfer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Stop fromStop;

    private final Route fromRoute;

    private final Trip fromTrip;

    private final Stop toStop;

    private final Route toRoute;

    private final Trip toTrip;

    private final TransferType transferType;

    private final int minTransferTimeSeconds;

    public Transfer(
            Stop fromStop,
            Stop toStop,
            Route fromRoute,
            Route toRoute,
            Trip fromTrip,
            Trip toTrip,
            TransferType transferType,
            int minTransferTimeSeconds
    ) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.fromRoute = fromRoute;
        this.toRoute = toRoute;
        this.fromTrip = fromTrip;
        this.toTrip = toTrip;
        this.transferType = transferType;
        this.minTransferTimeSeconds = minTransferTimeSeconds;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public Trip getFromTrip() {
        return fromTrip;
    }

    public Stop getToStop() {
        return toStop;
    }

    public Trip getToTrip() {
        return toTrip;
    }

    public String toString() {
        return "<Transfer"
                + toStrOpt(" stop=", fromStop, toStop)
                + toStrOpt(" route=", fromRoute, toRoute)
                + toStrOpt(" trip=", fromTrip, toTrip)
                + ">";
    }

    private static String toStrOpt(String lbl, TransitEntity arg1, TransitEntity arg2) {
        return  (arg1 == null ? "" : (lbl + arg1.getId() + ".." + arg2.getId()));
    }
}
