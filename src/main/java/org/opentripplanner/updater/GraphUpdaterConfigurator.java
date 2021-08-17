package org.opentripplanner.updater;

import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.updater.alerts.GtfsRealtimeAlertsUpdater;
import org.opentripplanner.updater.alerts.GtfsRealtimeAlertsUpdaterParameters;
import org.opentripplanner.updater.bike_rental.BikeRentalUpdater;
import org.opentripplanner.updater.bike_rental.BikeRentalUpdaterParameters;
import org.opentripplanner.updater.stoptime.MqttGtfsRealtimeUpdater;
import org.opentripplanner.updater.stoptime.MqttGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.PollingStoptimeUpdater;
import org.opentripplanner.updater.stoptime.PollingStoptimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.WebsocketGtfsRealtimeUpdater;
import org.opentripplanner.updater.stoptime.WebsocketGtfsRealtimeUpdaterParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Sets up and starts all the graph updaters.
 *
 * Updaters are instantiated based on the updater parameters contained in UpdaterConfig. Updaters
 * are then setup by providing the graph as a parameter. Finally, the updaters are added to the
 * GraphUpdaterManager.
 */
public abstract class GraphUpdaterConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(GraphUpdaterConfigurator.class);

    public static void setupGraph(
        Graph graph,
        UpdatersParameters updatersParameters
    ) {
        List<GraphUpdater> updaters = new ArrayList<>();

        updaters.addAll(
            createUpdatersFromConfig(updatersParameters)
        );

        setupUpdaters(graph, updaters);
        GraphUpdaterManager updaterManager = new GraphUpdaterManager(graph, updaters);
        updaterManager.startUpdaters();

        // Stop the updater manager if it contains nothing
        if (updaterManager.size() == 0) {
            updaterManager.stop();
        }
        // Otherwise add it to the graph
        else {
            graph.updaterManager = updaterManager;
        }
    }

    public static void shutdownGraph(Graph graph) {
        GraphUpdaterManager updaterManager = graph.updaterManager;
        if (updaterManager != null) {
            LOG.info("Stopping updater manager with " + updaterManager.size() + " updaters.");
            updaterManager.stop();
        }
    }

    public static void setupUpdaters(Graph graph, List<GraphUpdater> updaters) {
        for (GraphUpdater updater : updaters) {
            try {
                updater.setup(graph);
            } catch (Exception e) {
                LOG.warn("Failed to setup updater {}", updater.getConfigRef());
            }
        }
    }


    /* private methods */

    /**
     * @return a list of GraphUpdaters created from the configuration
     */
    private static List<GraphUpdater> createUpdatersFromConfig(
        UpdatersParameters config
    ) {
        List<GraphUpdater> updaters = new ArrayList<>();

        for (BikeRentalUpdaterParameters configItem : config.getBikeRentalParameters()) {
            updaters.add(new BikeRentalUpdater(configItem));
        }
        for (GtfsRealtimeAlertsUpdaterParameters configItem : config.getGtfsRealtimeAlertsUpdaterParameters()) {
            updaters.add(new GtfsRealtimeAlertsUpdater(configItem));
        }
        for (PollingStoptimeUpdaterParameters configItem : config.getPollingStoptimeUpdaterParameters()) {
            updaters.add(new PollingStoptimeUpdater(configItem));
        }
        for (WebsocketGtfsRealtimeUpdaterParameters configItem : config.getWebsocketGtfsRealtimeUpdaterParameters()) {
            updaters.add(new WebsocketGtfsRealtimeUpdater(configItem));
        }
        for (MqttGtfsRealtimeUpdaterParameters configItem : config.getMqttGtfsRealtimeUpdaterParameters()) {
            updaters.add(new MqttGtfsRealtimeUpdater(configItem));
        }

        return updaters;
    }
}
