package info.rsdev.mysite.common;

import java.util.Map;
import java.util.Properties;

import info.rsdev.mysite.exception.ConfigurationException;

/**
 * An implementation of {@link ModuleConfig} that communicates a {@link ConfigurationException} when it is accessed, informing the
 * website user of problems loading and processing the module's configuration.
 */
public class ErrorModuleConfig implements ModuleConfig {

    private String mountPoint = null;
    
    private ConfigurationException error = null;
    
    public ErrorModuleConfig(String mountPoint, Throwable error) {
        this.mountPoint = mountPoint;
        this.error = new ConfigurationException(String.format("Module configuration at mount point %s could not be loaded", mountPoint), error);
    }

    @Override
    public RequestHandler getRequestHandler() {
        throw error;
    }
    
    @Override
    public boolean hasHandlerFor(String requestPath) {
        return requestPath.startsWith(mountPoint);
    }

    @Override
    public String getString(String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInteger(String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMountPoint() {
        String mountPoint = this.mountPoint;
        if (mountPoint.startsWith("/")) {
            mountPoint = mountPoint.substring(1);
        }
        return mountPoint;
    }

    @Override
    public Map<Object, Object> getProperties() {
        return new Properties();
    }
    
}
