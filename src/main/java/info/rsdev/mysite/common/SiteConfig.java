package info.rsdev.mysite.common;


/**
 * This class contains the global configuration for the website
 */
public interface SiteConfig {
    
    public ModuleConfig getModuleConfig(String moduleContextPath);

    public String getSiteName();
}
