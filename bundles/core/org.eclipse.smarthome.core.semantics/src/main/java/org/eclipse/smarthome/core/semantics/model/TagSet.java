package org.eclipse.smarthome.core.semantics.model;

import java.util.Locale;


public interface TagSet {

    public static String name() {
        return null;
    }

    public String label(Locale locale);

    public String description(Locale locale);
}
