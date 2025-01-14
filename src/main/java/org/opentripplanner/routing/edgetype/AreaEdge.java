package org.opentripplanner.routing.edgetype;

import org.locationtech.jts.geom.LineString;
import org.opentripplanner.util.I18NString;
import org.opentripplanner.routing.vertextype.IntersectionVertex;


public class AreaEdge extends StreetWithElevationEdge implements EdgeWithCleanup {
    private static final long serialVersionUID = 6761687673982054612L;
    private AreaEdgeList area;

    public AreaEdge(IntersectionVertex startEndpoint, IntersectionVertex endEndpoint,
            LineString geometry, I18NString name, double length, StreetTraversalPermission permissions,
            boolean back, AreaEdgeList area) {
        super(startEndpoint, endEndpoint, geometry, name, length, permissions, back);
        this.area = area;
        area.addEdge(this);
    }

    public AreaEdgeList getArea() {
        return area;
    }

    @Override
    public void detach() {
        area.removeEdge(this);
    }
}
