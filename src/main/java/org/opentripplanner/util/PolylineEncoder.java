package org.opentripplanner.util;

import java.util.AbstractList;

import org.opentripplanner.util.model.EncodedPolylineBean;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

public class PolylineEncoder {

    public static EncodedPolylineBean createEncodings(Iterable<Coordinate> points) {
        return createEncodings(points, -1);
    }

    public static EncodedPolylineBean createEncodings(Geometry geometry) {
        if (geometry instanceof LineString) {

            LineString string = (LineString) geometry;
            Coordinate[] coordinates = string.getCoordinates();
            return createEncodings(new CoordinateList(coordinates));
        } else if (geometry instanceof MultiLineString) {
            MultiLineString mls = (MultiLineString) geometry;
            return createEncodings(new CoordinateList(mls.getCoordinates()));
        } else {
            throw new IllegalArgumentException(geometry.toString());
        }
    }

    /**
     * 
     * @param points
     * @param level
     * @return
     */
    public static EncodedPolylineBean createEncodings(Iterable<Coordinate> points, int level) {

        StringBuilder encodedPoints = new StringBuilder();
        StringBuilder encodedLevels = new StringBuilder();

        int plat = 0;
        int plng = 0;
        int count = 0;

        for (Coordinate point : points) {

            int late5 = floor1e5(point.y);
            int lnge5 = floor1e5(point.x);

            int dlat = late5 - plat;
            int dlng = lnge5 - plng;

            plat = late5;
            plng = lnge5;

            encodedPoints.append(encodeSignedNumber(dlat)).append(encodeSignedNumber(dlng));
            if (level >= 0)
                encodedLevels.append(encodeNumber(level));
            count++;
        }

        String pointsString = encodedPoints.toString();
        String levelsString = level >= 0 ? encodedLevels.toString() : null;
        return new EncodedPolylineBean(pointsString, levelsString, count);
    }

    /*****************************************************************************
     * Private Methods
     ****************************************************************************/

    private static final int floor1e5(double coordinate) {
        return (int) Math.floor(coordinate * 1e5);
    }

    public static String encodeSignedNumber(int num) {
        int sgn_num = num << 1;
        if (num < 0) {
            sgn_num = ~(sgn_num);
        }
        return (encodeNumber(sgn_num));
    }

    public static String encodeNumber(int num) {

        StringBuffer encodeString = new StringBuffer();

        while (num >= 0x20) {
            int nextValue = (0x20 | (num & 0x1f)) + 63;
            encodeString.append((char) (nextValue));
            num >>= 5;
        }

        num += 63;
        encodeString.append((char) (num));

        return encodeString.toString();
    }

    private static class CoordinateList extends AbstractList<Coordinate> {

        private Coordinate[] coordinates;

        public CoordinateList(Coordinate[] coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public Coordinate get(int index) {
            return coordinates[index];
        }

        @Override
        public int size() {
            return coordinates.length;
        }
    }
}
