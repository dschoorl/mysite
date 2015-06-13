package info.rsdev.mysite.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public abstract class AbstractModuleConfig implements ModuleConfig, DefaultConfigKeys {
    
    protected final Properties properties;
    
    private STGroup cachedTemplateGroup = null;
    
    public AbstractModuleConfig(Properties configProperties) {
        this.properties = configProperties;
    }
    
    @Override
    public String getString(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
    
    @Override
    public boolean getBoolean(String propertyName) {
        return Boolean.parseBoolean(this.properties.getProperty(propertyName));
    }
    
    @Override
    public int getInteger(String propertyName) {
        String value = this.properties.getProperty(propertyName);
        if (value == null) { return 0; }
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
    
    public synchronized ST getTemplate() {
        String templateName = getString(TEMPLATE_NAME_KEY);
        if (cachedTemplateGroup == null) {
            //is the template in the external directory or within the webapp?
            File templateDir = new File(getString(SITE_DATA_DIR_KEY), getMountPoint());
            File templateFile = new File(templateDir, templateName.concat(".stg"));
            URL templateResource = null;
            if (templateFile.isFile()) {
                try {
                    templateResource = templateFile.toURI().toURL();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String resourceName = "/templates/".concat(templateName).concat(".stg");
                templateResource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
            }
            this.cachedTemplateGroup = new STGroupFile(templateResource, "UTF-8", '$', '$');
        }
        return cachedTemplateGroup.getInstanceOf(templateName);
    }
    
    @Override
    public Logger getAccessLogger() {
        //TODO: programmatically create logger per website that logs into website's data directory
        return LoggerFactory.getLogger("AccessLog");
    }
    
    @Override
    public File getAccessLogFile() {
        String logDir = System.getProperty("MYSITE_LOG_DIR");
        if ((logDir == null) || logDir.isEmpty()) {
            throw new IllegalStateException("The value of Java system property 'MYSITE_LOG_DIR' is not set");
        }
//        File logDir = new File(getString(SITE_DATA_DIR_KEY), "logs");
        return new File(logDir, "mysite-accesslog.log");
    }
    
    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }
    
}
