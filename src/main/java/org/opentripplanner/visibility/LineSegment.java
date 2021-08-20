package org.opentripplanner.visibility;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer


 This port undoubtedly introduced a number of bugs (and removed some features).

 Bug reports should be directed to the OpenTripPlanner project, unless they
 can be reproduced in the original VisiLibity.
 */
public class LineSegment {

    VLPoint[] endpoints;

    public int size() {
        if (endpoints == null) {
            return 0;
        } else {
            return endpoints.length;
        }
    }

    LineSegment(VLPoint first_point_temp, VLPoint second_point_temp) {
        this(first_point_temp, second_point_temp, 0);
    }

    LineSegment(VLPoint first_point_temp, VLPoint second_point_temp, double epsilon) {
        if (first_point_temp.distance(second_point_temp) <= epsilon) {
            endpoints = new VLPoint[1];
            endpoints[0] = first_point_temp;
        } else {
            endpoints = new VLPoint[2];
            endpoints[0] = first_point_temp;
            endpoints[1] = second_point_temp;
        }
    }

    VLPoint first() {
        assert (size() > 0);

        return endpoints[0];
    }

    VLPoint second() {
        assert (size() > 0);

        if (size() == 2)
            return endpoints[1];
        else
            return endpoints[0];
    }

    public boolean equals(Object o) {
        if (!(o instanceof LineSegment)) {
            return false;
        }
        LineSegment line_segment2 = (LineSegment) o;
        if (size() != line_segment2.size() || size() == 0 || line_segment2.size() == 0)
            return false;
        else
            return (first().equals(line_segment2.first()) && second()
                    .equals(line_segment2.second()));
    }

    public String toString() {
        switch (size()) {
        case 0:
            return "";
        case 1:
        case 2:
            return first() + "\n" + second() + "\n";
        default:
            throw new IllegalArgumentException();
        }
    }

}