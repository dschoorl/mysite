package info.rsdev.mysite.common.startup;

import java.io.File;
import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.servlet.GuiceServletContextListener;

import info.rsdev.mysite.common.startup.PropertiesModule.Jdbc;

public class GuiceServletConfig extends GuiceServletContextListener {
    
    /**
     * The name of the OS Environment Variable or Java System property that contains the full path to the directory
     * on the filesystem that contains the applications' data directory.
     */
    public static final String DATA_DIR_VARIABLE_NAME = "MYSITE_DATA_DIR";
    
    @Override
    protected Injector getInjector() {
        File contentRoot = getContentRoot();
        return Guice.createInjector(
                                    new PropertiesModule(contentRoot),
                                    new MysiteServletModule()/*, 
                                    new MysiteJpaModule(contentRoot)*/);
        
    }
    
    private File getContentRoot() {
        File contentRoot  = null;
        String dataDir = System.getenv(DATA_DIR_VARIABLE_NAME);
        if (dataDir == null) {
            dataDir = System.getProperty(dataDir);
            if (dataDir == null) {
                throw new IllegalStateException(String.format("%s is not set. Check setup documentation.", DATA_DIR_VARIABLE_NAME));
            }
        }
        contentRoot = new File(dataDir);
        if (!contentRoot.isDirectory()) {
            throw new IllegalStateException(String.format("Directory '%s' pointed to by %s does not exist. Please create it manually.", 
                    dataDir, DATA_DIR_VARIABLE_NAME));
        }
        return contentRoot;

    }

}