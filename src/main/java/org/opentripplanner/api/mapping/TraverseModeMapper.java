package org.opentripplanner.api.mapping;

import org.opentripplanner.routing.core.TraverseMode;

import java.util.HashMap;
import java.util.Map;

public class TraverseModeMapper {

    private static final Map<String, TraverseMode> toDomain;

    static {
        Map<String, TraverseMode> map = new HashMap<>();
        for (TraverseMode it : TraverseMode.values()) {
            map.put(mapToApi(it), it);
        }
        toDomain = Map.copyOf(map);
    }

    public static String mapToApi(TraverseMode domain) {
        if(domain == null) {
            return null;
        }

        switch (domain) {
            case AIRPLANE: return "AIRPLANE";
            case BICYCLE: return "BICYCLE";
            case BUS: return "BUS";
            case CAR: return "CAR";
            case CABLE_CAR: return "CABLE_CAR";
            case FERRY: return "FERRY";
            case FUNICULAR: return "FUNICULAR";
            case GONDOLA: return "GONDOLA";
            case RAIL: return "RAIL";
            case SUBWAY: return "SUBWAY";
            case TRAM: return "TRAM";
            case TRANSIT: return "TRANSIT";
            case WALK: return "WALK";
        }
        throw new IllegalArgumentException("Traverse mode not mapped: " + domain);
    }
}
