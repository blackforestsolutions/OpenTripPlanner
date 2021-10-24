package org.opentripplanner.model.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opentripplanner.ConstantsForTests;
import org.opentripplanner.gtfs.GtfsContextBuilder;
import org.opentripplanner.model.Agency;
import org.opentripplanner.model.FareAttribute;
import org.opentripplanner.model.FareRule;
import org.opentripplanner.model.FeedInfo;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.OtpTransitService;
import org.opentripplanner.model.Pathway;
import org.opentripplanner.model.ShapePoint;
import org.opentripplanner.model.Station;
import org.opentripplanner.model.Stop;
import org.opentripplanner.model.Transfer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.opentripplanner.gtfs.GtfsContextBuilder.contextBuilder;

public class OtpTransitServiceImplTest {
    private static final String FEED_ID = "Z";

    private static final FeedScopedId STATION_ID = new FeedScopedId(FEED_ID, "station");

    // The subject is used as read only; hence static is ok
    private static OtpTransitService subject;

    private static Agency agency;

    @BeforeClass
    public static void setup() throws IOException {
        GtfsContextBuilder contextBuilder = contextBuilder(FEED_ID, ConstantsForTests.FAKE_GTFS);
        OtpTransitServiceBuilder builder = contextBuilder.getTransitBuilder();

        agency = first(builder.getAgenciesById().values());

        // Supplement test data with at least one entity in all collections
        FareRule rule = createFareRule();
        builder.getFareAttributes().add(rule.getFare());
        builder.getFareRules().add(rule);
        builder.getFeedInfos().add(new FeedInfo());

        subject = builder.build();
    }

    @Test
    public void testGetAllAgencies() {
        Collection<Agency> agencies = subject.getAllAgencies();
        Agency agency = first(agencies);

        assertEquals(1, agencies.size());
        assertEquals("agency", agency.getId().getId());
        assertEquals("Fake Agency", agency.getName());
    }

    @Test
    public void testGetAllFareAttributes() {
        Collection<FareAttribute> fareAttributes = subject.getAllFareAttributes();

        assertEquals(1, fareAttributes.size());
        assertEquals("<FareAttribute Z:FA>", first(fareAttributes).toString());
    }

    @Test
    public void testGetAllFareRules() {
        Collection<FareRule> fareRules = subject.getAllFareRules();

        assertEquals(1, fareRules.size());
        assertEquals(
                "<FareRule  origin='Zone A' contains='Zone B' destination='Zone C'>",
                first(fareRules).toString()
        );
    }

    @Test
    public void testGetAllFeedInfos() {
        Collection<FeedInfo> feedInfos = subject.getAllFeedInfos();

        assertEquals(1, feedInfos.size());
        assertEquals("<FeedInfo 1>", first(feedInfos).toString());
    }

    @Test
    public void testGetAllPathways() {
        Collection<Pathway> pathways = subject.getAllPathways();

        assertEquals(3, pathways.size());
        assertEquals("<Pathway Z:pathways_1_1>", first(pathways).toString());
    }

    @Test
    public void testGetAllTransfers() {
        Collection<Transfer> transfers = subject.getAllTransfers();

        assertEquals(9, transfers.size());
        assertEquals("<Transfer stop=Z:F..Z:E>", first(transfers).toString());
    }

    @Test
    public void testGetAllStations() {
        Collection<Station> stations = subject.getAllStations();

        assertEquals(1, stations.size());
        assertEquals("<Station Z:station>", first(stations).toString());
    }

    @Test
    public void testGetAllStops() {
        Collection<Stop> stops = subject.getAllStops();

        assertEquals(22, stops.size());
        assertEquals("<Stop Z:A>", first(stops).toString());
    }

    @Test
    public void testGetShapePointsForShapeId() {
        List<ShapePoint> shapePoints = subject.getShapePointsForShapeId(new FeedScopedId("Z", "5"));
        assertEquals("[#1 (41,-72), #2 (41,-72), #3 (40,-72), #4 (41,-73), #5 (41,-74)]",
                shapePoints.stream().map(OtpTransitServiceImplTest::toString).collect(toList()).toString());
    }

    @Test
    public void testGetAllServiceIds() {
        Collection<FeedScopedId> serviceIds = subject.getAllServiceIds();

        assertEquals(2, serviceIds.size());
        assertEquals("Z:alldays", first(serviceIds).toString());
    }

    private static FareRule createFareRule() {
        FareAttribute fa = new FareAttribute();
        fa.setId(new FeedScopedId(FEED_ID, "FA"));
        FareRule rule = new FareRule();
        rule.setOriginId("Zone A");
        rule.setContainsId("Zone B");
        rule.setDestinationId("Zone C");
        rule.setFare(fa);
        return rule;
    }

    private static <T> T first(Collection<? extends T> c) {
        //noinspection ConstantConditions
        return c.stream().sorted(comparing(T::toString)).findFirst().get();
    }

    private static String toString(ShapePoint sp) {
        int lat = (int) sp.getLat();
        int lon = (int) sp.getLon();
        return "#" + sp.getSequence() + " (" + lat + "," + lon + ")";
    }
}