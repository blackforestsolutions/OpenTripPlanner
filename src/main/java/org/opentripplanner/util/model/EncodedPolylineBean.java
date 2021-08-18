package org.opentripplanner.util.model;

import java.io.Serializable;

/**
 * A list of coordinates encoded as a string.
 * 
 * See <a href="http://code.google.com/apis/maps/documentation/polylinealgorithm.html">Encoded
 * polyline algorithm format</a>
 */

public class EncodedPolylineBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String points;

    private String levels;

    private int length;

    public EncodedPolylineBean(String points, String levels, int length) {
        this.points = points;
        this.levels = levels;
        this.length = length;
    }

    /**
     * The encoded points of the polyline.
     */
    public String getPoints() {
        return points;
    }

    /**
     * Levels describes which points should be shown at various zoom levels. Presently, we show all
     * points at all zoom levels.
    */
    public String getLevels() {
        return levels;
    }

    /**
     * The number of points in the string
     */
    public int getLength() {
        return length;
    }
}