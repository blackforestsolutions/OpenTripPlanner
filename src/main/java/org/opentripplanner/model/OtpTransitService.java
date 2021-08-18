package org.opentripplanner.model;

import java.util.Collection;
import java.util.List;

/**
 * Methods for accessing imported entities.
 */
public interface OtpTransitService {

    /**
     * @return  a list of all Agencies.
     */
    Collection<Agency> getAllAgencies();

    Collection<FareAttribute> getAllFareAttributes();

    Collection<FareRule> getAllFareRules();

    Collection<FeedInfo> getAllFeedInfos();

    Collection<GroupOfStations> getAllGroupsOfStations();

    Collection<MultiModalStation> getAllMultiModalStations();

    Collection<Pathway> getAllPathways();

    /**
     * @return all ids for both Calendars and CalendarDates merged into on list without duplicates.
     */
    Collection<FeedScopedId> getAllServiceIds();

    List<ShapePoint> getShapePointsForShapeId(FeedScopedId shapeId);

    Station getStationForId(FeedScopedId id);

    Stop getStopForId(FeedScopedId id);

    Collection<Station> getAllStations();

    Collection<Stop> getAllStops();

    Collection<Entrance> getAllEntrances();

    Collection<PathwayNode> getAllPathwayNodes();

    Collection<BoardingArea> getAllBoardingAreas();

    /**
     * @return the list of {@link StopTime} objects associated with the trip,
     * sorted by {@link StopTime#getStopSequence()}
     */
    List<StopTime> getStopTimesForTrip(Trip trip);

    Collection<Transfer> getAllTransfers();

    Collection<TripPattern> getTripPatterns();

    Collection<Trip> getAllTrips();

}
