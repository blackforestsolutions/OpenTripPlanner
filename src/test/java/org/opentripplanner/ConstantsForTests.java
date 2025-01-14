package org.opentripplanner;

import com.google.common.collect.Lists;
import org.opentripplanner.graph_builder.module.AddTransitModelEntitiesToGraph;
import org.opentripplanner.graph_builder.module.StreetLinkerModule;
import org.opentripplanner.graph_builder.module.geometry.GeometryAndBlockProcessor;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.graph_builder.services.GraphBuilderModule;
import org.opentripplanner.gtfs.GtfsContext;
import org.opentripplanner.model.calendar.CalendarServiceData;
import org.opentripplanner.openstreetmap.BinaryOpenStreetMapProvider;
import org.opentripplanner.routing.graph.Graph;

import java.io.File;
import java.util.HashMap;

import static org.opentripplanner.gtfs.GtfsContextBuilder.contextBuilder;

public class ConstantsForTests {

    public static final String CALTRAIN_GTFS = "src/test/resources/caltrain_gtfs.zip";

    private static final String PORTLAND_GTFS = "src/test/resources/google_transit.zip";

    private static final String PORTLAND_CENTRAL_OSM = "src/test/resources/portland-central-filtered.osm.pbf";

    private static final String OSLO_EAST_OSM = "src/test/resources/oslo-east-filtered.osm.pbf";

    public static final String KCM_GTFS = "src/test/resources/kcm_gtfs.zip";
    
    public static final String FAKE_GTFS = "src/test/resources/testagency.zip";

    public static final String FARE_COMPONENT_GTFS = "src/test/resources/farecomponent_gtfs.zip";

    private static ConstantsForTests instance = null;

    private Graph portlandGraph = null;

    private GtfsContext portlandContext = null;

    private ConstantsForTests() {

    }

    public static ConstantsForTests getInstance() {
        if (instance == null) {
            instance = new ConstantsForTests();
        }
        return instance;
    }

    public Graph getPortlandGraph() {
        if (portlandGraph == null) {
            setupPortland();
        }
        return portlandGraph;
    }

    private void setupPortland() {
        try {
            portlandGraph = new Graph();
            // Add street data from OSM
            {
                File osmFile = new File(PORTLAND_CENTRAL_OSM);
                BinaryOpenStreetMapProvider osmProvider = new BinaryOpenStreetMapProvider(osmFile);
                OpenStreetMapModule osmModule = new OpenStreetMapModule(Lists.newArrayList(osmProvider));
                osmModule.buildGraph(portlandGraph, new HashMap<>());
            }
            // Add transit data from GTFS
            {
                portlandContext = contextBuilder(ConstantsForTests.PORTLAND_GTFS)
                        .withIssueStoreAndDeduplicator(portlandGraph)
                        .build();
                AddTransitModelEntitiesToGraph.addToGraph(portlandContext, portlandGraph);
                GeometryAndBlockProcessor factory = new GeometryAndBlockProcessor(portlandContext);
                factory.run(portlandGraph);
            }
            // Link transit stops to streets
            {
                GraphBuilderModule streetTransitLinker = new StreetLinkerModule();
                streetTransitLinker.buildGraph(portlandGraph, new HashMap<>());
            }
            // TODO: eliminate GTFSContext
            // this is now making a duplicate calendarservicedata but it's oh so practical
            portlandGraph.putService(
                    CalendarServiceData.class,
                    portlandContext.getCalendarServiceData()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
