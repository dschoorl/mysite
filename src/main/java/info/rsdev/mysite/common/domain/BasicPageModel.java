package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.ModuleConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        for (MenuGroup menuGroup: menu) {
            if (menuGroup.markActive(selectedMenuitemName)) {
                break;
            }
        }
    }

    @Override
    public String getSelectedMenuItemName() {
        return this.selectedMenuItemName;
    }

}
