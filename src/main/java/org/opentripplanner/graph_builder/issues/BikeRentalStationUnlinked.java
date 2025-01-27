package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;
import org.opentripplanner.routing.vertextype.BikeRentalStationVertex;

public class BikeRentalStationUnlinked implements DataImportIssue {

    public static final String FMT = "Bike rental station %s not near any streets; it will not be usable.";
    private static final String HTMLFMT = "Bike rental station <a href=\"http://www.openstreetmap.org/?mlat=%s&mlon=%s\">\"%s\"</a> not near any streets; it will not be usable.";
    
    final BikeRentalStationVertex station;
    
    public BikeRentalStationUnlinked(BikeRentalStationVertex station){
    	this.station = station;
    }

    @Override
    public String getMessage() {
        return String.format(FMT, station);
    }

}
