package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.DefaultConfigKeys;

/**
 * This class defines the property keys that are understood by the gallery module.
 */
public interface ConfigKeys extends DefaultConfigKeys {
    
    public static final String SHOW_THUMBNAILS_KEY = "showThumbnails";
    
    public static final String IMAGES_PER_PAGE_HINT_KEY = "imagesPerPageHint";
    
    public static final String CACHE_IMAGER_INFO_KEY = "cacheImageInfo";
    
    public static final String RANDOM_PAGE_KEY = "showRandomPageAsFirstPage";
    
    public static final String THUMBNAILS_PER_ROW_KEY = "thumbnailsPerRow";
    
    
    
}
