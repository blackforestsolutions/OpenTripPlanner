package org.opentripplanner.graph_builder.services;

import org.opentripplanner.routing.edgetype.AreaEdge;
import org.opentripplanner.routing.edgetype.AreaEdgeList;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.vertextype.IntersectionVertex;

import org.locationtech.jts.geom.LineString;
import org.opentripplanner.util.I18NString;

public class DefaultStreetEdgeFactory implements StreetEdgeFactory {

    @Override
    public StreetEdge createEdge(IntersectionVertex startEndpoint, IntersectionVertex endEndpoint,
            LineString geometry, I18NString name, double length, StreetTraversalPermission permissions,
            boolean back) {
        return new StreetEdge(startEndpoint, endEndpoint, geometry, name, length, permissions, back);
    }

    @Override
    public AreaEdge createAreaEdge(IntersectionVertex startEndpoint,
            IntersectionVertex endEndpoint, LineString geometry, I18NString name, double length,
            StreetTraversalPermission permissions, boolean back, AreaEdgeList area) {
        // By default AreaEdge are elevation-capable so nothing to do.
        AreaEdge ae = new AreaEdge(startEndpoint, endEndpoint, geometry, name, length, permissions,
                back, area);
        return ae;
    }
}
