package org.opentripplanner.routing.alertpatch;

import org.opentripplanner.util.I18NString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransitAlert implements Serializable {
    private static final long serialVersionUID = 8305126586053909836L;

    private String id;

    public I18NString alertHeaderText;
    public I18NString alertDescriptionText;
    public I18NString alertDetailText;
    public I18NString alertAdviceText;

    // TODO OTP2 we wanted to merge the GTFS single alertUrl and the SIRI multiple URLs.
    //      However, GTFS URLs are one-per-language in a single object, and SIRI URLs are N objects with no translation.
    public I18NString alertUrl;

    //null means unknown
    public String alertType;

    //null means unknown
    public String severity;

    private List<TimePeriod> timePeriods = new ArrayList<>();

    private String feedId;

    private final Set<EntitySelector> entities = new HashSet<>();

    private final Collection<StopCondition> stopConditions = new ArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public boolean displayDuring(long startTimeSeconds, long endTimeSeconds) {
        for (TimePeriod timePeriod : timePeriods) {
            if (endTimeSeconds >= timePeriod.startTime) {
                if (startTimeSeconds < timePeriod.endTime) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setTimePeriods(List<TimePeriod> periods) {
        timePeriods = periods;
    }

    public void addEntity(EntitySelector entitySelector) {
        entities.add(entitySelector);
    }

    public Set<EntitySelector> getEntities() {
        return entities;
    }

    public Collection<StopCondition> getStopConditions() {
        return stopConditions;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public Date getEffectiveStartDate() {
        return timePeriods
            .stream()
            .map(timePeriod -> timePeriod.startTime)
            .min(Comparator.naturalOrder())
            .map(startTime -> new Date(startTime * 1000))
            .orElse(null);
    }

    public Date getEffectiveEndDate() {
        return timePeriods
            .stream()
            .map(timePeriod -> timePeriod.endTime)
            .max(Comparator.naturalOrder())
            .map(startTime -> new Date(startTime * 1000))
            .orElse(null);
    }

    @Override
    public String toString() {
        return "Alert('"
                + (alertHeaderText != null ? alertHeaderText.toString()
                        : alertDescriptionText != null ? alertDescriptionText.toString()
                        : alertDetailText != null ? alertDetailText.toString()
                        : alertAdviceText != null ? alertAdviceText.toString()
                                : "?") + "')";
    }

}
