package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.DefaultConfigKeys;

/**
 * This class defines the property keys that are understood by the gallery module.
 */
public interface ConfigKeys extends DefaultConfigKeys {
    
    public static final String TEMPLATE_NAME_KEY = "template";
    
    /**
     * The path relative to the site data directory that is the root directory for this image collection
     */
    public static final String COLLECTION_PATH_KEY = "collection";
    
    public static final String SHOW_THUMBNAILS_KEY = "showThumbnails";
    
    public static final String IMAGES_PER_PAGE_HINT_KEY = "imagesPerPageHint";
    
    public static final String CACHE_IMAGER_INFO_KEY = "cacheImageInfo";
    
    public static final String RANDOM_PAGE_KEY = "showRandomPageAsFirstPage";
    
    /**
     * The list of menuitem names, separated by a column (:), that may appear in the navigation menu, in the order
     * that they will appear. This is a filter and it is applied to a list of menu items; it will not cause menu items
     * to be created, it only removes ones that are not wanted and orders the remaining ones.
     */
    public static final String APPROVED_MENUITEMS = "menuitems";
}
