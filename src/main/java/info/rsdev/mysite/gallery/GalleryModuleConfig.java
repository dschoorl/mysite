package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class GalleryModuleConfig extends AbstractModuleConfig implements ConfigKeys {
    
    /**
     * Implementation of a {@link RequestHandler} without request related state to serve gallery information
     */
    private final GalleryContentServant requestHandler;
    
    private ST cachedTemplate = null;
    
    public GalleryModuleConfig(Properties configProperties) {
        super(configProperties);
        File collectionDir = new File(getString(SITE_DATA_DIR_KEY), getString(COLLECTION_PATH_KEY));
        this.requestHandler = new GalleryContentServant(collectionDir);
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

    public synchronized ST getTemplate() {
        if (cachedTemplate != null) {
            return cachedTemplate;
        }
        
        //is the template in the external directory or within the webapp?
        String templateName = getString(TEMPLATE_NAME_KEY);
        File templateDir = new File(getString(SITE_DATA_DIR_KEY), getMountPoint());
        File templateFile = new File(templateDir, templateName.concat(".stg"));
        URL templateResource = null;
        if (templateFile.isFile()) {
            try {
                templateResource = templateFile.toURI().toURL();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            String resourceName = "/templates/".concat(templateName).concat(".stg");
            templateResource = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        STGroup group = new STGroupFile(templateResource, "UTF-8", '$', '$');
        this.cachedTemplate = group.getInstanceOf(templateName);
        return this.cachedTemplate;
    }
    
}
