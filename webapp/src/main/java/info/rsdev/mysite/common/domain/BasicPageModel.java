package info.rsdev.mysite.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.LocalizationContext;
import info.rsdev.mysite.common.ModuleConfig;

public class BasicPageModel<T extends ModuleConfig> implements CorePageModel<T> {

    private final T config;

    private List<MenuGroup> menu = Collections.emptyList();

    private final String selectedMenuItemName;

    public BasicPageModel(T config, String selectedMenuItemName) {
        this.selectedMenuItemName = selectedMenuItemName;
        this.config = config;
    }

    @Override
    public T getConfig() {
        return this.config;
    }

    @Override
    public List<MenuGroup> getMenu() {
        return this.menu;
    }

    @Override
    public void setMenu(List<MenuGroup> menu) {
        this.menu = new ArrayList<>(menu);
        markActiveItem(this.menu, getSelectedMenuItemName());
    }

    private void markActiveItem(List<MenuGroup> menu, String selectedMenuitemName) {
        for (MenuGroup menuGroup : menu) {
            if (menuGroup.markActive(selectedMenuitemName)) {
                break;
            }
        }
    }

    @Override
    public String getSelectedMenuItemName() {
        return this.selectedMenuItemName;
    }

    @Override
    public String getCopyrightNotice() {
        return config.getString(DefaultConfigKeys.COPYRIGHT_NOTICE_KEY);
    }

    @Override
    public String getStylesheetLocation() {
        String location = config.getString(DefaultConfigKeys.CSS_LOCATION_KEY);
        String returnValue = null;
        if (location != null) {
            if (location.startsWith("/")) {
                returnValue = location; // relative to server host
            } else if (location.startsWith("http://") || location.startsWith("https://")) {
                returnValue = location; // full url
            } else {
                returnValue = config.getContextPath().concat(location); // relative to context path
            }
            if (!returnValue.endsWith("/")) {
                returnValue = returnValue.concat("/");
            }
        }
        return returnValue;
    }

    @Override
    public String getLanguage() {
        return config.getLocale().getLanguage();
    }

    public Map<String, String> getTranslations() {
        return LocalizationContext.getLocalizations();
    }

}
