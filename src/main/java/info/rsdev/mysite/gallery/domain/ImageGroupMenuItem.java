package info.rsdev.mysite.gallery.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.gallery.RequestKeys;

public class ImageGroupMenuItem implements MenuItem {
    
    private static final String UTF8 = "UTF-8";
    
    private final ImageGroup imageGroup;
    
    public ImageGroupMenuItem(ImageGroup imageGroup) {
        this.imageGroup = imageGroup;
    }

    @Override
    public String getTargetUrl() {
        try {
            return String.format("%s?imagegroup=%s&%s=1", 
                    imageGroup.getCollection().getMountPoint(), 
                    URLEncoder.encode(imageGroup.getName(), UTF8),
                    RequestKeys.PAGENUMBER_PARAM);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCaption() {
        return imageGroup.getName();
    }

    @Override
    public String getImageUrl() {
        return null;
    }
    
}
