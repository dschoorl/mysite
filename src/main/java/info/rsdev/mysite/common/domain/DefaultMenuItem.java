package info.rsdev.mysite.common.domain;

public class DefaultMenuItem implements MenuItem {
    
    private final String caption;
    
    private final String targetUrl;
    
    private String imageUrl = null;
    
    private boolean active = false;
    
    /**
     * Copy constructor
     * 
     * @param original the {@link DefaultMenuItem} to copy
     */
    public DefaultMenuItem(DefaultMenuItem original) {
        this(original.caption, original.targetUrl);
        this.imageUrl = original.imageUrl;
        this.active = original.active;
    }
    
    public DefaultMenuItem(String caption) {
        this(caption, null);
    }

    public DefaultMenuItem(String caption, String targetUrl) {
        this.caption = caption;
        this.targetUrl = targetUrl;
    }
    
    @Override
    public String getTargetUrl() {
        return targetUrl;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public MenuItem setActive(boolean isActive) {
        DefaultMenuItem copy = new DefaultMenuItem(this);
        copy.active = isActive;
        return copy;
    }

}
