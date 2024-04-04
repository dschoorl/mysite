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
        this.requestHandler = new DocumentContentServant(new File(getString(SITE_DATA_DIR_KEY)), getString(COLLECTION_PATH_KEY), servletPath, getLocale());
        
        String recentDocumentsGroupName = getString(RECENT_DOCUMENTS_GROUP_NAME_KEY);
        if (recentDocumentsGroupName != null) {
            this.requestHandler.addRecentDocumentsGroup(recentDocumentsGroupName, getInteger(RECENT_DOCUMENTS_DAYS_KEY));
        }
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public boolean hasHandlerFor(String requestPath) {

        /*
         * We can handle this request, when the requestpath equals the modules mountpoint,
         * or starts with it (we have to compare url path elements).
         */
        String[] requestParts = requestPath.split("/");
        String[] mountParts = properties.getProperty(MOUNTPOINT_KEY).split("/");
        if (mountParts.length > requestParts.length) {
            return false;
        }
        for (int i=0; i<mountParts.length; i++) {
            if (!mountParts[i].equals(requestParts[i])) {
                return false;
            }
        }
        return true;
    }

}
