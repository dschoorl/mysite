package info.rsdev.mysite.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public abstract class AbstractModuleConfig implements ModuleConfig, DefaultConfigKeys {

    protected final Properties properties;

    private ConcurrentHashMap<String, STGroup> cachedTemplateGroupByTemplateName = new ConcurrentHashMap<>();

    public AbstractModuleConfig(Properties configProperties) {
        this.properties = configProperties;
    }

    @Override
    public String getString(String propertyName) {
        return this.properties.getProperty(propertyName);
    }

    protected String getString(String forMenuItem, String propertyName) {
        String propertyValue = null;
        if (forMenuItem != null) {
            propertyValue = getString(forMenuItem + "." + propertyName); // is
                                                                         // the
                                                                         // property
                                                                         // defined
                                                                         // specific
                                                                         // for
                                                                         // the
                                                                         // menu
                                                                         // item?
        }
        if (propertyValue == null) {
            propertyValue = getString(propertyName); // use default instead
        }
        return propertyValue;
    }

    @Override
    public boolean getBoolean(String propertyName) {
        return Boolean.parseBoolean(this.properties.getProperty(propertyName));
    }

    protected boolean getBoolean(String forMenuItem, String propertyName) {
        return Boolean.parseBoolean(getString(forMenuItem, propertyName));
    }

    @Override
    public int getInteger(String propertyName) {
        return getInteger(null, propertyName);
    }

    protected int getInteger(String forMenuItem, String propertyName) {
        String value = getString(forMenuItem, propertyName);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    @Override
    public String getContextPath() {
        String contextPath = getString(CONTEXTPATH_KEY);
        if (contextPath == null) {
            contextPath = "/";
        } else if (!contextPath.endsWith("/")) {
            contextPath = contextPath.concat("/");
        }
        return contextPath;
    }

    @Override
    public String getMountPoint() {
        String mountPoint = getString(MOUNTPOINT_KEY);
        if (mountPoint.startsWith("/")) {
            mountPoint = mountPoint.substring(1);
        }
        return mountPoint;
    }

    @Override
    public List<String> getVisibleMenuItems() {
        String itemString = properties.getProperty(APPROVED_MENUITEMS_KEY);
        if (itemString == null) {
            return null;
        }
        return Arrays.asList(itemString.split(":"));
    }

    @Override
    public String getMenugroupTitle() {
        return getString(MENUGROUP_TITLE_KEY);
    }

    @Override
    public int getMenuSortingPriority() {
        String sortingPriority = getString(MENU_ORDER_PRIORITY_KEY);
        if (sortingPriority != null) {
            return Integer.parseInt(sortingPriority);
        }
        return DEFAULT_MENU_ORDER_PRIORITY_VALUE;
    }

    public synchronized ST getTemplate(String forMenuItem) {
        String templateName = getString(forMenuItem, TEMPLATE_NAME_KEY);
        if (!cachedTemplateGroupByTemplateName.contains(templateName)) {
            URL templateResource = getSiteSpecificTemplateLocation(templateName);
            if (templateResource == null) {
                // no user supplied template provided, fallback on templates
                // supplied by the application
                String resourceName = "templates/".concat(templateName).concat(".stg");
                templateResource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
            }
            this.cachedTemplateGroupByTemplateName.putIfAbsent(templateName, new STGroupFile(templateResource, "UTF-8", '$', '$'));
        }
        return cachedTemplateGroupByTemplateName.get(templateName).getInstanceOf(templateName);
    }

    /**
     * A site specific template can be located either in a designated template
     * folder in the site's data directory, or, as a fallback mechanism, in the
     * folder where the collection is located. If these two locations do not
     * hold the template file, then there is no site specific template.
     * 
     * @param templateName the name of the template we are looking for
     * @return the URL to the site specific template or null if there is none
     */
    private URL getSiteSpecificTemplateLocation(String templateName) {
        File templateDir = null;
        String customTemplatePath = getString(CUSTOM_TEMPLATE_FOLDER_KEY);
        if (customTemplatePath != null) {
            templateDir = new File(getString(SITE_DATA_DIR_KEY), customTemplatePath);
        }
        if ((templateDir == null) || !templateDir.isDirectory()) {
            String collectionPath = getString(COLLECTION_PATH_KEY);
            if (collectionPath != null) {
                templateDir = new File(getString(SITE_DATA_DIR_KEY), collectionPath);
            }
        }
        if ((templateDir != null) && templateDir.isDirectory()) {
            File templateFile = new File(templateDir, templateName.concat(".stg"));
            if (templateFile.isFile()) {
                try {
                    return templateFile.toURI().toURL();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    public Logger getAccessLogger() {
        return LoggerFactory.getLogger("AccessLog");
    }

    @Override
    public File getAccessLogFile() {
        String logDir = System.getProperty("MYSITE_LOG_DIR");
        if ((logDir == null) || logDir.isEmpty()) {
            logDir = System.getProperty("MYSITE_LOG_DIR");
            if ((logDir == null) || logDir.isEmpty()) {
                throw new IllegalStateException("The value of Java system property 'MYSITE_LOG_DIR' is not set");
            }
        }
        return new File(logDir, "mysite-accesslog.log");
    }

    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    public boolean isDisabled() {
        return getBoolean(DISABLED_KEY);
    }

}
