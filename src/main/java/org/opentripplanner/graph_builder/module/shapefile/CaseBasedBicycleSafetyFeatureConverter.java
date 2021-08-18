package org.opentripplanner.graph_builder.module.shapefile;

import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;
import org.opentripplanner.common.model.P2;
import org.opentripplanner.graph_builder.services.shapefile.SimpleFeatureConverter;

/**
 * Handles marking certain types of streets/bike lanes as more or less safe for bike trips.
 */
public class CaseBasedBicycleSafetyFeatureConverter implements SimpleFeatureConverter<P2<Double>> {

    private String safetyAttributeName;
    private String directionAttributeName;

    private Map<String, Double> safetyFeatures = new HashMap<String, Double>();
    private Map<String, Integer> directions = new HashMap<String, Integer>();
    public static final P2<Double> oneone = new P2<Double>(1.0, 1.0);

    @Override
    public P2<Double> convert(SimpleFeature feature) {
        String safetyKey = feature.getAttribute(safetyAttributeName).toString();
        Double safetyFeature = safetyFeatures.get(safetyKey);
        if (safetyFeature == null)
            return oneone;

        int directionFeature = 3; // Default to applying the safety feature in both directions
                                  // (useful if the dataset doesn't include direction information)
        if (directionAttributeName != null) {
        	String directionKey = feature.getAttribute(directionAttributeName).toString();
        	if (directionKey != null) {
        		directionFeature = directions.get(directionKey.toString());
        	}
        }

        return new P2<Double>((directionFeature & 0x1) == 0 ? 1.0 : safetyFeature,
                (directionFeature & 0x2) == 0 ? 1.0 : safetyFeature);
    }

    public CaseBasedBicycleSafetyFeatureConverter(String safetyAttributeName,
            String directionAttributeName) {
        this.safetyAttributeName = safetyAttributeName;
        this.directionAttributeName = directionAttributeName;
    }

    public CaseBasedBicycleSafetyFeatureConverter() {
    }
}
