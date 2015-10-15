package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.MenuGroup;

import java.util.List;


/**
 * This class contains the global configuration for the website
 */
public interface SiteConfig {
    
    public ModuleConfig getModuleConfig(String moduleContextPath);

    public String getSiteName();
    
    public List<MenuGroup> getMenu();
    
}
