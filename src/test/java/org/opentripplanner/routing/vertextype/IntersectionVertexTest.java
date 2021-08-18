package org.opentripplanner.routing.vertextype;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntersectionVertexTest {

    private Graph graph;

    private StreetEdge fromEdge;
    private StreetEdge straightAheadEdge;
    
    @Before
    public void before() {
        graph = new Graph();

        // Graph for a fictional grid city with turn restrictions
        StreetVertex maple1 = vertex("maple_1st", 2.0, 2.0);
        StreetVertex maple2 = vertex("maple_2nd", 1.0, 2.0);
        StreetVertex maple3 = vertex("maple_3rd", 0.0, 2.0);

        // Each block along the main streets has unit length and is one-way
        StreetEdge maple1_2 = edge(maple1, maple2, 100.0, false);
        StreetEdge maple2_3 = edge(maple2, maple3, 100.0, false);

        this.fromEdge = maple1_2;
        this.straightAheadEdge = maple2_3;
    }

    @Test
    public void testFreeFlowing() {
        IntersectionVertex iv = new IntersectionVertex(graph, "vertex", 1.0, 2.0);
        assertFalse(iv.freeFlowing);
        
        iv.freeFlowing = true;
        assertTrue(iv.freeFlowing);
    }
    
    @Test
    public void testInferredFreeFlowing() {
        IntersectionVertex iv = new IntersectionVertex(graph, "vertex", 1.0, 2.0);
        assertFalse(iv.trafficLight);
        assertFalse(iv.inferredFreeFlowing());
        assertEquals(0, iv.getDegreeIn());
        assertEquals(0, iv.getDegreeOut());
        
        iv.trafficLight = true;
        assertTrue(iv.trafficLight);
        assertFalse(iv.inferredFreeFlowing());
        
        iv.addIncoming(fromEdge);
        assertEquals(1, iv.getDegreeIn());
        assertEquals(0, iv.getDegreeOut());
        assertFalse(iv.inferredFreeFlowing());
        
        iv.addOutgoing(straightAheadEdge);
        assertEquals(1, iv.getDegreeIn());
        assertEquals(1, iv.getDegreeOut());
        assertFalse(iv.inferredFreeFlowing());
        
        iv.trafficLight = false;
        assertFalse(iv.trafficLight);
        assertTrue(iv.inferredFreeFlowing());
        
        // Set the freeFlowing bit to false.
        iv.freeFlowing = false;
        assertFalse(iv.freeFlowing);       
    }

    /****
     * Private Methods
     ****/

    private StreetVertex vertex(String label, double lat, double lon) {
        IntersectionVertex v = new IntersectionVertex(graph, label, lat, lon);
        return v;
    }

    /**
     * Create an edge. If twoWay, create two edges (back and forth).
     * 
     * @param vA
     * @param vB
     * @param length
     * @param back true if this is a reverse edge
     */
    private StreetEdge edge(StreetVertex vA, StreetVertex vB, double length, boolean back) {
        String labelA = vA.getLabel();
        String labelB = vB.getLabel();
        String name = String.format("%s_%s", labelA, labelB);
        Coordinate[] coords = new Coordinate[2];
        coords[0] = vA.getCoordinate();
        coords[1] = vB.getCoordinate();
        LineString geom = GeometryUtils.getGeometryFactory().createLineString(coords);

        StreetTraversalPermission perm = StreetTraversalPermission.ALL;
        return new StreetEdge(vA, vB, geom, name, length, perm, back);
    }
}
