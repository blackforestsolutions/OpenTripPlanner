package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;
import org.opentripplanner.routing.vertextype.TransitStopVertex;

public class StopUnlinked implements DataImportIssue {

    public static final String FMT = "Stop %s not near any streets; it will not be usable.";
    public static final String HTMLFMT = "Stop <a href=\"http://www.openstreetmap.org/?mlat=%s&mlon=%s&layers=T\">\"%s\" (%s)</a> not near any streets; it will not be usable.";
    
    final TransitStopVertex stop;
    
    public StopUnlinked(TransitStopVertex stop){
    	this.stop = stop;
    }
    
    @Override
    public String getMessage() {
        return String.format(FMT, stop);
    }

}
