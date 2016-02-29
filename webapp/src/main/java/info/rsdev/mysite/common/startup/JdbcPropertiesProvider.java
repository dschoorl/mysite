package info.rsdev.mysite.common.startup;

import java.io.File;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import info.rsdev.mysite.common.startup.PropertiesModule.ContentRoot;

@Singleton
public class JdbcPropertiesProvider implements Provider<Properties> {

    private final File contentRoot;
    
    @Inject
    JdbcPropertiesProvider(@ContentRoot File contentRoot) {
        this.contentRoot = contentRoot;
    }
    
    @Override
    public Properties get() {
        Properties properties = new Properties();
        properties.put("location", contentRoot.getAbsolutePath());
        return properties;
    }

}
