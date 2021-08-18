package org.opentripplanner.index;

import org.opentripplanner.api.mapping.FeedScopedIdMapper;
import org.opentripplanner.api.mapping.StopMapper;
import org.opentripplanner.api.model.ApiStopShort;
import org.opentripplanner.model.*;
import org.opentripplanner.model.calendar.ServiceDate;
import org.opentripplanner.routing.RoutingService;
import org.opentripplanner.standalone.server.OTPServer;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// TODO move to org.opentripplanner.api.resource, this is a Jersey resource class

@Path("/routers/{routerId}/index")    // It would be nice to get rid of the final /index.
@Produces(MediaType.APPLICATION_JSON) // One @Produces annotation for all endpoints.
public class IndexAPI {

    private static final double MAX_STOP_SEARCH_RADIUS = 5000;

    /** Choose short or long form of results. */
    @QueryParam("detail")
    private boolean detail = false;

    /** Include GTFS entities referenced by ID in the result. */
    @QueryParam("refs")
    private boolean refs = false;

    private final OTPServer otpServer;

    public IndexAPI(@Context OTPServer otpServer, @PathParam("routerId") String routerId) {
        this.otpServer = otpServer;
    }

    /* Needed to check whether query parameter map is empty, rather than chaining " && x == null"s */
    @Context
    UriInfo uriInfo;

    /** Return a list of all stops within a circle around the given coordinate. */
    @SuppressWarnings("ConstantConditions")
    @GET
    @Path("/stops")
    public List<ApiStopShort> getStopsInRadius(
            @QueryParam("minLat") Double minLat,
            @QueryParam("minLon") Double minLon,
            @QueryParam("maxLat") Double maxLat,
            @QueryParam("maxLon") Double maxLon,
            @QueryParam("lat") Double lat,
            @QueryParam("lon") Double lon,
            @QueryParam("radius") Double radius
    ) {
        /* When no parameters are supplied, return all stops. */
        if (uriInfo.getQueryParameters().isEmpty()) {
            return StopMapper.mapToApiShort(createRoutingService().getAllStops());
        }

        /* If any of the circle parameters are specified, expect a circle not a box. */
        boolean expectCircle = (lat != null || lon != null || radius != null);
        if (expectCircle) {
            verifyParams()
                    .withinBounds("lat", lat, -90.0, 90.0)
                    .withinBounds("lon", lon, -180, 180)
                    .positiveOrZero("radius", radius)
                    .validate();

            radius = Math.min(radius, MAX_STOP_SEARCH_RADIUS);

            return createRoutingService().getStopsInRadius(new WgsCoordinate(lat, lon), radius)
                    .stream()
                    .map(it -> StopMapper.mapToApiShort(it.first, it.second.intValue()))
                    .collect(Collectors.toList());
        }
        else {
            /* We're not circle mode, we must be in box mode. */
            verifyParams()
                    .withinBounds("minLat", minLat, -90.0, 90.0)
                    .withinBounds("maxLat", maxLat, -90.0, 90.0)
                    .withinBounds("minLon", minLon, -180.0, 180.0)
                    .withinBounds("maxLon", maxLon, -180.0, 180.0)
                    .lessThan("minLat", minLat, "maxLat", maxLat)
                    .lessThan("minLon", minLon, "maxLon", maxLon)
                    .validate();
            Collection<Stop> stops = createRoutingService()
                    .getStopsByBoundingBox(minLat, minLon, maxLat, maxLon);
            return StopMapper.mapToApiShort(stops
            );
        }
    }


    /* PRIVATE METHODS */

    private RoutingService createRoutingService() {
        return otpServer.createRoutingRequestService();
    }

    private static ValidateParameters verifyParams() {
        return new ValidateParameters();
    }
}