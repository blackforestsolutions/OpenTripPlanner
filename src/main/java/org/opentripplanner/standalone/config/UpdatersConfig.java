package org.opentripplanner.standalone.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.opentripplanner.standalone.config.updaters.BikeRentalUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.GtfsRealtimeAlertsUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.MqttGtfsRealtimeUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.PollingStoptimeUpdaterConfig;
import org.opentripplanner.standalone.config.updaters.WebsocketGtfsRealtimeUpdaterConfig;
import org.opentripplanner.updater.UpdatersParameters;
import org.opentripplanner.updater.alerts.GtfsRealtimeAlertsUpdaterParameters;
import org.opentripplanner.updater.bike_rental.BikeRentalUpdaterParameters;
import org.opentripplanner.updater.stoptime.MqttGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.PollingStoptimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.WebsocketGtfsRealtimeUpdaterParameters;
import org.opentripplanner.util.OtpAppException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class maps between the JSON array of updaters and the concrete class implementations of
 * each updater parameters. Some updaters use the same parameters, so a map is kept between the
 * JSON updater type strings and the appropriate updater parameter class.
 */
public class UpdatersConfig implements UpdatersParameters {

  private static final String BIKE_RENTAL = "bike-rental";
  private static final String STOP_TIME_UPDATER = "stop-time-updater";
  private static final String WEBSOCKET_GTFS_RT_UPDATER = "websocket-gtfs-rt-updater";
  private static final String MQTT_GTFS_RT_UPDATER = "mqtt-gtfs-rt-updater";
  private static final String REAL_TIME_ALERTS = "real-time-alerts";

  private static final Map<String, BiFunction<String, NodeAdapter, ?>> CONFIG_CREATORS = new HashMap<>();

  static {
    CONFIG_CREATORS.put(BIKE_RENTAL, BikeRentalUpdaterConfig::create);
    CONFIG_CREATORS.put(STOP_TIME_UPDATER, PollingStoptimeUpdaterConfig::create);
    CONFIG_CREATORS.put(WEBSOCKET_GTFS_RT_UPDATER, WebsocketGtfsRealtimeUpdaterConfig::create);
    CONFIG_CREATORS.put(MQTT_GTFS_RT_UPDATER, MqttGtfsRealtimeUpdaterConfig::create);
    CONFIG_CREATORS.put(REAL_TIME_ALERTS, GtfsRealtimeAlertsUpdaterConfig::create);
  }

  private final Multimap<String, Object> configList = ArrayListMultimap.create();

  private final URI bikeRentalServiceDirectoryUrl;

  public UpdatersConfig(NodeAdapter rootAdapter) {
    this.bikeRentalServiceDirectoryUrl = rootAdapter.asUri("bikeRentalServiceDirectoryUrl", null);

    List<NodeAdapter> updaters = rootAdapter.path("updaters").asList();

    for (NodeAdapter conf : updaters) {
      String type = conf.asText("type");
      BiFunction<String, NodeAdapter, ?> factory = CONFIG_CREATORS.get(type);
      if(factory == null) {
        throw new OtpAppException("The updater config type is unknown: " + type);
      }
      configList.put(type, factory.apply(type, conf));
    }
  }

  /**
   * This is the endpoint url used for the BikeRentalServiceDirectory sandbox feature.
   * @see org.opentripplanner.ext.bikerentalservicedirectory.BikeRentalServiceDirectoryFetcher
   */
  @Override
  public URI bikeRentalServiceDirectoryUrl() {
   return this.bikeRentalServiceDirectoryUrl;
  }

  @Override
  public List<BikeRentalUpdaterParameters> getBikeRentalParameters() {
    return getParameters(BIKE_RENTAL);
  }

  @Override
  public List<GtfsRealtimeAlertsUpdaterParameters> getGtfsRealtimeAlertsUpdaterParameters() {
    return getParameters(REAL_TIME_ALERTS);
  }

  @Override
  public List<PollingStoptimeUpdaterParameters> getPollingStoptimeUpdaterParameters() {
    return getParameters(STOP_TIME_UPDATER);
  }

  @Override
  public List<WebsocketGtfsRealtimeUpdaterParameters> getWebsocketGtfsRealtimeUpdaterParameters() {
    return getParameters(WEBSOCKET_GTFS_RT_UPDATER);
  }

  @Override
  public List<MqttGtfsRealtimeUpdaterParameters> getMqttGtfsRealtimeUpdaterParameters() {
    return getParameters(MQTT_GTFS_RT_UPDATER);
  }

  private <T> List<T> getParameters(String key) {
    return (List<T>) configList.get(key);
  }
}
