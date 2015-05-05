package info.rsdev.mysite.common.domain;

public class DefaultMenuItem implements MenuItem {
    
    private final String caption;
    
    private final String targetUrl;
    
    private String imageUrl = null;
    
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
    
}
