package info.rsdev.mysite.common;

import info.rsdev.mysite.exception.ConfigurationException;

import java.util.Properties;

/**
 * An implementation of {@link ModuleConfig} that communicates a {@link ConfigurationException} when it is accessed, informing the
 * website user of problems loading and processing the module's configuration.
 */
public class ErrorModuleConfig extends AbstractModuleConfig {

    private ConfigurationException error = null;
    
    public ErrorModuleConfig(String mountPoint, Throwable error) {
        super(new Properties());
        this.properties.put(MOUNTPOINT_KEY, mountPoint);
        this.error = new ConfigurationException(String.format("Module configuration at mount point %s could not be loaded", mountPoint), error);
    }

    @Override
    public RequestHandler getRequestHandler() {
        throw error;
    }
    
    @Override
    public boolean hasHandlerFor(String requestPath) {
        return requestPath.startsWith(getMountPoint());
    }
    
}