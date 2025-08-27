package info.rsdev.mysite.stats;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.RequestHandler;

import java.util.Properties;

public class StatsModuleConfig extends AbstractModuleConfig implements DefaultConfigKeys {

    /**
     * Implementation of a {@link RequestHandler} without request related state to
     * serve web statistics
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

        /*
         * We can handle this request, when the request path exactly matches this
         * modules mount point and it optionally may be followed by a reporting period (e.g. /2025-06)
         */
        boolean canHandle = requestPath.equalsIgnoreCase(properties.getProperty(MOUNTPOINT_KEY));
        canHandle |= requestPath.startsWith(properties.getProperty(MOUNTPOINT_KEY).concat("/"))
                && requestPath.length() == properties.getProperty(MOUNTPOINT_KEY).length() + 8;
        return canHandle;
    }

}
