package org.opentripplanner.visibility;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer
   
 
 This port undoubtedly introduced a number of bugs (and removed some features).
 
 Bug reports should be directed to the OpenTripPlanner project, unless they 
 can be reproduced in the original VisiLibity
 */

import java.util.ArrayList;
import java.util.List;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer


 This port undoubtedly introduced a number of bugs (and removed some features).

 Bug reports should be directed to the OpenTripPlanner project, unless they
 can be reproduced in the original VisiLibity
 */
public class VLPolygon {

    public ArrayList<VLPoint> vertices;

    public VLPolygon() {

        vertices = new ArrayList<VLPoint>();
    }

    public VLPolygon(List<VLPoint> vertices_temp) {
        vertices = new ArrayList<VLPoint>(vertices_temp);
    }

    public int n() {
        return vertices.size();
    }

    public boolean is_simple(double epsilon) {

        if (n() == 0 || n() == 1 || n() == 2)
            return false;

        // Make sure adjacent edges only intersect at a single point.
        for (int i = 0; i <= n() - 1; i++)
            if (new LineSegment(get(i), get(i + 1)).intersection(
                    new LineSegment(get(i + 1), get(i + 2)), epsilon).size() > 1)
                return false;

        // Make sure nonadjacent edges do not intersect.
        for (int i = 0; i < n() - 2; i++)
            for (int j = i + 2; j <= n() - 1; j++)
                if (0 != (j + 1) % vertices.size()
                        && new LineSegment(get(i), get(i + 1)).distance(new LineSegment(get(j),
                                get(j + 1))) <= epsilon)
                    return false;

        return true;
    }

    public boolean is_in_standard_form() {
        if (vertices.size() > 1) // if more than one point in the polygon.
            for (int i = 1; i < vertices.size(); i++)
                if (vertices.get(0).compareTo(vertices.get(i)) > 0)
                    return false;
        return true;
    }

    public double area() {
        double area_temp = 0;
        if (n() == 0)
            return 0;
        for (int i = 0; i <= n() - 1; i++)
            area_temp += get(i).x * get(i + 1).y - get(i + 1).x * get(i).y;
        return area_temp / 2.0;
    }

    public void enforce_standard_form() {
        int point_count = vertices.size();
        if (point_count > 1) { // if more than one point in the polygon.
            ArrayList<VLPoint> vertices_temp = new ArrayList<VLPoint>(point_count);
            // Find index of lexicographically smallest point.
            int index_of_smallest = 0;
            int i; // counter.
            for (i = 1; i < point_count; i++)
                if (vertices.get(i).compareTo(vertices.get(index_of_smallest)) < 0)
                    index_of_smallest = i;
            // Fill vertices_temp starting with lex. smallest.
            for (i = index_of_smallest; i < point_count; i++)
                vertices_temp.add(vertices.get(i));
            for (i = 0; i < index_of_smallest; i++)
                vertices_temp.add(vertices.get(i));
            vertices = vertices_temp;
        }
    }

    public void eliminate_redundant_vertices(double epsilon) {
        // Degenerate case.
        if (vertices.size() < 4)
            return;

        // Store new minimal length list of vertices.
        ArrayList<VLPoint> vertices_temp = new ArrayList<VLPoint>(vertices.size());

        // Place holders.
        int first = 0;
        int second = 1;
        int third = 2;

        while (third <= vertices.size()) {
            // if second is redundant
            if (new LineSegment(get(first), get(third)).distance(get(second)) <= epsilon) {
                // =>skip it
                second = third;
                third++;
            }
            // else second not redundant
            else {
                // =>add it
                vertices_temp.add(get(second));
                first = second;
                second = third;
                third++;
            }
        }

        // decide whether to add original first point
        if (new LineSegment(vertices_temp.get(0), vertices_temp.get(vertices_temp.size() - 1))
                .distance(vertices.get(0)) > epsilon)
            vertices_temp.add(vertices.get(0));

        // Update list of vertices.
        vertices = vertices_temp;
    }

    public void reverse() {
        int n = n();
        if (n > 2) {
            // reverse, leaving the first point in its place
            for (int i = 1; i < (n+1) / 2; ++i) {
                VLPoint temp = vertices.get(i);
                vertices.set(i, vertices.get((n - i)));
                vertices.set((n - i), temp);
            }
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof VLPolygon)) {
            return false;
        }
        VLPolygon polygon2 = (VLPolygon) o;
        if (n() != polygon2.n() || n() == 0 || polygon2.n() == 0)
            return false;
        for (int i = 0; i < n(); i++)
            if (!get(i).equals(polygon2.get(i)))
                return false;
        return true;
    }

    public int hashCode() {
        return vertices.hashCode() + 1;
    }

    public VLPoint get(int i) {
        return vertices.get(i % vertices.size());
    }

    double boundary_distance(VLPolygon polygon2) {
        assert (n() > 0 && polygon2.n() > 0);

        // Handle single point degeneracy.
        if (n() == 1)
            return get(0).boundary_distance(polygon2);
        else if (polygon2.n() == 1)
            return polygon2.get(0).boundary_distance(this);
        // Handle cases where each polygon has at least 2 points.
        // Initialize to an upper bound.
        double running_min = get(0).boundary_distance(polygon2);
        double distance_temp;
        // Loop over all possible pairs of line segments.
        for (int i = 0; i <= n() - 1; i++) {
            for (int j = 0; j <= polygon2.n() - 1; j++) {
                distance_temp = new LineSegment(get(i), get(i + 1)).distance(new LineSegment(
                        polygon2.get(j), polygon2.get(j + 1)));
                if (distance_temp < running_min)
                    running_min = distance_temp;
            }
        }
        return running_min;
    }

    public String toString() {
        String outs = "";
        for (int i = 0; i < n(); i++)
            outs += get(i) + "\n";
        return outs;
    }

    public boolean hasPointInside(VLPolygon container) {
        for (VLPoint point : vertices) {
            if (point.in(container)) {
                return true;
            }
        }
        return false;
    }

}