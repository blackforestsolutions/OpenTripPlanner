package org.opentripplanner.graph_builder.issues;

import org.opentripplanner.graph_builder.DataImportIssue;
import org.opentripplanner.routing.vertextype.TransitEntranceVertex;

public class EntranceUnlinked implements DataImportIssue {

  public static final String FMT = "Entrance %s not near any streets; it will not be usable.";
  public static final String HTMLFMT = "Entrance <a href=\"http://www.openstreetmap.org/?mlat=%s&mlon=%s&layers=T\">\"%s\" (%s)</a> not near any streets; it will not be usable.";

  final TransitEntranceVertex entrance;

  public EntranceUnlinked(TransitEntranceVertex entrance) {
    this.entrance = entrance;
  }

  @Override
  public String getMessage() {
    return String.format(FMT, entrance);
  }

}
