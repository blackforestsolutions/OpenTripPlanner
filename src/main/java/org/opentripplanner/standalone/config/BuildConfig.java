package org.opentripplanner.standalone.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.opentripplanner.graph_builder.module.osm.WayPropertySetSource;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.model.calendar.ServiceDateInterval;
import org.opentripplanner.routing.impl.DefaultFareServiceFactory;
import org.opentripplanner.routing.services.FareServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;

/**
 * This class is an object representation of the 'build-config.json'.
 * <p>
 * These are parameters that when changed, necessitate a Graph rebuild. They are distinct from the
 * RouterParameters which can be applied to a pre-built graph or on the fly at runtime. Eventually
 * both classes may be initialized from the same config file so make sure there is no overlap in the
 * JSON keys used.
 * <p>
 * These used to be command line parameters, but there were getting to be too many of them and
 * besides, we want to allow different graph build configuration for each Graph.
 * <p>
 * TODO maybe have only one giant config file and just annotate the parameters to indicate which
 * ones trigger a rebuild ...or just feed the same JSON tree to two different classes, one of which
 * is the build configuration and the other is the router configuration.
 */
public class BuildConfig {
    private static final Logger LOG = LoggerFactory.getLogger(BuildConfig.class);

    public static final BuildConfig DEFAULT = new BuildConfig(MissingNode.getInstance(), "DEFAULT", false);

    private static final double DEFAULT_SUBWAY_ACCESS_TIME_MINUTES = 2.0;

    /**
     * The raw JsonNode three kept for reference and (de)serialization.
     */
    private final JsonNode rawJson;

    /**
     * Minutes necessary to reach stops served by trips on routes of route_type=1 (subway) from the street.
     * Perhaps this should be a runtime router parameter rather than a graph build parameter.
     */
    public final double subwayAccessTime;

    /**
     * A specific fares service to use.
     */
    public final FareServiceFactory fareServiceFactory;

    /**
     * Custom OSM way properties
     */
    public final WayPropertySetSource osmWayPropertySet;

    /**
     * Whether we should create car P+R stations from OSM data.
     */
    public boolean staticParkAndRide;

    /**
     * Maximal distance between stops in meters that will connect consecutive trips that are made with same vehicle
     * Stop -> Stop or Stop -> Footpath -> Stop
     */
    public int maxInterlineDistance;

    /**
     * This field indicates the pruning threshold for islands without stops.
     * Any such island under this size will be pruned.
     */
    public final int pruningThresholdIslandWithoutStops;

    /**
     * This field indicates the pruning threshold for islands with stops.
     * Any such island under this size will be pruned.
     */
    public final int pruningThresholdIslandWithStops;

    /**
     * This field indicates whether walking should be allowed on OSM ways
     * tagged with "foot=discouraged".
     */
    public final boolean banDiscouragedWalking;

    /**
     * This field indicates whether bicycling should be allowed on OSM ways
     * tagged with "bicycle=discouraged".
     */
    public final boolean banDiscouragedBiking;

    /**
     * Transfers up to this length in meters will be pre-calculated and included in the Graph.
     */
    public final double maxTransferDistance;

    /**
     * Limit the import of transit services to the given START date. Inclusive. If set, any transit
     * service on a day BEFORE the given date is dropped and will not be part of the graph.
     * Use an absolute date or a period relative to the date the graph is build(BUILD_DAY).
     * <p>
     * Optional, defaults to "-P1Y" (BUILD_DAY minus 1 year). Use an empty string to make it
     * unbounded.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "2019-11-24"} - 24. November 2019.</li>
     *     <li>{@code "-P3W"} - BUILD_DAY minus 3 weeks.</li>
     *     <li>{@code "-P1Y2M"} - BUILD_DAY minus 1 year and 2 months.</li>
     *     <li>{@code ""} - Unlimited, no upper bound.</li>
     * </ul>
     * @see LocalDate#parse(CharSequence) for date format accepted.
     * @see Period#parse(CharSequence) for period format accepted.
     */
    public LocalDate transitServiceStart;

    /**
     * Limit the import of transit services to the given END date. Inclusive. If set, any transit
     * service on a day AFTER the given date is dropped and will not be part of the graph.
     * Use an absolute date or a period relative to the date the graph is build(BUILD_DAY).
     * <p>
     * Optional, defaults to "P3Y" (BUILD_DAY plus 3 years). Use an empty string to make it
     * unbounded.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "2021-12-31"} - 31. December 2021.</li>
     *     <li>{@code "P24W"} - BUILD_DAY plus 24 weeks.</li>
     *     <li>{@code "P1Y6M5D"} - BUILD_DAY plus 1 year, 6 months and 5 days.</li>
     *     <li>{@code ""} - Unlimited, no lower bound.</li>
     * </ul>
     * @see LocalDate#parse(CharSequence) for date format accepted.
     * @see Period#parse(CharSequence) for period format accepted.
     */
    public LocalDate transitServiceEnd;

    /**
     * Otp auto detect input and output files using the command line supplied paths. This parameter
     * make it possible to override this by specifying a path for each file. All parameters in the
     * storage section is optional, and the fallback is to use the auto detection. It is OK to
     * autodetect some file and specify the path to others.
     */
    public final StorageConfig storage;

    /**
     * Set all parameters from the given Jackson JSON tree, applying defaults.
     * Supplying MissingNode.getInstance() will cause all the defaults to be applied.
     * This could be done automatically with the "reflective query scraper" but it's less type safe and less clear.
     * Until that class is more type safe, it seems simpler to just list out the parameters by name here.
     */
    public BuildConfig(JsonNode node, String source, boolean logUnusedParams) {
        NodeAdapter c = new NodeAdapter(node, source);
        rawJson = node;

        // Keep this list of BASIC parameters sorted alphabetically on config PARAMETER name
        banDiscouragedWalking = c.asBoolean("banDiscouragedWalking", false);
        banDiscouragedBiking = c.asBoolean("banDiscouragedBiking", false);
        pruningThresholdIslandWithStops = c.asInt("islandWithStopsMaxSize", 5);
        pruningThresholdIslandWithoutStops = c.asInt("islandWithoutStopsMaxSize", 40);
        maxInterlineDistance = c.asInt("maxInterlineDistance", 200);
        maxTransferDistance = c.asDouble("maxTransferDistance", 2000d);
        osmWayPropertySet = WayPropertySetSource.fromConfig(c.asText("osmWayPropertySet", "default"));
        staticParkAndRide = c.asBoolean("staticParkAndRide", true);
        subwayAccessTime = c.asDouble("subwayAccessTime", DEFAULT_SUBWAY_ACCESS_TIME_MINUTES);
        transitServiceStart = c.asDateOrRelativePeriod("transitServiceStart", "-P1Y");
        transitServiceEnd = c.asDateOrRelativePeriod( "transitServiceEnd", "P3Y");

        // List of complex parameters
        fareServiceFactory = DefaultFareServiceFactory.fromConfig(c.asRawNode("fares"));
        storage = new StorageConfig(c.path("storage"));

        if(logUnusedParams) {
            c.logAllUnusedParameters(LOG);
        }
    }

    /**
     * If {@code true} the config is loaded from file, in not the DEFAULT config is used.
     */
    public boolean isDefault() {
        return rawJson.isMissingNode();
    }

    public String toJson() {
        return rawJson.isMissingNode() ? "" : rawJson.toString();
    }

    public ServiceDateInterval getTransitServicePeriod() {
        return new ServiceDateInterval(
                new ServiceDate(transitServiceStart),
                new ServiceDate(transitServiceEnd)
        );
    }

    public int getSubwayAccessTimeSeconds() {
        // Convert access time in minutes to seconds
        return (int)(subwayAccessTime * 60.0);
    }
}
