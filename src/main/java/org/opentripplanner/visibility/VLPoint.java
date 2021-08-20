package org.opentripplanner.visibility;

import java.lang.Math;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer


 This port undoubtedly introduced a number of bugs (and removed some features).

 Bug reports should be directed to the OpenTripPlanner project, unless they
 can be reproduced in the original VisiLibity
 */
public class VLPoint implements Comparable<VLPoint>, Cloneable {

    public double x, y;

    public VLPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public VLPoint projection_onto(LineSegment line_segment_temp) {

        if (line_segment_temp.size() == 1)
            return line_segment_temp.first();
        // The projection of point_temp onto the line determined by
        // line_segment_temp can be represented as an affine combination
        // expressed in the form projection of Point =
        // theta*line_segment_temp.first +
        // (1.0-theta)*line_segment_temp.second. if theta is outside
        // the interval [0,1], then one of the LineSegment's endpoints
        // must be closest to calling Point.
        double theta = ((line_segment_temp.second().x - x)
                * (line_segment_temp.second().x - line_segment_temp.first().x) + (line_segment_temp
                .second().y - y) * (line_segment_temp.second().y - line_segment_temp.first().y))
                / (Math.pow(line_segment_temp.second().x - line_segment_temp.first().x, 2) + Math
                        .pow(line_segment_temp.second().y - line_segment_temp.first().y, 2));
        // std::cout << "\E[1;37;40m" << "Theta is: " << theta << "\x1b[0m"
        // << std::endl;
        if ((0.0 <= theta) && (theta <= 1.0))
            return line_segment_temp.first().times(theta)
                    .plus(line_segment_temp.second().times(1.0 - theta));
        // Else pick closest endpoint.
        if (distance(line_segment_temp.first()) < distance(line_segment_temp.second()))
            return line_segment_temp.first();
        return line_segment_temp.second();
    }

    public VLPoint projection_onto_boundary_of(VLPolygon polygon_temp) {

        VLPoint running_projection = polygon_temp.get(0);
        double running_min = distance(running_projection);
        VLPoint point_temp;
        for (int i = 0; i <= polygon_temp.n() - 1; i++) {
            point_temp = projection_onto(new LineSegment(polygon_temp.get(i),
                    polygon_temp.get(i + 1)));
            if (distance(point_temp) < running_min) {
                running_projection = point_temp;
                running_min = distance(running_projection);
            }
        }
        return running_projection;
    }

    public boolean on_boundary_of(VLPolygon polygon_temp, double epsilon) {

        if (distance(projection_onto_boundary_of(polygon_temp)) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean in(VLPolygon polygon_temp)

    {
        return in(polygon_temp, 0);
    }

    public boolean in(VLPolygon polygon_temp, double epsilon) {

        int n = polygon_temp.vertices.size();
        if (on_boundary_of(polygon_temp, epsilon))
            return true;
        // Then check the number of times a ray emanating from the Point
        // crosses the boundary of the Polygon. An odd number of
        // crossings indicates the Point is in the interior of the
        // Polygon. Based on
        // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html

        boolean c = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            if ((((polygon_temp.get(i).y <= y) && (y < polygon_temp.get(j).y)) || ((polygon_temp
                    .get(j).y <= y) && (y < polygon_temp.get(i).y)))
                    && (x < (polygon_temp.get(j).x - polygon_temp.get(i).x)
                            * (y - polygon_temp.get(i).y)
                            / (polygon_temp.get(j).y - polygon_temp.get(i).y)
                            + polygon_temp.get(i).x))
                c = !c;
        }
        return c;
    }

    public boolean equals(Object o) {
        if (!(o instanceof VLPoint)) {
            return false;
        }
        VLPoint point2 = (VLPoint) o;
        return x == point2.x && y == point2.y;
    }

    public int compareTo(VLPoint point2) {

        if (x < point2.x)
            return -1;
        else if (x == point2.x) {
            if (y < point2.y) {
                return -1;
            } else if (y == point2.y) {
                return 0;
            }
            return 1;
        }
        return 1;
    }

    public VLPoint plus(VLPoint point2) {
        return new VLPoint(x + point2.x, y + point2.y);
    }

    public VLPoint times(double scalar) {
        return new VLPoint(scalar * x, scalar * y);
    }

    public double distance(VLPoint point2) {
        return Math.sqrt(Math.pow(x - point2.x, 2) + Math.pow(y - point2.y, 2));
    }

    public String toString() {
        return "\n" + x + ", " + y;
    }

    public VLPoint clone() {
        return new VLPoint(x, y);
    }

    public int hashCode() {
        return new Double(x).hashCode() + new Double(y).hashCode();
    }

}