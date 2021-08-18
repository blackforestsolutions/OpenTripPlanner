package org.opentripplanner.graph_builder.module.osm;

public class SlopeOverridePicker {
    private OSMSpecifier specifier;

    private boolean override;

    public SlopeOverridePicker(OSMSpecifier specifier, boolean override) {
        this.specifier = specifier;
        this.override = override;
    }

    public OSMSpecifier getSpecifier() {
        return specifier;
    }

    public boolean getOverride() {
        return override;
    }

}
