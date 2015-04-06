package info.rsdev.mysite.common;

import java.util.Map;


/**
 * This class contains the configuration for a module of a website. 
 */
public interface ModuleConfig {
    
    /**
     * Obtain the {@link RequestHandler} for this website's module
     * @return 
     */
    public RequestHandler getRequestHandler();
    
    /**
     * Check if the paths on which the module is mounted for the website. The paths are relative to the servlet context path
     * @return an array of paths on which the module is mounted
     */
    public boolean hasHandlerFor(String requestPath);
    
    public String getString(String propertyName);
    
    public boolean getBoolean(String propertyName);
    
    public int getInteger(String propertyName);
    
    /**
     * Get the path from the URL that the module is mounted on, without leading slashes. If you need leading slashes,
     * then you should retrieve the property value directly: {@link ModuleConfig#getString(String)} with property name
     * {@link DefaultConfigKeys#MOUNTPOINT_KEY}.
     * @return the path from the URL where the module is mounted on
     */
    public String getMountPoint();
    
    public Map<Object, Object> getProperties();
}
