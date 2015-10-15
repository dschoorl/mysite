package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.DefaultConfigKeys;

/**
 * This class defines the property keys that are understood by the gallery module.
 */
public interface ConfigKeys extends DefaultConfigKeys {
    
    /**
     * The path relative to the site data directory that is the root directory for this image collection
     */
    public static final String COLLECTION_PATH_KEY = "collection";
    
    public static final String SHOW_THUMBNAILS_KEY = "showThumbnails";
    
    public static final String IMAGES_PER_PAGE_HINT_KEY = "imagesPerPageHint";
    
    public static final String CACHE_IMAGER_INFO_KEY = "cacheImageInfo";
    
    public static final String RANDOM_PAGE_KEY = "showRandomPageAsFirstPage";
    
}
