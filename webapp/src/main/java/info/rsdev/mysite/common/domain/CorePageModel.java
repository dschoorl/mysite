package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.ModuleConfig;

import java.util.List;
import java.util.Map;

public interface CorePageModel<T extends ModuleConfig> {

    public List<MenuGroup> getMenu();

    public void setMenu(List<MenuGroup> menu);

    public T getConfig();

    public String getSelectedMenuItemName();

    public String getCopyrightNotice();

    /**
     * Get the location of the stylesheets. This could be relative to the server
     * host (location starts with a slash), relative to an internet address
     * (location starts with http:// or https://) or relative to the servlet context
     * (all other). The location always ends with a slash. If a trailing slash is
     * missing from the configuration, it is appended.
     * 
     * @return the location to css files, ending with a slash
     */
    public String getStylesheetLocation();

    public String getLanguage();

    /**
     * Get a map with localized values for global keys. These are, most commonly,
     * captions for GUI items, such as buttons, links etc. that can not be localized
     * on a resource level. The application looks for translations in the site root
     * directory with files with the following format: [locale].translations, e.g.
     * en.translations for English.
     * <em>Remark: the keys cannot have dots in them</em>
     * 
     * @return the translations for the language of this pagemodel
     * @see #getLanguage()
     */
    Map<String, String> getTranslations();
}
