package info.rsdev.mysite.common;

import java.util.List;
import java.util.Locale;

import info.rsdev.mysite.common.domain.MenuGroup;

/**
 * This class contains the global configuration for the website
 */
public interface SiteConfig {

    public ModuleConfig getModuleConfig(String moduleContextPath);

    public String getSiteName();

    /**
     * Collect all menu items that are applicable for the given {@link Locale}
     * 
     * @param applicableLocale the Locale that applies to the request
     * @return a collection of menu items from modules with the same locale or an
     *         empty list if there are none
     */
    public List<MenuGroup> getMenu(Locale applicableLocale);

}
