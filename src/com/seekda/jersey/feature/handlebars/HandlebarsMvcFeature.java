/*
 *	Based on the MustacheMvcFeature.java class of Jersey 2.22
 *	Portions Copyright 2013-2015 Oracle and/or its affiliates.
 */

package com.seekda.jersey.feature.handlebars;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.server.mvc.MvcFeature;

/*
 * {@link Feature} used to add support for {@link MvcFeature MVC} and Handlebars templates.
 * <p/>
 * Note: This feature also registers {@link MvcFeature}.
 * <p/>
 * Based on the official {@link MustacheMvcFeature} of the
 *
 * @author David Hainzl (david.hainzl@seekda.com)
 */
@ConstrainedTo(RuntimeType.SERVER)
public class HandlebarsMvcFeature implements Feature {

    private static final String SUFFIX = ".handlebars";

    /**
     * {@link String} property defining the base path to Handlebars templates. If set, the value of the property is added in front
     * of the template name defined in:
     * <ul>
     * <li>{@link org.glassfish.jersey.server.mvc.Viewable Viewable}</li>
     * <li>{@link org.glassfish.jersey.server.mvc.Template Template}, or</li>
     * <li>{@link org.glassfish.jersey.server.mvc.ErrorTemplate ErrorTemplate}</li>
     * </ul>
     * <p/>
     * Value is relative to current {@link javax.servlet.ServletContext servlet context}.
     * <p/>
     * There is no default value.
     * <p/>
     * The name of the configuration property is <tt>{@value}</tt>.
     */
    public static final String TEMPLATE_BASE_PATH = MvcFeature.TEMPLATE_BASE_PATH + SUFFIX;

    /**
     * {@link String} property defining the default file suffix (without dot) of the Handlebars templates.
     * <p/>
     * Default value is "handlebars"
     */
    public static final String FILE_SUFFIX = "jersey.config.server.mvc.suffix" + SUFFIX;

    /**
     * A comma-separated {@link String} property to define one or more classes which's static methods
     * will be injected as handlers into each template.
     */
    public static final String DEFAULT_HELPERS = "jersey.config.server.mvc.helpers" + SUFFIX;

    @Override
    public boolean configure(final FeatureContext context) {
        final Configuration config = context.getConfiguration();

        if (!config.isRegistered(HandlebarsTemplateProcessor.class)) {
            // Template Processor.
            context.register(HandlebarsTemplateProcessor.class);

            // MvcFeature.
            if (!config.isRegistered(MvcFeature.class)) {
                context.register(MvcFeature.class);
            }

            return true;
        }
        return false;
    }
}