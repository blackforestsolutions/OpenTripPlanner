package org.opentripplanner.common.geometry;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.atan2;
import static org.apache.commons.math3.util.FastMath.cos;
import static org.apache.commons.math3.util.FastMath.sin;
import static org.apache.commons.math3.util.FastMath.sqrt;
import static org.apache.commons.math3.util.FastMath.toRadians;

import org.apache.commons.math3.util.FastMath;

import org.locationtech.jts.geom.Coordinate;

public abstract class SphericalDistanceLibrary {

    public static final double RADIUS_OF_EARTH_IN_KM = 6371.01;
    public static final double RADIUS_OF_EARTH_IN_M = RADIUS_OF_EARTH_IN_KM * 1000;
    
    // Max admissible lat/lon delta for approximated distance computation
    public static final double MAX_LAT_DELTA_DEG = 4.0;
    public static final double MAX_LON_DELTA_DEG = 4.0;

    // 1 / Max over-estimation error of approximated distance,
    // for delta lat/lon in given range
    public static final double MAX_ERR_INV = 0.999462;  

    public static final double distance(Coordinate from, Coordinate to) {
        return distance(from.y, from.x, to.y, to.x);
    }

    public static final double fastDistance(Coordinate from, Coordinate to) {
        return fastDistance(from.y, from.x, to.y, to.x);
    }

    public static final double distance(double lat1, double lon1, double lat2, double lon2) {
        return distance(lat1, lon1, lat2, lon2, RADIUS_OF_EARTH_IN_M);
    }

    /**
     * Compute an (approximated) distance between two points, with a known cos(lat).
     * Be careful, this is approximated and never check for the validity of input cos(lat).
     */
    public static final double fastDistance(double lat1, double lon1, double lat2, double lon2) {
        return fastDistance(lat1, lon1, lat2, lon2, RADIUS_OF_EARTH_IN_M);
    }
    
    public static final double distance(double lat1, double lon1, double lat2, double lon2,
            double radius) {
        // http://en.wikipedia.org/wiki/Great-circle_distance
        lat1 = toRadians(lat1); // Theta-s
        lon1 = toRadians(lon1); // Lambda-s
        lat2 = toRadians(lat2); // Theta-f
        lon2 = toRadians(lon2); // Lambda-f

        double deltaLon = lon2 - lon1;

        double y = sqrt(p2(cos(lat2) * sin(deltaLon))
                + p2(cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)));
        double x = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(deltaLon);

        return radius * atan2(y, x);        
    }
    

    /**
     * Approximated, fast and under-estimated equirectangular distance between two points.
     * Works only for small delta lat/lon, fall-back on exact distance if not the case.
     * See: http://www.movable-type.co.uk/scripts/latlong.html
     */
    public static final double fastDistance(double lat1, double lon1, double lat2, double lon2, double radius) {
        if (abs(lat1 - lat2) > MAX_LAT_DELTA_DEG || abs(lon1 - lon2) > MAX_LON_DELTA_DEG) {
            return distance(lat1, lon1, lat2, lon2, radius);
        }
        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1) * cos(toRadians((lat1 + lat2) / 2));
        return radius * sqrt(dLat * dLat + dLon * dLon) * MAX_ERR_INV;
    }

    private static final double p2(double a) {
        return a * a;
    }

    /**
     * @param distanceMeters Distance in meters.
     * @return The number of degree for the given distance. For degrees latitude, this is nearly correct. For degrees
     *         longitude, this is an overestimate because meridians converge toward the poles.
     */
    public static double metersToDegrees(double distanceMeters) {
        return 360 * distanceMeters / (2 * Math.PI * RADIUS_OF_EARTH_IN_M);
    }

    /**
     * @return the approximate number of meters for the given number of degrees latitude. If degrees longitude are
     *         supplied, this is an overestimate anywhere off the equator because meridians converge toward the poles.
     */
    public static double degreesLatitudeToMeters(double degreesLatitude) {
        return (2 * Math.PI * RADIUS_OF_EARTH_IN_M) * degreesLatitude / 360;
    }

    /**
     * @param distanceMeters Distance in meters.
     * @param latDeg Latitude of center point, in degree.
     * @return The number of longitude degree for the given distance. This is a slight overestimate
     *         as the number of degree of longitude for a given distance depends on the exact
     *         latitude.
     */
    public static double metersToLonDegrees(double distanceMeters, double latDeg) {
        double dLatDeg = 360 * distanceMeters / (2 * Math.PI * RADIUS_OF_EARTH_IN_M);
        /*
         * The computation below ensure that minCosLat is the minimum value of cos(lat) for lat in
         * the range [lat-dLat, lat+dLat].
         */
        double minCosLat;
        if (latDeg > 0) {
            minCosLat = FastMath.cos(FastMath.toRadians(latDeg + dLatDeg));
        } else {
            minCosLat = FastMath.cos(FastMath.toRadians(latDeg - dLatDeg));
        }
        return dLatDeg / minCosLat;
    }
    
}
