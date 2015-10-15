package info.rsdev.mysite.common.domain;

public interface MenuItem {
    
    public String getTargetUrl();
    
    /**
     * Get the text for the menu item. Optional operation.
     * @return
     */
    public String getCaption();
    
    /**
     * Get the url, relative to the website, that points to an image that must be used to visualize
     * the menu item. Optional operation. 
     * 
     * @return relative url for a menu item image or null if this menu item has none
     */
    public String getImageUrl();
    
    public boolean isActive();

    /**
     * Create a copy of this menu item and set itś active state to the provided value. When a {@link MenuItem} is
     * marked active, it indicates that the page content stems from this menu item.
     * @param isActive the active state of the copy
     * @return a copy of the {@link MenuItem} with the desired active state
     */
    public MenuItem setActive(boolean isActive);
    
}
