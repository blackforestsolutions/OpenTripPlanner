package org.opentripplanner.gtfs;

import org.opentripplanner.graph_builder.DataImportIssueStore;
import org.opentripplanner.gtfs.mapping.AgencyAndIdMapper;
import org.opentripplanner.gtfs.mapping.GTFSToOtpTransitServiceMapper;
import org.opentripplanner.model.FeedScopedId;

import org.opentripplanner.model.impl.OtpTransitServiceBuilder;

import java.io.File;
import java.io.IOException;

public class MockGtfs {

    private final org.onebusaway.gtfs.services.MockGtfs gtfsDelegate;

    private MockGtfs(org.onebusaway.gtfs.services.MockGtfs gtfsDelegate) {
        this.gtfsDelegate = gtfsDelegate;
    }

    public static MockGtfs create() throws IOException {
        return new MockGtfs(org.onebusaway.gtfs.services.MockGtfs.create());
    }

    public File getPath() {
        return gtfsDelegate.getPath();
    }

    public void putLines(String fileName, String... rows) {
        gtfsDelegate.putLines(fileName, rows);
    }

    public OtpTransitServiceBuilder read() throws IOException {
        return GTFSToOtpTransitServiceMapper.mapGtfsDaoToInternalTransitServiceBuilder(
                gtfsDelegate.read(),
                "a0",
                new DataImportIssueStore(false)
        );
    }

    public void putAgencies(int numberOfRows, String... columns) {
        gtfsDelegate.putAgencies(numberOfRows, columns);
    }

    public void putRoutes(int numberOfRows, String... columns) {
        gtfsDelegate.putRoutes(numberOfRows, columns);
    }

    public void putStops(int numberOfRows, String... columns) {
        gtfsDelegate.putStops(numberOfRows, columns);
    }

    public void putCalendars(int numberOfServiceIds, String... columns) {
        gtfsDelegate.putCalendars(numberOfServiceIds, columns);
    }

    public void putTrips(int numberOfRows, String routeIds, String serviceIds, String... columns) {
        gtfsDelegate.putTrips(numberOfRows, routeIds, serviceIds, columns);
    }

    public void putStopTimes(String tripIds, String stopIds) {
        gtfsDelegate.putStopTimes(tripIds, stopIds);
    }
}
