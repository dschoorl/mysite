package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.util.ServletUtils;

import java.io.File;
import java.util.Properties;

public class GalleryModuleConfig extends AbstractModuleConfig implements ConfigKeys {
    
    /**
     * Implementation of a {@link RequestHandler} without request related state to serve gallery information
     */
    private final GalleryContentServant requestHandler;
    
    public GalleryModuleConfig(Properties configProperties) {
        super(configProperties);
        String servletPath = ServletUtils.concatenatePaths(properties.getProperty(CONTEXTPATH_KEY), properties.getProperty(MOUNTPOINT_KEY));
        this.requestHandler = new GalleryContentServant(new File(getString(SITE_DATA_DIR_KEY)), getString(COLLECTION_PATH_KEY), servletPath);
    }
    
    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
    
    @Override
    public boolean hasHandlerFor(String requestPath) {
        
        /* We can handle this request, when the requestpath exactly matches this modules mountpoint and it contains
         * the template file.
         */
        return requestPath.equalsIgnoreCase(properties.getProperty(MOUNTPOINT_KEY));
    }

    public boolean showRandomFirstPage() {
        return getBoolean(RANDOM_PAGE_KEY);
    }
    
    public int getThumbnailsPerRow(String forMenuItem) {
        if (getBoolean(forMenuItem, SHOW_THUMBNAILS_KEY)) {
            return getInteger(forMenuItem, THUMBNAILS_PER_ROW_KEY);
        }
        return 0;
    }
    
    public int getImagesPerPage(String forMenuItem) {
        return getInteger(forMenuItem, IMAGES_PER_PAGE_HINT_KEY);
    }
    
}
