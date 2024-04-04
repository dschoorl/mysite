package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.ModuleConfig;

import java.util.List;

public interface CorePageModel<T extends ModuleConfig> {

    public List<MenuGroup> getMenu();

    public void setMenu(List<MenuGroup> menu);

    public T getConfig();

    public String getSelectedMenuItemName();

    public String getCopyrightNotice();

    /**
     * Get the location of the stylesheets. This could be relative to the server
     * host (location starts with a slash), relative to an internet address
     * (location starts with http:// or https://) or relative to the servlet
     * context (all other). The location always ends with a slash. If a trailing
     * slash is missing from the configuration, it is appended.
     * 
     * @return the location to css files, ending with a slash
     */
    public String getStylesheetLocation();

    public String getLanguage();
}
