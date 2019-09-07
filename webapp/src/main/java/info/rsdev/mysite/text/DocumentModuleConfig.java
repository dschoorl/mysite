package info.rsdev.mysite.text;

import java.io.File;
import java.util.Properties;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.util.ServletUtils;

public class DocumentModuleConfig extends AbstractModuleConfig implements ConfigKeys {

    /**
     * Implementation of a {@link RequestHandler} without request related state
     * to serve gallery information
     */
    private final DocumentContentServant requestHandler;

    public DocumentModuleConfig(Properties configProperties) {
        super(configProperties);
        String servletPath =
                ServletUtils.concatenatePaths(properties.getProperty(CONTEXTPATH_KEY), properties.getProperty(MOUNTPOINT_KEY));
        this.requestHandler = new DocumentContentServant(new File(getString(SITE_DATA_DIR_KEY)), getString(COLLECTION_PATH_KEY), servletPath);
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public boolean hasHandlerFor(String requestPath) {

        /*
         * We can handle this request, when the requestpath starts with this
         * modules mountpoint and it contains the content file.
         */
        return requestPath.startsWith(properties.getProperty(MOUNTPOINT_KEY));
    }

}
