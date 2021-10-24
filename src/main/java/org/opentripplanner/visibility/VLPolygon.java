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

    public VLPolygon(List<VLPoint> vertices_temp) {
        vertices = new ArrayList<VLPoint>(vertices_temp);
    }

    public int n() {
        return vertices.size();
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