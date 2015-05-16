package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.ModuleConfig;

import java.util.List;

public interface CorePageModel<T extends ModuleConfig> {
    
    public List<MenuGroup> getMenu();
    
    public void setMenu(List<MenuGroup> menu);
    
    public T getConfig();
    
    public String getSelectedMenuItemName();
    
}
