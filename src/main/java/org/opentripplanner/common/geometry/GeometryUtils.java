package org.opentripplanner.common.geometry;

import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opentripplanner.common.model.P2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeometryUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GeometryUtils.class);

    private static CoordinateSequenceFactory csf = new Serializable2DPackedCoordinateSequenceFactory();
    private static GeometryFactory gf = new GeometryFactory(csf);

    /** A shared copy of the WGS84 CRS with longitude-first axis order. */
    public static final CoordinateReferenceSystem WGS84_XY;
    static {
        try {
            WGS84_XY = CRS.getAuthorityFactory(true).createCoordinateReferenceSystem("EPSG:4326");
        } catch (Exception ex) {
            LOG.error("Unable to create longitude-first WGS84 CRS", ex);
            throw new RuntimeException("Could not create longitude-first WGS84 coordinate reference system.");
        }
    }

    public static LineString makeLineString(double... coords) {
        GeometryFactory factory = getGeometryFactory();
        Coordinate [] coordinates = new Coordinate[coords.length / 2];
        for (int i = 0; i < coords.length; i+=2) {
            coordinates[i / 2] = new Coordinate(coords[i], coords[i+1]);
        }
        return factory.createLineString(coordinates);
    }

    public static LineString makeLineString(Coordinate[] coordinates) {
        GeometryFactory factory = getGeometryFactory();
        return factory.createLineString(coordinates);
    }

    public static LineString addStartEndCoordinatesToLineString(Coordinate startCoord, LineString lineString, Coordinate endCoord) {
        Coordinate[] coordinates = new Coordinate[lineString.getCoordinates().length + 2];
        coordinates[0] = startCoord;
        for (int j = 0; j < lineString.getCoordinates().length; j++) {
            coordinates[j + 1] = lineString.getCoordinates()[j];
        }
        coordinates[lineString.getCoordinates().length + 1] = endCoord;
        return makeLineString(coordinates);
    }

    public static LineString removeStartEndCoordinatesFromLineString(LineString lineString) {
        Coordinate[] coordinates = new Coordinate[lineString.getCoordinates().length - 2];
        for (int j = 1; j < lineString.getCoordinates().length - 1; j++) {
            coordinates[j - 1] = lineString.getCoordinates()[j];
        }
        return makeLineString(coordinates);
    }

    public static GeometryFactory getGeometryFactory() {
        return gf;
    }
    
    /**
     * Splits the input geometry into two LineStrings at the given point.
     */
    public static P2<LineString> splitGeometryAtPoint(Geometry geometry, Coordinate nearestPoint) {
        // An index in JTS can actually refer to any point along the line. It is NOT an array index.
        LocationIndexedLine line = new LocationIndexedLine(geometry);
        LinearLocation l = line.indexOf(nearestPoint);

        LineString beginning = (LineString) line.extractLine(line.getStartIndex(), l);
        LineString ending = (LineString) line.extractLine(l, line.getEndIndex());

        return new P2<LineString>(beginning, ending);
    }

    /**
     * Returns the chunk of the given geometry between the two given coordinates.
     * 
     * Assumes that "second" is after "first" along the input geometry.
     */
    public static LineString getInteriorSegment(Geometry geomerty, Coordinate first,
            Coordinate second) {
        P2<LineString> splitGeom = GeometryUtils.splitGeometryAtPoint(geomerty, first);
        splitGeom = GeometryUtils.splitGeometryAtPoint(splitGeom.second, second);
        return splitGeom.first;
    }
}
