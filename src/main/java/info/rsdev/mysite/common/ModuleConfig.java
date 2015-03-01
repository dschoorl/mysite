package info.rsdev.mysite.common;


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
    
}
