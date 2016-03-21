package info.rsdev.mysite.writing;

import java.util.Properties;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.writing.dao.StaticReadingDao;

public class WritingModuleConfig extends AbstractModuleConfig implements ConfigKeys {
    
    /**
     * Implementation of a {@link RequestHandler} without request related state to serve gallery information
     */
    private final WritingContentServant requestHandler;
    
    public WritingModuleConfig(Properties configProperties) {
        super(configProperties);
        this.requestHandler = new WritingContentServant(new StaticReadingDao());
    }
    
    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
    
    @Override
    public boolean hasHandlerFor(String requestPath) {
        
        /* We can handle this request, when the requestpath starts with this modules mountpoint and it contains
         * the content file.
         */
        return requestPath.toLowerCase().startsWith(properties.getProperty(MOUNTPOINT_KEY).toLowerCase());
    }

}
