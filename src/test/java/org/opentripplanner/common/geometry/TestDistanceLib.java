package org.opentripplanner.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import junit.framework.TestCase;

public class TestDistanceLib extends TestCase {

    public void testMetersToDegree() {
        // Note: 111194.926644559 is 1 degree at the equator, given the earth radius used in the lib
        double degree;
        degree = SphericalDistanceLibrary.metersToDegrees(111194.926644559);
        assertTrue(Math.abs(degree - 1.0) < 1e-5);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 0);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(1))) < 1e-5);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 1.0);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(2))) < 1e-5);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, -1.0);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(2))) < 1e-5);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 45.0);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(46))) < 1e-5);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, -45.0);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(46))) < 1e-5);
        // Further north, solutions get degenerated.
        degree = SphericalDistanceLibrary.metersToLonDegrees(111194.926644559, 80);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(81))) < 1e-4);
        degree = SphericalDistanceLibrary.metersToLonDegrees(111.194926, 45);
        assertTrue(Math.abs(degree - 1.0 / Math.cos(Math.toRadians(44.999)) / 1000) < 1e-5);
    }

    private Coordinate makeCoordinate(double lat, double lon) {
        return new Coordinate(lon, lat);
    }

    private LineString makeLineString(double... latlon) {
        assertTrue(latlon.length % 2 == 0);
        Coordinate[] coords = new Coordinate[latlon.length / 2];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = new Coordinate(latlon[i * 2 + 1], latlon[i * 2]);
        }
        return new GeometryFactory().createLineString(coords);
    }
}