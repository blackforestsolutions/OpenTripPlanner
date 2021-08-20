package org.opentripplanner.graph_builder;

import com.google.common.collect.Lists;
import org.opentripplanner.datastore.CompositeDataSource;
import org.opentripplanner.datastore.DataSource;
import org.opentripplanner.graph_builder.model.GtfsBundle;
import org.opentripplanner.graph_builder.module.DirectTransferGenerator;
import org.opentripplanner.graph_builder.module.GtfsModule;
import org.opentripplanner.graph_builder.module.PruneFloatingIslands;
import org.opentripplanner.graph_builder.module.StreetLinkerModule;
import org.opentripplanner.graph_builder.module.TransitToTaggedStopsModule;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.graph_builder.services.DefaultStreetEdgeFactory;
import org.opentripplanner.graph_builder.services.GraphBuilderModule;
import org.opentripplanner.openstreetmap.BinaryOpenStreetMapProvider;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.standalone.config.BuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.opentripplanner.datastore.FileType.GTFS;
import static org.opentripplanner.datastore.FileType.OSM;

/**
 * This makes a Graph out of various inputs like GTFS and OSM.
 * It is modular: GraphBuilderModules are placed in a list and run in sequence.
 */
public class GraphBuilder implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(GraphBuilder.class);

    private final List<GraphBuilderModule> graphBuilderModules = new ArrayList<>();

    private final Graph graph;

    private GraphBuilder(Graph baseGraph) {
        this.graph = baseGraph == null ? new Graph() : baseGraph;
    }

    private void addModule(GraphBuilderModule loader) {
        graphBuilderModules.add(loader);
    }

    public Graph getGraph() {
        return graph;
    }

    public void run() {
         // Record how long it takes to build the graph, purely for informational purposes.
        long startTime = System.currentTimeMillis();

        // Check all graph builder inputs, and fail fast to avoid waiting until the build process
        // advances.
        for (GraphBuilderModule builder : graphBuilderModules) {
            builder.checkInputs();
        }

        DataImportIssueStore issueStore = new DataImportIssueStore(true);
        HashMap<Class<?>, Object> extra = new HashMap<Class<?>, Object>();

        for (GraphBuilderModule load : graphBuilderModules) {
            load.buildGraph(graph, extra, issueStore);
        }
        issueStore.summarize();

        long endTime = System.currentTimeMillis();
        LOG.info(String.format("Graph building took %.1f minutes.", (endTime - startTime) / 1000 / 60.0));
        LOG.info("Main graph size: |V|={} |E|={}", graph.countVertices(), graph.countEdges());
    }

    /**
     * Factory method to create and configure a GraphBuilder with all the appropriate modules to
     * build a graph from the given data source and configuration directory.
     */
    public static GraphBuilder create(
            BuildConfig config,
            GraphBuilderDataSources dataSources,
            Graph baseGraph
    ) {

        boolean hasOsm  = dataSources.has(OSM);
        boolean hasGtfs = dataSources.has(GTFS);

        GraphBuilder graphBuilder = new GraphBuilder(baseGraph);

        if ( hasOsm ) {
            List<BinaryOpenStreetMapProvider> osmProviders = Lists.newArrayList();
            for (DataSource osmFile : dataSources.get(OSM)) {
                osmProviders.add(new BinaryOpenStreetMapProvider(osmFile));
            }
            OpenStreetMapModule osmModule = new OpenStreetMapModule(osmProviders);
            DefaultStreetEdgeFactory streetEdgeFactory = new DefaultStreetEdgeFactory();
            osmModule.edgeFactory = streetEdgeFactory;
            osmModule.setDefaultWayPropertySetSource(config.osmWayPropertySet);
            osmModule.staticParkAndRide = config.staticParkAndRide;
            osmModule.banDiscouragedWalking = config.banDiscouragedWalking;
            osmModule.banDiscouragedBiking = config.banDiscouragedBiking;
            graphBuilder.addModule(osmModule);
            PruneFloatingIslands pruneFloatingIslands = new PruneFloatingIslands();
            pruneFloatingIslands.setPruningThresholdIslandWithoutStops(config.pruningThresholdIslandWithoutStops);
            pruneFloatingIslands.setPruningThresholdIslandWithStops(config.pruningThresholdIslandWithStops);
            graphBuilder.addModule(pruneFloatingIslands);
        }
        if ( hasGtfs ) {
            List<GtfsBundle> gtfsBundles = Lists.newArrayList();
            for (DataSource gtfsData : dataSources.get(GTFS)) {

                GtfsBundle gtfsBundle = new GtfsBundle((CompositeDataSource)gtfsData);
                gtfsBundle.subwayAccessTime = config.getSubwayAccessTimeSeconds();
                gtfsBundle.maxInterlineDistance = config.maxInterlineDistance;
                gtfsBundles.add(gtfsBundle);
            }
            GtfsModule gtfsModule = new GtfsModule(gtfsBundles, config.getTransitServicePeriod());
            gtfsModule.setFareServiceFactory(config.fareServiceFactory);
            graphBuilder.addModule(gtfsModule);
        }

        if(hasGtfs && (hasOsm || graphBuilder.graph.hasStreets)) {
            graphBuilder.addModule(new TransitToTaggedStopsModule());
        }

        // This module is outside the hasGTFS conditional block because it also links things like bike rental
        // which need to be handled even when there's no transit.
        StreetLinkerModule streetLinkerModule = new StreetLinkerModule();
        graphBuilder.addModule(streetLinkerModule);

        if (hasGtfs) {
            // The stops can be linked to each other once they are already linked to the street network.
            // This module will use streets or straight line distance depending on whether OSM data is found in the graph.
            graphBuilder.addModule(new DirectTransferGenerator(config.maxTransferDistance));
        }
        return graphBuilder;
    }
}

