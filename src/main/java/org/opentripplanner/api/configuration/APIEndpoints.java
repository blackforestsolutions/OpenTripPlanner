package org.opentripplanner.api.configuration;

import org.opentripplanner.api.resource.PlannerResource;
import org.opentripplanner.index.IndexAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Configure API resource endpoints.
 */
public class APIEndpoints {

    private final List<Class<?>> resources = new ArrayList<>();

    private APIEndpoints() {
        // Add mandatory APIs
        add(PlannerResource.class);
        add(IndexAPI.class);
    }

    /**
     * List all mandatory and feature enabled endpoints as Jersey resource
     * classes: define web services, i.e. an HTTP APIs.
     * <p>
     * Some of the endpoints can be turned on/off using {@link OTPFeature}s, this
     * method check if an endpoint is enabled before adding it to the list.
     *
     * @return all mandatory and feature enabled endpoints are returned.
     */
    public static Collection<? extends Class<?>> listAPIEndpoints() {
        return Collections.unmodifiableCollection(new APIEndpoints().resources);
    }

    private void add(Class<?> resource) {
        resources.add(resource);
    }
}
