package info.rsdev.mysite.stats;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.RequestHandler;

import java.util.Properties;

public class StatsModuleConfig extends AbstractModuleConfig implements DefaultConfigKeys {
    
    /**
     * Implementation of a {@link RequestHandler} without request related state to serve web statistics
     */
    private final StatsContentServant requestHandler = new StatsContentServant();
    
    public StatsModuleConfig(Properties configProperties) {
        super(configProperties);
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

}
