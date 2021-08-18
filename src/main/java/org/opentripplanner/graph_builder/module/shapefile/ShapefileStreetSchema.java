package org.opentripplanner.graph_builder.module.shapefile;

import org.opentripplanner.common.model.P2;
import org.opentripplanner.graph_builder.services.shapefile.SimpleFeatureConverter;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;

public class ShapefileStreetSchema {

    private SimpleFeatureConverter<String> idConverter = new FeatureIdConverter();

    private SimpleFeatureConverter<String> nameConverter;

    private SimpleFeatureConverter<P2<StreetTraversalPermission>> permissionConverter = new CaseBasedTraversalPermissionConverter();

    private SimpleFeatureConverter<P2<Double>> bicycleSafetyConverter = null;

    private SimpleFeatureConverter<Boolean> slopeOverrideConverter = new CaseBasedBooleanConverter();

    private SimpleFeatureConverter<Boolean> featureSelector = null;

    private SimpleFeatureConverter<String> noteConverter = null;

    public SimpleFeatureConverter<String> getIdConverter() {
        return idConverter;
    }

    /**
     * The ID attribute is used to uniquely identify street segments. This is useful if a given
     * street segment appears multiple times in a shapefile.
     */
    public void setIdAttribute(String attributeName) {
        this.idConverter = new AttributeFeatureConverter<String>(attributeName);
    }

    public SimpleFeatureConverter<String> getNameConverter() {
        return nameConverter;
    }

    public void setNameAttribute(String attributeName) {
        this.nameConverter = new AttributeFeatureConverter<String>(attributeName);
    }

    /**
     * The permission converter gets the {@link StreetTraversalPermission} for a street segment and
     * its reverse.
     * 
     * @return
     */
    public SimpleFeatureConverter<P2<StreetTraversalPermission>> getPermissionConverter() {
        return permissionConverter;
    }

    public void setPermissionConverter(
            SimpleFeatureConverter<P2<StreetTraversalPermission>> permissionConverter) {
        this.permissionConverter = permissionConverter;
    }

    public SimpleFeatureConverter<P2<Double>> getBicycleSafetyConverter() {
        return bicycleSafetyConverter;
    }

    /**
     * @see setSlopeOverrideConverter
     * @return
     */
    public SimpleFeatureConverter<Boolean> getSlopeOverrideConverter() {
        return slopeOverrideConverter;
    }

    /**
     * @param featureSelector
     *            A featureSelector returns true if a feature is a street, and false otherwise.
     *            Useful for centerline files that also have non-streets, such as political
     *            boundaries or coastlines
     */
    public void setFeatureSelector(SimpleFeatureConverter<Boolean> featureSelector) {
        this.featureSelector = featureSelector;
    }

    /**
     * @see setFeatureSelector
     * @return the current feature selector
     */
    public SimpleFeatureConverter<Boolean> getFeatureSelector() {
        return featureSelector;
    }

	public SimpleFeatureConverter<String> getNoteConverter() {
		return noteConverter;
	}
}
