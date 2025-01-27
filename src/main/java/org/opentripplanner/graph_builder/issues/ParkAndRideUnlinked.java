package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;

public class ParkAndRideUnlinked implements DataImportIssue {

    public static final String FMT = "Park and ride '%s' (%d) not linked to any streets; it will not be usable.";
    public static final String HTMLFMT = "Park and ride <a href='http://www.openstreetmap.org/way/%d'>'%s' (%d)</a> not linked to any streets; it will not be usable.";
    
    final String name;
    final long osmId;
    
    public ParkAndRideUnlinked(String name, long osmId){
    	this.name = name;
    	this.osmId = osmId;
    }
    
    @Override
    public String getMessage() {
        return String.format(FMT, name, osmId);
    }

}
