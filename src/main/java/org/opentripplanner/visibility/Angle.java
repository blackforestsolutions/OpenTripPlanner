package org.opentripplanner.visibility;

import java.lang.Math;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer


 This port undoubtedly introduced a number of bugs (and removed some features).

 Bug reports should be directed to the OpenTripPlanner project, unless they
 can be reproduced in the original VisiLibity.
 */
class Angle implements Comparable<Angle>, Cloneable {
    double angle_radians;

    public Angle clone() {
        Angle angle = new Angle(angle_radians);
        angle.angle_radians = angle_radians; // handle 2pi case
        return angle;
    }

    public void set_to_2pi() {
        angle_radians = 2 * Math.PI;
    }

    public double get() {
        return angle_radians;
    }

    public Angle(double data_temp) {
        if (data_temp >= 0)
            angle_radians = data_temp % (2 * Math.PI);
        else {
            angle_radians = 2 * Math.PI + data_temp % -(2 * Math.PI);
            if (angle_radians == 2 * Math.PI)
                angle_radians = 0;
        }
    }

    public Angle(double rise_temp, double run_temp) {
        // First calculate 4 quadrant inverse tangent into [-pi,+pi].
        angle_radians = Math.atan2(rise_temp, run_temp);
        // Correct so angles specified in [0, 2*PI).
        if (angle_radians < 0)
            angle_radians = 2 * Math.PI + angle_radians;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Angle)) {
            return false;
        }
        Angle angle2 = (Angle) other;
        return (get() == angle2.get());
    }

    public int compareTo(Angle angle2) {
        return (int) Math.signum(get() - angle2.get());
    }

    public String toString() {

        return "" + angle_radians;
    }

    public int hashCode() {
        return new Double(angle_radians).hashCode();
    }
}