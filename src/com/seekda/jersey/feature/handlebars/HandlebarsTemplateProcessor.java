/*
 *	Based on the MustacheTemplateProcessor.java class of Jersey 2.22
 *	Portions Copyright 2013-2015 Oracle and/or its affiliates.
 */

package com.seekda.jersey.feature.handlebars;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.mvc.spi.AbstractTemplateProcessor;
import org.glassfish.jersey.server.mvc.spi.TemplateProcessor;
import org.jvnet.hk2.annotations.Optional;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

/**
 * {@link TemplateProcessor Template processor} providing support for Handlebars templates.
 *
 * @author David Hainzl (david.hainzl@seekda.com)
 * @see HandlebarsMvcFeature
 */
@Singleton
final class HandlebarsTemplateProcessor extends AbstractTemplateProcessor<String> {

	private List<Class<? extends Object>> defaultHandlers = null;
	private String templateSuffix = "";

    private static String getSuffixFromConfig (final Configuration config) {
        String suffix = (String) config.getProperty(HandlebarsMvcFeature.FILE_SUFFIX);
        return (suffix== null ? "handlebars" : suffix);
    }

    /**
     * Create an instance of this processor with injected {@link Configuration config} and
     * (optional) {@link ServletContext servlet context}.
     *
     * @param config configuration to configure this processor from.
     * @param serviceLocator service locator to initialize template object factory if needed.
     * @param servletContext (optional) servlet context to obtain template resources from.
     */
    @Inject
    public HandlebarsTemplateProcessor(final Configuration config, final ServiceLocator serviceLocator,
                                     @Optional final ServletContext servletContext) {
        super(config, servletContext, "handlebars", getSuffixFromConfig(config));
        initFromConfig(config);
    }

	@Override
    protected String resolve(final String templatePath, final Reader reader) throws IOException {
		return templatePath;
    }

	@Override
    public void writeTo(String templatePath, final Viewable viewable, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream out) throws IOException {
    	Object renderData;
    	Handlebars handlebars;
    	TemplateLoader loader = new ClassPathTemplateLoader();
    	int pathEnd = templatePath.lastIndexOf('/');
    	String prefix = templatePath.substring(0, pathEnd);
    	String fileName = templatePath.substring(pathEnd, templatePath.length() - this.templateSuffix.length());

    	loader.setSuffix(this.templateSuffix);
    	loader.setPrefix(prefix);


    	if (viewable.getModel() instanceof HandlebarsModel) {
    		HandlebarsModel handlebarsModel = (HandlebarsModel) viewable.getModel();
    		renderData = handlebarsModel.getData();

    		handlebars = handlebarsModel.getHandlebars();
    		handlebars.with(loader);
    	} else {
    		renderData = viewable.getModel();
    		handlebars = new Handlebars(loader);
    	}

    	if (defaultHandlers != null && defaultHandlers.size() != 0) {
    		for (Class cls : defaultHandlers) {
    			handlebars.registerHelpers(cls);
    		}
    	}

    	Charset encoding = setContentType(mediaType, httpHeaders);

    	final PrintStream ps = new PrintStream(out, false, encoding.name());
    	Template template = handlebars.compile(fileName);
    	ps.print(template.apply(renderData));
    	ps.flush();
    }

	private void initFromConfig(final Configuration config) {
    	// Default Handlers
        String defaultHandler = (String) config.getProperty(HandlebarsMvcFeature.DEFAULT_HANDLERS);
        try {
        	if (defaultHandler != null) {
        		String[] handlers = defaultHandler.split(",");
        		List<Class<? extends Object>> classes = new ArrayList<>();

        		for (String handler : handlers) {
            		classes.add(Class.forName(handler));
        		}

        		this.defaultHandlers = classes;
        	}
        } catch (ClassNotFoundException ex) {
        	System.err.println("Default handler class not found: " + ex.getMessage());
        }

        this.templateSuffix = "." + getSuffixFromConfig(config);
	}
}