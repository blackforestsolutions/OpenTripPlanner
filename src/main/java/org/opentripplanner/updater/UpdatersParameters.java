package org.opentripplanner.updater;

import org.opentripplanner.updater.alerts.GtfsRealtimeAlertsUpdaterParameters;
import org.opentripplanner.updater.bike_park.BikeParkUpdaterParameters;
import org.opentripplanner.updater.bike_rental.BikeRentalUpdaterParameters;
import org.opentripplanner.updater.stoptime.MqttGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.PollingStoptimeUpdaterParameters;
import org.opentripplanner.updater.stoptime.WebsocketGtfsRealtimeUpdaterParameters;
import org.opentripplanner.updater.street_notes.WFSNotePollingGraphUpdaterParameters;

import java.net.URI;
import java.util.List;

public interface UpdatersParameters {

  URI bikeRentalServiceDirectoryUrl();

  List<BikeRentalUpdaterParameters> getBikeRentalParameters();

  List<GtfsRealtimeAlertsUpdaterParameters> getGtfsRealtimeAlertsUpdaterParameters();

  List<PollingStoptimeUpdaterParameters> getPollingStoptimeUpdaterParameters();

  List<WebsocketGtfsRealtimeUpdaterParameters> getWebsocketGtfsRealtimeUpdaterParameters();

  List<MqttGtfsRealtimeUpdaterParameters> getMqttGtfsRealtimeUpdaterParameters();

  List<BikeParkUpdaterParameters> getBikeParkUpdaterParameters();

  List<WFSNotePollingGraphUpdaterParameters> getWinkkiPollingGraphUpdaterParameters();
}
