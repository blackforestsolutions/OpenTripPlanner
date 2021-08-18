package org.opentripplanner.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The purpose of Properties is to easily read a ResourceBundel (set of localized .properties files), and get the named contents.
 * Goes really well with an enumerated type (@see org.opentripplanner.api.ws.Message)
 */
public class Properties {

    public static final Logger LOG = LoggerFactory.getLogger(Properties.class);

    private final String bundle;

    public Properties() {
        this(Properties.class);
    }

    public Properties(Class<?> c) {
        bundle = c.getSimpleName();
    }

    public Properties(String bundle) {
        this.bundle = bundle;
    }

    /** 
     * static .properties resource loader
     * will first look for a resource org.opentripplaner.blah.blah.blah.ClassName.properties.
     * if that doesn't work, it searches for ClassName.properties.
     */
    public static ResourceBundle getBundle(String name, Locale l) {
        try {
            return ResourceBundle.getBundle(name, l);
        }
        catch(Exception e) {
            LOG.error("Uh oh...no .properties file could be found, so things are most definately not going to turn out well!!!", e);
        }
        return null;
    }

    public synchronized String get(String name, Locale l) throws Exception {
        ResourceBundle rb = getBundle(bundle, l);
        return rb.getString(name);
    }
}
