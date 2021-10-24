package org.opentripplanner.gtfs.mapping;

import org.opentripplanner.model.Entrance;

import java.util.HashMap;
import java.util.Map;

/** Responsible for mapping GTFS Entrance into the OTP model. */
class EntranceMapper {

    private Map<org.onebusaway.gtfs.model.Stop, Entrance> mappedEntrances = new HashMap<>();

    /** Map from GTFS to OTP model, {@code null} safe. */
    Entrance map(org.onebusaway.gtfs.model.Stop orginal) {
        return orginal == null ? null : mappedEntrances.computeIfAbsent(orginal, this::doMap);
    }

    private Entrance doMap(org.onebusaway.gtfs.model.Stop gtfsStop) {
        if (gtfsStop.getLocationType()
                != org.onebusaway.gtfs.model.Stop.LOCATION_TYPE_ENTRANCE_EXIT) {
            throw new IllegalArgumentException(
                    "Expected type " + org.onebusaway.gtfs.model.Stop.LOCATION_TYPE_ENTRANCE_EXIT
                            + ", but got " + gtfsStop.getLocationType());
        }

        StopMappingWrapper base = new StopMappingWrapper(gtfsStop);

        return new Entrance(
            base.getId(),
            base.getName(),
            base.getCode(),
            base.getDescription(),
            base.getCoordinate(),
            base.getWheelchairBoarding(),
            base.getLevel()
        );
    }
}
