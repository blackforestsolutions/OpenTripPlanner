package org.opentripplanner.visibility;

import java.util.ArrayList;
import java.util.List;

/**
 Ported by David Turner from Visilibity, by Karl J. Obermeyer

 This port undoubtedly introduced a number of bugs (and removed some features).

 Bug reports should be directed to the OpenTripPlanner project, unless they
 can be reproduced in the original VisiLibity.
 \brief environment represented by simple polygonal outer boundary with simple polygonal holes

 \remarks For methods to work correctly, the outer boundary vertices must be listed ccw and the
 hole vertices cw
 */
public class Environment {

    VLPolygon outer_boundary;

    ArrayList<VLPolygon> holes = new ArrayList<VLPolygon>();

    ArrayList<pair<Integer, Integer>> flattened_index_key = new ArrayList<pair<Integer, Integer>>();

    public Environment(List<VLPolygon> polygons) {
        outer_boundary = polygons.get(0);
        for (int i = 1; i < polygons.size(); i++)
            holes.add(polygons.get(i));
        update_flattened_index_key();
    }

    public Environment(VLPolygon polygon_temp) {
        outer_boundary = polygon_temp;
        update_flattened_index_key();
    }

    VLPoint kth_point(int k) {
        pair<Integer, Integer> ij = flattened_index_key.get(k);
        return get(ij.first()).get(ij.second());
    }

    int n() {
        int n_count = 0;
        n_count = outer_boundary.n();
        for (int i = 0; i < h(); i++)
            n_count += holes.get(i).n();
        return n_count;
    }

    int h() {
        return holes.size();
    }

    boolean is_in_standard_form() {
        if (outer_boundary.is_in_standard_form() == false || outer_boundary.area() < 0)
            return false;
        for (int i = 0; i < holes.size(); i++)
            if (holes.get(i).is_in_standard_form() == false || holes.get(i).area() > 0)
                return false;
        return true;
    }

    public boolean is_valid(double epsilon) {
        if (n() <= 2)
            return false;

        // Check all Polygons are simple.
        if (!outer_boundary.is_simple(epsilon)) {
            /*
             * std::cerr << std::endl << "\x1b[31m" << "The outer boundary is not simple." <<
             * "\x1b[0m" << std::endl;
             */
            return false;
        }
        for (int i = 0; i < h(); i++)
            if (!holes.get(i).is_simple(epsilon)) {
                /*
                 * std::cerr << std::endl << "\x1b[31m" << "Hole " << i << " is not simple." <<
                 * "\x1b[0m" << std::endl;
                 */
                return false;
            }

        // Check none of the Polygons' boundaries intersect w/in epsilon.
        for (int i = 0; i < h(); i++)
            if (outer_boundary.boundary_distance(holes.get(i)) <= epsilon) {
                /*
                 * std::cerr << std::endl << "\x1b[31m" <<
                 * "The outer boundary intersects the boundary of hole " << i << "." << "\x1b[0m" <<
                 * std::endl;
                 */
                return false;
            }
        for (int i = 0; i < h(); i++)
            for (int j = i + 1; j < h(); j++)
                if (holes.get(i).boundary_distance(holes.get(j)) <= epsilon) {
                    /*
                     * std::cerr << std::endl << "\x1b[31m" << "The boundary of hole " << i <<
                     * " intersects the boundary of hole " << j << "." << "\x1b[0m" << std::endl;
                     */
                    return false;
                }

        // Check that the vertices of each hole are in the outside_boundary
        // and not in any other holes.
        // Loop over holes.
        for (int i = 0; i < h(); i++) {
            // Loop over vertices of a hole
            for (int j = 0; j < holes.get(i).n(); j++) {
                if (!holes.get(i).get(j).in(outer_boundary, epsilon)) {
                    /*
                     * std::cerr << std::endl << "\x1b[31m" << "Vertex " << j << " of hole " << i <<
                     * " is not within the outer boundary." << "\x1b[0m" << std::endl;
                     */
                    return false;
                }
                // Second loop over holes.
                for (int k = 0; k < h(); k++)
                    if (i != k && holes.get(i).get(j).in(holes.get(k), epsilon)) {
                        /*
                         * std::cerr << std::endl << "\x1b[31m" << "Vertex " << j << " of hole " <<
                         * i << " is in hole " << k << "." << "\x1b[0m" << std::endl;
                         */
                        return false;
                    }
            }
        }

        // Check outer_boundary is ccw and holes are cw.
        if (outer_boundary.area() <= 0) {
            /*
             * std::cerr << std::endl << "\x1b[31m" <<
             * "The outer boundary vertices are not listed ccw." << "\x1b[0m" << std::endl;
             */
            return false;
        }
        for (int i = 0; i < h(); i++)
            if (holes.get(i).area() >= 0) {
                /*
                 * std::cerr << std::endl << "\x1b[31m" << "The vertices of hole " << i <<
                 * " are not listed cw." << "\x1b[0m" << std::endl;
                 */
                return false;
            }

        return true;
    }

    public VLPolygon get(int i) {
        if (i == 0) {
            return outer_boundary;
        } else {
            return holes.get(i - 1);
        }
    }

    public void enforce_standard_form() {
        if (outer_boundary.area() < 0)
            outer_boundary.reverse();
        outer_boundary.enforce_standard_form();
        for (int i = 0; i < h(); i++) {
            if (holes.get(i).area() > 0)
                holes.get(i).reverse();
            holes.get(i).enforce_standard_form();
        }
    }

    void update_flattened_index_key() {
        flattened_index_key.clear();

        for (int i = 0; i <= h(); i++) {
            for (int j = 0; j < get(i).n(); j++) {
                pair<Integer, Integer> pair_temp = new pair<Integer, Integer>(i, j);
                flattened_index_key.add(pair_temp);
            }
        }
    }

    public String toString() {
        String outs = "//Environment Model\n";
        outs += "//Outer Boundary\n" + get(0);
        for (int i = 1; i <= h(); i++) {
            outs += "//Hole\n " + get(i);
        }
        // outs << "//EOF marker";
        return outs;
    }

}