package org.opentripplanner.routing.core;

import org.junit.Test;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.GenericLocation;
import org.opentripplanner.routing.api.request.RoutingRequest;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opentripplanner.routing.core.TraverseMode.CAR;

public class RoutingRequestTest {

    private static final FeedScopedId AGENCY_ID = new FeedScopedId("F", "A1");
    private static final FeedScopedId ROUTE_ID = new FeedScopedId("F", "R1");
    private static final FeedScopedId OTHER_ID = new FeedScopedId("F", "X");
    public static final String TIMEZONE = "Europe/Paris";

    private GenericLocation randomLocation() {
        return new GenericLocation(Math.random(), Math.random());
    }

    @Test
    public void testRequest() {
        RoutingRequest request = new RoutingRequest();

        request.addMode(CAR);
        assertTrue(request.streetSubRequestModes.getCar());
        request.removeMode(CAR);
        assertFalse(request.streetSubRequestModes.getCar());

        request.setStreetSubRequestModes(new TraverseModeSet(TraverseMode.BICYCLE,TraverseMode.WALK));
        assertFalse(request.streetSubRequestModes.getCar());
        assertTrue(request.streetSubRequestModes.getBicycle());
        assertTrue(request.streetSubRequestModes.getWalk());
    }

    @Test
    public void testIntermediatePlaces() {
        RoutingRequest req = new RoutingRequest();
        assertFalse(req.hasIntermediatePlaces());

        req.clearIntermediatePlaces();
        assertFalse(req.hasIntermediatePlaces());

        req.addIntermediatePlace(randomLocation());
        assertTrue(req.hasIntermediatePlaces());
        
        req.clearIntermediatePlaces();
        assertFalse(req.hasIntermediatePlaces());

        req.addIntermediatePlace(randomLocation());
        req.addIntermediatePlace(randomLocation());
        assertTrue(req.hasIntermediatePlaces());
    }


    private static class RoutePenaltyTC {
        final boolean prefAgency;
        final boolean prefRoute;
        final boolean unPrefAgency;
        final boolean unPrefRoute;
        public final int expectedCost;

        RoutePenaltyTC(String input) {
            String[] cells = input.replace(" ", "").split("\\|");
            this.prefAgency = "x".equalsIgnoreCase(cells[0]);
            this.prefRoute = "x".equalsIgnoreCase(cells[1]);
            this.unPrefAgency = "x".equalsIgnoreCase(cells[2]);
            this.unPrefRoute = "x".equalsIgnoreCase(cells[3]);
            this.expectedCost = Integer.parseInt(cells[4]);
        }

        RoutingRequest createRoutingRequest() {
            RoutingRequest request = new RoutingRequest();
            if(prefAgency) { request.setPreferredAgencies(List.of(OTHER_ID));}
            if(prefRoute) { request.setPreferredRoutes(List.of(OTHER_ID));}
            if(unPrefAgency) { request.setUnpreferredAgencies(List.of(AGENCY_ID));}
            if(unPrefRoute) { request.setUnpreferredRoutes(List.of(ROUTE_ID));}
            return request;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if(prefAgency) { sb.append(", prefAgency=X"); }
            if(prefRoute) { sb.append(", prefRoute=X"); }
            if(unPrefAgency) { sb.append(", unPrefAgency=X"); }
            if(unPrefRoute) { sb.append(", unPrefRoute=X"); }

            return "RoutePenaltyTC {" +  sb.substring(sb.length() == 0 ? 0 : 2) + "}";
        }
    }
}
