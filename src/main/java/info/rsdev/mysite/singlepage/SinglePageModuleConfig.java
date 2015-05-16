package info.rsdev.mysite.singlepage;

import java.io.File;
import java.util.Properties;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;

public class SinglePageModuleConfig extends AbstractModuleConfig implements ConfigKeys {
    
    private final SinglePageContentServant requestHandler;
    
    public SinglePageModuleConfig(Properties configProperties) {
        super(configProperties);
        String servletPath = properties.getProperty(CONTEXTPATH_KEY).concat(properties.getProperty(MOUNTPOINT_KEY));
        this.requestHandler = new SinglePageContentServant(new File(getString(SITE_DATA_DIR_KEY)), getString(PAGECOLLECTION_PATH_KEY), servletPath);
    }

    @Override
    public RequestHandler getRequestHandler() {
        return this.requestHandler;
    }
    
    @Override
    public boolean hasHandlerFor(String requestPath) {
        
        /* We can handle this request, when the requestpath starts with this modules mountpoint and it contains
         * the content file.
         */
        return requestPath.toLowerCase().startsWith(properties.getProperty(MOUNTPOINT_KEY).toLowerCase());
    }
    
}
