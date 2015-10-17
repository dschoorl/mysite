package info.rsdev.mysite.common;

/**
 * Defines the interaction with the datalayer with regard to configuration
 */
public interface ConfigDAI {
    
    public SiteConfig getConfig(String hostname);
}
