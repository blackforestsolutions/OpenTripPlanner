package org.opentripplanner.routing.edgetype;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.common.geometry.SphericalDistanceLibrary;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.IntersectionVertex;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * This is a representation of a set of contiguous OSM areas, used for various tasks related to edge splitting, such as start/endpoint snapping and
 * adding new edges during transit linking.
 * 
 * @author novalis
 */
public class AreaEdgeList implements Serializable {
    private static final long serialVersionUID = 969137349467214074L;

    private ArrayList<AreaEdge> edges = new ArrayList<AreaEdge>();

    private HashSet<IntersectionVertex> vertices = new HashSet<IntersectionVertex>();

    // these are all of the original edges of the area, whether
    // or not there are corresponding OSM edges. It is used as part of a hack
    // to fix up areas after network linking.
    private Polygon originalEdges;

    private List<NamedArea> areas = new ArrayList<NamedArea>();

    public List<AreaEdge> getEdges() {
        return edges;
    }

    public void addEdge(AreaEdge edge) {
        edges.add(edge);
        vertices.add((IntersectionVertex) edge.getFromVertex());
    }

    public void removeEdge(AreaEdge edge) {
        edges.remove(edge);
        // reconstruct vertices
        vertices.clear();
        for (Edge e : edges) {
            vertices.add((IntersectionVertex) e.getFromVertex());
        }
    }

    public void setOriginalEdges(Polygon polygon) {
        this.originalEdges = polygon;
    }

    public void addArea(NamedArea namedArea) {
        areas.add(namedArea);
    }

    public List<NamedArea> getAreas() {
        return areas;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        edges.trimToSize();
        out.defaultWriteObject();
    }
}
