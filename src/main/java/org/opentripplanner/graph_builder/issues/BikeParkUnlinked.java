package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;
import org.opentripplanner.routing.vertextype.BikeParkVertex;

public class BikeParkUnlinked implements DataImportIssue {

    private static final String FMT = "Bike park %s not near any streets; it will not be usable.";
    private static final String HTMLFMT = "Bike park <a href=\"http://www.openstreetmap.org/?mlat=%s&mlon=%s\">\"%s\"</a> not near any streets; it will not be usable.";

    final BikeParkVertex bikeParkVertex;

    public BikeParkUnlinked(BikeParkVertex bikeParkVertex) {
        this.bikeParkVertex = bikeParkVertex;
    }

    @Override
    public String getMessage() {
        return String.format(FMT, bikeParkVertex);
    }

}
