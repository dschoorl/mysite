package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.DefaultConfigKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultMenuGroup implements MenuGroup, Comparable<MenuGroup> {
    
    public static final DefaultMenuGroup EMPTY_GROUP = new DefaultMenuGroup(Collections.<MenuItem>emptyList());
    
    private final int sortingPriority;;
    
    private final List<MenuItem> menuItems;
    
    private final String caption;
    
    public DefaultMenuGroup(List<MenuItem> menuItems) {
        this(menuItems, null, DefaultConfigKeys.DEFAULT_MENU_ORDER_PRIORITY_VALUE);
    }
    
    public DefaultMenuGroup(List<MenuItem> menuItems, String caption, int sortingPriority) {
        this.sortingPriority = sortingPriority;
        this.caption = caption;
        if (menuItems == null) {
            this.menuItems = Collections.emptyList();
        } else {
            this.menuItems = new ArrayList<>(menuItems);
        }
    }
    
    @Override
    public List<MenuItem> getMenuItems() {
        return Collections.unmodifiableList(this.menuItems);
    }
    
    @Override
    public boolean markActive(String itemCaption) {
        for (int i=0; i< this.menuItems.size(); i++) {
            if (this.menuItems.get(i).getCaption().equals(itemCaption)) {
                MenuItem activeCopy = this.menuItems.get(i).setActive(true);
                this.menuItems.set(i, activeCopy);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getMenuTitle() {
        return caption;
    }
    
    @Override
    public boolean isEmpty() {
        return this.menuItems.isEmpty();
    }
    
    @Override
    public boolean isActive() {
        for (MenuItem item: this.menuItems) {
            if (item.isActive()) { return true; }
        }
        return false;
    }
    
    @Override
    public int getSortingPriority() {
        return this.sortingPriority;
    }

    @Override
    public int compareTo(MenuGroup o) {
        if (o == null) {
            return -1;
        }
        if (o.getSortingPriority() == this.sortingPriority) {
            return this.getMenuTitle().compareTo(o.getMenuTitle());
        }
        return this.getSortingPriority()<o.getSortingPriority()?1:-1;
    }

}
