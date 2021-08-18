package org.opentripplanner.routing;

import lombok.experimental.Delegate;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.TimetableSnapshot;
import org.opentripplanner.model.TripPattern;
import org.opentripplanner.routing.algorithm.RoutingWorker;
import org.opentripplanner.routing.api.request.RoutingRequest;
import org.opentripplanner.routing.api.response.RoutingResponse;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.GraphIndex;
import org.opentripplanner.routing.graphfinder.GraphFinder;
import org.opentripplanner.standalone.server.Router;

import java.util.Collection;

/**
 * This is the entry point of all API requests towards the OTP graph. A new instance of this class
 * should be created for each request. This ensures that the same TimetableSnapshot is used for the
 * duration of the request (which may involve several method calls).
 */
public class RoutingService {

    @Delegate(types = Graph.class)
    private final Graph graph;

    @Delegate(types = GraphIndex.class)
    private final GraphIndex graphIndex;

    @Delegate(types = GraphFinder.class)
    private final GraphFinder graphFinder;

    /**
     * This should only be accessed through the getTimetableSnapshot method.
     */
    private TimetableSnapshot timetableSnapshot;

    public RoutingService(Graph graph) {
        this.graph = graph;
        this.graphIndex = graph.index;
        this.graphFinder = GraphFinder.getInstance(graph);
    }

    // TODO We should probably not have the Router as a parameter here
    public RoutingResponse route(RoutingRequest request, Router router) {
        RoutingWorker worker = new RoutingWorker(router.raptorConfig, request);
        return worker.route(router);
    }

    /**
     * Returns all the patterns for a specific stop. If includeRealtimeUpdates is set, new patterns
     * added by realtime updates are added to the collection.
     */
    public Collection<TripPattern> getPatternsForStop(Stop stop, boolean includeRealtimeUpdates) {
        return graph.index.getPatternsForStop(stop,
                includeRealtimeUpdates ? lazyGetTimeTableSnapShot() : null
        );
    }

    /**
     * Lazy-initialization of TimetableSnapshot
     *
     * @return The same TimetableSnapshot is returned throughout the lifecycle of this object.
     */
    private TimetableSnapshot lazyGetTimeTableSnapShot() {
        if (this.timetableSnapshot == null) {
            timetableSnapshot = graph.getTimetableSnapshot();
        }
        return this.timetableSnapshot;
    }
}
