package org.opentripplanner.api.mapping;

import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.TransitEntity;


public class FeedScopedIdMapper {


    private static final String SEPARATOR = ":";

    public static String mapToApi(FeedScopedId arg) {
        if (arg == null) {
            return null;
        }
        return arg.getFeedId() + SEPARATOR + arg.getId();
    }

    public static String mapIdToApi(TransitEntity<FeedScopedId> entity) {
        return entity == null ? null : mapToApi(entity.getId());
    }
}
