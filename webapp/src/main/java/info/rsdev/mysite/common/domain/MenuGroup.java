package info.rsdev.mysite.common.domain;

import java.util.List;

public interface MenuGroup {
    
    public List<MenuItem> getMenuItems();
    
    public String getMenuTitle();
    
    public boolean isEmpty();
    
    public boolean isActive();
    
    /**
     * Get the priority of this menu group in the menu. How lower the number, how higher it will
     * be displayed in the navigational menu.
     * 
     * @return
     */
    public int getSortingPriority();

    public boolean markActive(String itemCaption);
    
}
