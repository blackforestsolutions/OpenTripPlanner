package org.opentripplanner.routing.edgetype;

import java.io.Serializable;

import org.locationtech.jts.geom.Geometry;
import org.opentripplanner.util.I18NString;

/**
 * A named area is a subset of an area with a certain set of properties
 * (name, safety, etc). Its originalEdges may include some edges which are 
 * crossable (because they separate it from another contiguous and
 * routeable area).
 * 
 */
public class NamedArea implements Serializable {
    private static final long serialVersionUID = 3570078249065754760L;

    private Geometry originalEdges;

    private I18NString name;

    private double bicycleSafetyMultiplier;

    private int streetClass;

    private StreetTraversalPermission permission;

    public double getBicycleSafetyMultiplier() {
        return bicycleSafetyMultiplier;
    }

    public void setBicycleSafetyMultiplier(double bicycleSafetyMultiplier) {
        this.bicycleSafetyMultiplier = bicycleSafetyMultiplier;
    }
}
