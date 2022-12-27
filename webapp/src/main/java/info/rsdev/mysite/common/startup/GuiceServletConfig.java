package info.rsdev.mysite.common.startup;

import java.io.File;
import java.util.Collection;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletRegistration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import info.rsdev.mysite.util.ServletUtils;

public class GuiceServletConfig extends GuiceServletContextListener {

    /**
     * The name of the OS Environment Variable or Java System property that
     * contains the full path to the directory on the filesystem that contains
     * the applications' data directory.
     */
    public static final String DATA_DIR_VARIABLE_NAME = "MYSITE_DATA_DIR";

    private String contextPath = "not defined";

    @Override
    protected Injector getInjector() {
        File contentRoot = getContentRoot();
        return Guice.createInjector(
                new PropertiesModule(contentRoot, contextPath),
                new MysiteServletModule());

    }

    private File getContentRoot() {
        File contentRoot = null;
        String dataDir = System.getProperty(DATA_DIR_VARIABLE_NAME);
        if (dataDir == null) {
            dataDir = System.getenv(DATA_DIR_VARIABLE_NAME);
            if (dataDir == null) {
                throw new IllegalStateException(String.format("%s is not set. Check setup documentation.", DATA_DIR_VARIABLE_NAME));
            }
        }
        contentRoot = new File(dataDir);
        if (!contentRoot.isDirectory()) {
            throw new IllegalStateException(
                    String.format("Directory '%s' pointed to by %s does not exist. Please create it manually.",
                            dataDir, DATA_DIR_VARIABLE_NAME));
        }
        return contentRoot;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        String servletContextPath = servletContext.getContextPath();
        if (servletContextPath.isEmpty()) {
            servletContextPath = "/";
        }

        String mapping = null;
        String servletName = null; // config.getServletName();
        ServletRegistration registration = servletContext.getServletRegistration(servletName);
        if (registration != null) {
            Collection<String> mappings = registration.getMappings();
            if (mappings.size() > 1) {
                throw new IllegalStateException(String.format("%s must only be mapped to a single url pattern: %s",
                        getClass().getName(), mappings));

            }
            if (!mappings.isEmpty()) {
                mapping = mappings.iterator().next();
            }
        }
        this.contextPath = getContextPath(servletContextPath, mapping);
        super.contextInitialized(servletContextEvent);
    }

    protected String getContextPath(String servletContextPath, String urlMapping) {
        if (urlMapping != null) {
            if (urlMapping.endsWith("/*")) {
                urlMapping = urlMapping.substring(0, urlMapping.length() - 1); // remove
                                                                               // trailing
                                                                               // astrix
            } else if (!urlMapping.endsWith("/")) {
                // remove possible resource name mapping
                String lastPart = urlMapping;
                int slashIndex = urlMapping.lastIndexOf('/');
                if (slashIndex > 1) {
                    lastPart = urlMapping.substring(slashIndex);
                }
                if (lastPart.contains("*.") || lastPart.contains(".")) {
                    urlMapping = urlMapping.substring(0, slashIndex + 1); // include
                                                                          // the
                                                                          // forward
                                                                          // slash
                } else {
                    urlMapping = urlMapping.concat("/");
                }
            }
        }

        return ServletUtils.concatenatePaths(servletContextPath, urlMapping);
    }

}
