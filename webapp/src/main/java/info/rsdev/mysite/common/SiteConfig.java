package info.rsdev.mysite.common;

import java.util.List;
import java.util.Locale;

import info.rsdev.mysite.common.domain.MenuGroup;

/**
 * This class contains the global configuration for the website
 */
public interface SiteConfig {

    public ModuleConfig getModuleConfig(String moduleContextPath);

    /**
     * The unified name of the site. It resolves to a directory in the content root.
     * Multiple aliases can use the same site name.
     * 
     * @return the unified name of the site
     */
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
