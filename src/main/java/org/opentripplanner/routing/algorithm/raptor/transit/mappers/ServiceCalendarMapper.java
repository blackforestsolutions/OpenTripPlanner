package org.opentripplanner.routing.algorithm.raptor.transit.mappers;

import org.opentripplanner.model.calendar.ServiceDate;

import java.time.LocalDate;

/**
 * This uses the integer service codes that are already present in the pre-Raptor OTP graph.
 */
class ServiceCalendarMapper {
    static LocalDate localDateFromServiceDate(ServiceDate serviceDate) {
        return LocalDate.of(serviceDate.getYear(), serviceDate.getMonth(), serviceDate.getDay());
    }
}
