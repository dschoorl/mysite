package info.rsdev.mysite.fixed;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.exception.ConfigurationException;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class FixedContentModuleConfig implements ModuleConfig, ConfigKeys {
    
    /**
     * Implementation of a {@link RequestHandler} without request related state to serve static content
     */
    private final FixedContentServant requestHandler;
    
    private Path siteRoot = null;
    
    private Properties configProperties = null;
    
    public FixedContentModuleConfig(Properties configProperties) {
        //TODO: validate presence and correctness of important properties
        this.configProperties = configProperties;
        this.requestHandler = new FixedContentServant(this);
    }
    
    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler;  //implementation is stateless
    }
    
    @Override
    public boolean hasHandlerFor(String path) {
        return path.startsWith(configProperties.getProperty(MOUNTPOINT_KEY));
    }
    
    /**
     * Obtain the path the to filesystem where the site specific static content is stored. 
     * @return
     */
    public Path getSiteRoot() {
        if (this.siteRoot == null) {
            String siteDataDir = configProperties.getProperty(SITE_DATA_DIR_KEY);
            if (siteDataDir == null) {
                throw new ConfigurationException(String.format("Property not defined: %s", SITE_DATA_DIR_KEY));
            }
            Path siteRoot = FileSystems.getDefault().getPath(siteDataDir);
            if (!siteRoot.toFile().isDirectory()) {
                throw new ConfigurationException(String.format("%s points to a not-existing directory: '%s'.", SITE_DATA_DIR_KEY ,siteRoot));
            }
            this.siteRoot = siteRoot;
        }
        return this.siteRoot;
    }
    
}
