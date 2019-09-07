package info.rsdev.mysite.common.startup;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

public class PropertiesModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesModule.class);

    private final File contentRoot;

    private final String contextPath;

    public PropertiesModule(File contentRoot, String contextPath) {
        this.contentRoot = contentRoot;
        logger.info(String.format("Using Servlet ContextPath: %s", contextPath));
        this.contextPath = contextPath;
    }

    @Override
    protected void configure() {
        logger.info("Configured PropertiesModule");
    }

    @Singleton
    @Provides
    @ContentRoot
    public File provideContentRoot() {
        return this.contentRoot;
    }

    @Singleton
    @Provides
    @ContextPath
    public String provideServletContextPath() {
        return this.contextPath;
    }

    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public @interface ContentRoot {
    }

    @BindingAnnotation
    @Target({FIELD, PARAMETER, METHOD})
    @Retention(RUNTIME)
    public @interface ContextPath {
    }

}
