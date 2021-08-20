package org.opentripplanner.graph_builder.module.osm;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.opentripplanner.common.geometry.GeometryUtils;
import org.opentripplanner.common.geometry.SphericalDistanceLibrary;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.graph_builder.DataImportIssueStore;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule.Handler;
import org.opentripplanner.graph_builder.services.StreetEdgeFactory;
import org.opentripplanner.openstreetmap.model.OSMNode;
import org.opentripplanner.openstreetmap.model.OSMWithTags;
import org.opentripplanner.routing.edgetype.AreaEdge;
import org.opentripplanner.routing.edgetype.AreaEdgeList;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.IntersectionVertex;
import org.opentripplanner.util.I18NString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Theoretically, it is not correct to build the visibility graph on the joined polygon of areas
 * with different levels of bike safety. That's because in the optimal path, you might end up
 * changing direction at area boundaries. The problem is known as "weighted planar subdivisions",
 * and the best known algorithm is O(N^3). That's not much worse than general visibility graph
 * construction, but it would have to be done at runtime to account for the differences in bike
 * safety preferences. Ted Chiang's "Story Of Your Life" describes how a very similar problem in
 * optics gives rise to Snell's Law. It is the second-best story about a law of physics that I know
 * of (Chiang's "Exhalation" is the first).
 * <p>
 * Anyway, since we're not going to run an O(N^3) algorithm at runtime just to give people who don't
 * understand Snell's Law weird paths that they can complain about, this should be just fine.
 * </p>
 * 
 */
public class WalkableAreaBuilder {

    private static Logger LOG = LoggerFactory.getLogger(WalkableAreaBuilder.class);

    private DataImportIssueStore issueStore;

    public static final int MAX_AREA_NODES = 500;

    public static final double VISIBILITY_EPSILON = 0.000000001;

    private Graph graph;

    private OSMDatabase osmdb;

    private WayPropertySet wayPropertySet;

    private StreetEdgeFactory edgeFactory;

    // This is an awful hack, but this class (WalkableAreaBuilder) ought to be rewritten.
    private Handler handler;

    private HashMap<Coordinate, IntersectionVertex> areaBoundaryVertexForCoordinate = new HashMap<Coordinate, IntersectionVertex>();

    public WalkableAreaBuilder(Graph graph, OSMDatabase osmdb, WayPropertySet wayPropertySet,
            StreetEdgeFactory edgeFactory, Handler handler, DataImportIssueStore issueStore
    ) {
        this.graph = graph;
        this.osmdb = osmdb;
        this.wayPropertySet = wayPropertySet;
        this.edgeFactory = edgeFactory;
        this.handler = handler;
        this.issueStore = issueStore;
    }

    /**
     * For all areas just use outermost rings as edges so that areas can be routable without visibility calculations
     * @param group
     */
    public void buildWithoutVisibility(AreaGroup group) {
        Set<Edge> edges = new HashSet<Edge>();

        // create polygon and accumulate nodes for area
        for (Ring ring : group.outermostRings) {

            AreaEdgeList edgeList = new AreaEdgeList();
            // the points corresponding to concave or hole vertices
            // or those linked to ways
            HashSet<P2<OSMNode>> alreadyAddedEdges = new HashSet<P2<OSMNode>>();

            // we also want to fill in the edges of this area anyway, because we can,
            // and to avoid the numerical problems that they tend to cause
            for (Area area : group.areas) {

                if (!ring.toJtsPolygon().contains(area.toJTSMultiPolygon())) {
                    continue;
                }

                for (Ring outerRing : area.outermostRings) {
                    for (int i = 0; i < outerRing.nodes.size(); ++i) {
                        createEdgesForRingSegment(edges, edgeList, area, outerRing, i,
                            alreadyAddedEdges);
                    }
                    //TODO: is this actually needed?
                    for (Ring innerRing : outerRing.holes) {
                        for (int j = 0; j < innerRing.nodes.size(); ++j) {
                            createEdgesForRingSegment(edges, edgeList, area, innerRing, j,
                                alreadyAddedEdges);
                        }
                    }
                }
            }
        }
    }

    private void createEdgesForRingSegment(Set<Edge> edges, AreaEdgeList edgeList, Area area,
            Ring ring, int i, HashSet<P2<OSMNode>> alreadyAddedEdges) {
        OSMNode node = ring.nodes.get(i);
        OSMNode nextNode = ring.nodes.get((i + 1) % ring.nodes.size());
        P2<OSMNode> nodePair = new P2<OSMNode>(node, nextNode);
        if (alreadyAddedEdges.contains(nodePair)) {
            return;
        }
        alreadyAddedEdges.add(nodePair);
        IntersectionVertex startEndpoint = handler.getVertexForOsmNode(node, area.parent);
        IntersectionVertex endEndpoint = handler.getVertexForOsmNode(nextNode, area.parent);

        createSegments(node, nextNode, startEndpoint, endEndpoint, Arrays.asList(area), edgeList,
                edges);
    }

    private void createSegments(OSMNode fromNode, OSMNode toNode, IntersectionVertex startEndpoint,
            IntersectionVertex endEndpoint, Collection<Area> areas, AreaEdgeList edgeList,
            Set<Edge> edges) {

        List<Area> intersects = new ArrayList<Area>();

        Coordinate[] coordinates = new Coordinate[] { startEndpoint.getCoordinate(),
                endEndpoint.getCoordinate() };
        GeometryFactory geometryFactory = GeometryUtils.getGeometryFactory();
        LineString line = geometryFactory.createLineString(coordinates);
        for (Area area : areas) {
            MultiPolygon polygon = area.toJTSMultiPolygon();
            Geometry intersection = polygon.intersection(line);
            if (intersection.getLength() > 0.000001) {
                intersects.add(area);
            }
        }
        if (intersects.size() == 0) {
            // apparently our intersection here was bogus
            return;
        }
        // do we need to recurse?
        if (intersects.size() == 1) {
            Area area = intersects.get(0);
            OSMWithTags areaEntity = area.parent;

            StreetTraversalPermission areaPermissions = OSMFilter.getPermissionsForEntity(
                    areaEntity, StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE);

            float carSpeed = wayPropertySet.getCarSpeedForWay(areaEntity, false);

            double length = SphericalDistanceLibrary.distance(startEndpoint.getCoordinate(),
                    endEndpoint.getCoordinate());

            int cls = StreetEdge.CLASS_OTHERPATH;
            cls |= OSMFilter.getStreetClasses(areaEntity);

            String label = "way (area) " + areaEntity.getId() + " from " + startEndpoint.getLabel()
                    + " to " + endEndpoint.getLabel();
            I18NString name = handler.getNameForWay(areaEntity, label);

            AreaEdge street = edgeFactory.createAreaEdge(startEndpoint, endEndpoint, line, name,
                    length, areaPermissions, false, edgeList);
            street.setCarSpeed(carSpeed);

            if (!areaEntity.hasTag("name") && !areaEntity.hasTag("ref")) {
                street.setHasBogusName(true);
            }

            if (areaEntity.isTagFalse("wheelchair")) {
                street.setWheelchairAccessible(false);
            }

            street.setStreetClass(cls);
            edges.add(street);

            label = "way (area) " + areaEntity.getId() + " from " + endEndpoint.getLabel() + " to "
                    + startEndpoint.getLabel();
            name = handler.getNameForWay(areaEntity, label);

            AreaEdge backStreet = edgeFactory.createAreaEdge(endEndpoint, startEndpoint,
                    (LineString) line.reverse(), name, length, areaPermissions, true, edgeList);
            backStreet.setCarSpeed(carSpeed);

            if (!areaEntity.hasTag("name") && !areaEntity.hasTag("ref")) {
                backStreet.setHasBogusName(true);
            }

            if (areaEntity.isTagFalse("wheelchair")) {
                backStreet.setWheelchairAccessible(false);
            }

            backStreet.setStreetClass(cls);
            edges.add(backStreet);

            WayProperties wayData = wayPropertySet.getDataForWay(areaEntity);
            handler.applyWayProperties(street, backStreet, wayData, areaEntity);

        } else {
            // take the part that intersects with the start vertex
            Coordinate startCoordinate = startEndpoint.getCoordinate();
            Point startPoint = geometryFactory.createPoint(startCoordinate);
            for (Area area : intersects) {
                MultiPolygon polygon = area.toJTSMultiPolygon();
                if (!(polygon.intersects(startPoint) || polygon.getBoundary()
                        .intersects(startPoint)))
                    continue;
                Geometry lineParts = line.intersection(polygon);
                if (lineParts.getLength() > 0.000001) {
                    Coordinate edgeCoordinate = null;
                    // this is either a LineString or a MultiLineString (we hope)
                    if (lineParts instanceof MultiLineString) {
                        MultiLineString mls = (MultiLineString) lineParts;
                        boolean found = false;
                        for (int i = 0; i < mls.getNumGeometries(); ++i) {
                            LineString segment = (LineString) mls.getGeometryN(i);
                            if (found) {
                                edgeCoordinate = segment.getEndPoint().getCoordinate();
                                break;
                            }
                            if (segment.contains(startPoint)
                                    || segment.getBoundary().contains(startPoint)) {
                                found = true;
                                if (segment.getLength() > 0.000001) {
                                    edgeCoordinate = segment.getEndPoint().getCoordinate();
                                    break;
                                }
                            }
                        }
                    } else if (lineParts instanceof LineString) {
                        edgeCoordinate = ((LineString) lineParts).getEndPoint().getCoordinate();
                    } else {
                        continue;
                    }

                    IntersectionVertex newEndpoint = areaBoundaryVertexForCoordinate
                            .get(edgeCoordinate);
                    if (newEndpoint == null) {
                        newEndpoint = new IntersectionVertex(graph, "area splitter at "
                                + edgeCoordinate, edgeCoordinate.x, edgeCoordinate.y);
                        areaBoundaryVertexForCoordinate.put(edgeCoordinate, newEndpoint);
                    }
                    createSegments(fromNode, toNode, startEndpoint, newEndpoint,
                            Arrays.asList(area), edgeList, edges);
                    createSegments(fromNode, toNode, newEndpoint, endEndpoint, intersects,
                            edgeList, edges);
                    break;
                }
            }
        }
    }
}
