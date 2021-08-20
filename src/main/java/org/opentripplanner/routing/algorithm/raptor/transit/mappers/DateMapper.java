package org.opentripplanner.routing.algorithm.raptor.transit.mappers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateMapper {
    public static ZonedDateTime asStartOfService(LocalDate localDate, ZoneId zoneId) {
        return ZonedDateTime.of(localDate, LocalTime.NOON, zoneId)
                .minusHours(12);
    }

    public static int secondsSinceStartOfTime(ZonedDateTime startOfTime, LocalDate localDate) {
        ZonedDateTime startOfDay = asStartOfService(localDate, startOfTime.getZone());
        return (int) Duration.between(startOfTime, startOfDay).getSeconds();
    }

    public static int secondsSinceStartOfTime(ZonedDateTime startOfTime, Instant instant) {
        return (int) Duration.between(startOfTime.toInstant(), instant).getSeconds();
    }
}
