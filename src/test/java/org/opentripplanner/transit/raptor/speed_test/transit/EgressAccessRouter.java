package org.opentripplanner.transit.raptor.speed_test.transit;

import gnu.trove.map.TIntIntMap;
import org.opentripplanner.graph_builder.DataImportIssueStore;
import org.opentripplanner.graph_builder.linking.SimpleStreetSplitter;
import org.opentripplanner.graph_builder.module.NearbyStopFinder;
import org.opentripplanner.routing.algorithm.raptor.transit.TransitLayer;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graphfinder.StopAtDistance;
import org.opentripplanner.transit.raptor.speed_test.SpeedTestRequest;
import org.opentripplanner.transit.raptor.util.AvgTimer;

public class EgressAccessRouter {
    private static final AvgTimer TIMER_ROUTE = AvgTimer.timerMilliSec("EgressAccessRouter:route");

    private final TransitLayer transitLayer;
    private final Graph graph;
    private final SimpleStreetSplitter splitter;

    private StreetSearch egressSearch;
    private StreetSearch accessSearch;

    public EgressAccessRouter(Graph graph, TransitLayer transitLayer) {
        this.graph = graph;
        this.transitLayer = transitLayer;
        this.splitter = new SimpleStreetSplitter(
                graph,
                null,
                null,
                false,
                new DataImportIssueStore(false)
        );
    }

    public void route(SpeedTestRequest request) {
        TIMER_ROUTE.time(() -> {
            // Search for access to / egress from transit on streets.
            NearbyStopFinder nearbyStopFinder = new NearbyStopFinder(
                    graph, request.getAccessEgressMaxWalkDistanceMeters(), true
            );
            accessSearch = new StreetSearch(transitLayer, graph, splitter, nearbyStopFinder);
            egressSearch = new StreetSearch(transitLayer, graph, splitter, nearbyStopFinder);

            accessSearch.route(request.tc().fromPlace, true);
            egressSearch.route(request.tc().toPlace, false);
        });
    }

    public TIntIntMap getAccessTimesInSecondsByStopIndex() {
        return accessSearch.resultTimesSecByStopIndex;
    }

    public TIntIntMap getEgressTimesInSecondsByStopIndex() {
        return egressSearch.resultTimesSecByStopIndex;
    }

    StopAtDistance getAccessPath(int stopIndex) {
        return accessSearch.pathsByStopIndex.get(stopIndex);
    }

    StopAtDistance getEgressPath(int stopIndex) {
        return egressSearch.pathsByStopIndex.get(stopIndex);
    }
}
