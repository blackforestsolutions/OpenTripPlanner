package org.opentripplanner.api.mapping;

import org.opentripplanner.api.model.ApiStopShort;
import org.opentripplanner.model.StationElement;
import org.opentripplanner.model.Stop;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StopMapper {

    public static ApiStopShort mapToApiShort(Stop domain) {
        if(domain == null) { return null; }

        ApiStopShort api = new ApiStopShort();
        api.id = FeedScopedIdMapper.mapToApi(domain.getId());
        api.code = domain.getCode();
        api.name = domain.getName();
        api.lat = domain.getLat();
        api.lon = domain.getLon();
        api.url = domain.getUrl();
        api.stationId = FeedScopedIdMapper.mapIdToApi(domain.getParentStation());
        // parentStation may be missing on the stop returning null.
        // TODO harmonize these names, maybe use "station" everywhere
        api.cluster = mapToParentStationOldId(domain);

        return api;
    }

    /** @param distance in integral meters, to avoid serializing a bunch of decimal places. */
    public static ApiStopShort mapToApiShort(Stop domain, int distance) {
        if(domain == null) { return null; }

        ApiStopShort api = mapToApiShort(domain);
        api.dist = distance;

        return api;
    }

    public static List<ApiStopShort> mapToApiShort(Collection<Stop> domain) {
        if(domain == null) { return null; }
        return domain.stream().map(StopMapper::mapToApiShort).collect(Collectors.toList());
    }

    /**
     * Get the parent station id (without feed-scope) for the given station element,
     * this method should only be used to fetch old ids for beeing backward compatible.
     */
    private static String mapToParentStationOldId(StationElement stop) {
        return stop.isPartOfStation() ? stop.getParentStation().getId().getId() : null;
    }
}
