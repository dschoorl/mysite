package info.rsdev.mysite.common.startup;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Properties;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;

public class PropertiesModule extends AbstractModule {    
    
    private static final Logger logger = LoggerFactory.getLogger(PropertiesModule.class);
    
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String SHOW_SQL_KEY = "showsql";
    
    public static final String JDBC_URL_FORMAT = "jdbc:mysql://%s:%s/mysite";
    
    private final File contentRoot;
    
    private final String contextPath;
    
    public PropertiesModule(File contentRoot, String contextPath) {
        this.contentRoot = contentRoot;
        logger.info(String.format("Using Servlet ContextPath: %s", contextPath));
        this.contextPath = contextPath;
    }

    @Override
    protected void configure() {
        bind(Properties.class).annotatedWith(Jdbc.class).toProvider(JdbcPropertiesProvider.class).asEagerSingleton();
        logger.info("Configured PropertiesModule");
    }
    
    @Provides @Singleton @ContentRoot
    public File provideContentRoot() {
        return this.contentRoot;
    }
    
    @Provides @Singleton @ContextPath
    public String provideServletContextPath() {
        return this.contextPath;
    }
    
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface ContentRoot {}

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface ContextPath {}

    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface Jdbc {}
    
}
