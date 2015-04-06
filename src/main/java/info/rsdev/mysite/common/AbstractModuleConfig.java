package info.rsdev.mysite.common;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractModuleConfig implements ModuleConfig {
    
    protected final Properties properties;
    
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
    public String getMountPoint() {
        String mountPoint = getString(DefaultConfigKeys.MOUNTPOINT_KEY);
        if (mountPoint.startsWith("/")) {
            mountPoint = mountPoint.substring(1);
        }
        return mountPoint;
    }
    
    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }
}
