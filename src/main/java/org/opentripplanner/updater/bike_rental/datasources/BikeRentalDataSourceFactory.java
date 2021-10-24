package org.opentripplanner.updater.bike_rental.datasources;

import org.opentripplanner.updater.bike_rental.BikeRentalDataSource;
import org.opentripplanner.updater.bike_rental.datasources.params.BikeRentalDataSourceParameters;
import org.opentripplanner.updater.bike_rental.datasources.params.GbfsBikeRentalDataSourceParameters;

public class BikeRentalDataSourceFactory {

  public static BikeRentalDataSource create(BikeRentalDataSourceParameters source) {
    switch (source.getSourceType()) {
      case GBFS:          return new GbfsBikeRentalDataSource((GbfsBikeRentalDataSourceParameters) source);
    }
    throw new IllegalArgumentException(
        "Unknown bike rental source type: " + source.getSourceType()
    );
  }
}
