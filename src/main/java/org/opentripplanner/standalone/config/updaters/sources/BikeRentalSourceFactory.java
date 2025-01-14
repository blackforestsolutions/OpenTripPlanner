package org.opentripplanner.standalone.config.updaters.sources;

import org.opentripplanner.standalone.config.NodeAdapter;
import org.opentripplanner.updater.DataSourceType;
import org.opentripplanner.updater.bike_rental.datasources.params.BikeRentalDataSourceParameters;
import org.opentripplanner.updater.bike_rental.datasources.params.GbfsBikeRentalDataSourceParameters;
import org.opentripplanner.util.OtpAppException;

import java.util.HashMap;
import java.util.Map;

import static org.opentripplanner.updater.DataSourceType.GBFS;

/**
 * This class is an object representation of the data source for a single real-time updater in
 * 'router-config.json' Each data source defines an inner interface with its required attributes.
 */
public class BikeRentalSourceFactory {

  private static final Map<String, DataSourceType> CONFIG_MAPPING = new HashMap<>();

  static {
    CONFIG_MAPPING.put("gbfs", GBFS);
  }

  private final DataSourceType type;
  private final NodeAdapter c;

  public BikeRentalSourceFactory(DataSourceType type, NodeAdapter c) {
    this.type = type;
    this.c = c;
  }

  public static BikeRentalDataSourceParameters create(String typeKey, NodeAdapter c) {
    DataSourceType type = CONFIG_MAPPING.get(typeKey);
    if (type == null) {
      throw new OtpAppException("The updater source type is unknown: " + typeKey);
    }
    return new BikeRentalSourceFactory(type, c).create();
  }


  public BikeRentalDataSourceParameters create() {
    switch (type) {
      case GBFS: return new GbfsBikeRentalDataSourceParameters(url(), network(), routeAsCar());
      default:   return new BikeRentalDataSourceParameters(type, url(), network(), apiKey());
    }
  }

  private String url() {
    return c.asText("url");
  }

  private String network() {
    return c.asText("network", null);
  }

  private String apiKey() {
    return c.asText("apiKey", null);
  }

  private boolean routeAsCar() {
    return c.asBoolean("routeAsCar", false);
  }
}
