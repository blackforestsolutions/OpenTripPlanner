package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;
import org.opentripplanner.routing.graph.Vertex;

public class GraphConnectivity implements DataImportIssue {

    public static final String FMT = "Removed/depedestrianized disconnected subgraph containing vertex '%s' at (%f, %f), with %d edges";
    public static final String HTMLFMT = "Removed/depedestrianized disconnected subgraph containing vertex <a href='http://www.openstreetmap.org/node/%s'>'%s'</a>, with %d edges";

    final Vertex vertex;
    final int size;
    
    public GraphConnectivity(Vertex vertex, int size){
    	this.vertex = vertex;
    	this.size = size;
    }

    @Override
    public String getMessage() {
        return String.format(FMT, vertex, vertex.getCoordinate().x, vertex.getCoordinate().y, size);
    }

}
