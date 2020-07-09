package info.rsdev.mysite.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.rsdev.mysite.common.startup.PropertiesModule.ContentRoot;
import info.rsdev.mysite.common.startup.PropertiesModule.ContextPath;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.fixed.ConfigKeys;

/**
 * This implementation of {@link ConfigDAI} reads configuration from the filesystem at the location pointed to
 * by the MYSITE_DATA_DIR variable.
 */
public class FileConfigDAO implements ConfigDAI, ConfigKeys {
    
    private static final Logger logger = LoggerFactory.getLogger(FileConfigDAO.class);
    
    /**
     * The name of the OS Environment Variable or Java System property that contains the full path to the directory
     * on the filesystem that contains the applications' data directory.
     */
    public static final String DATA_DIR_VARIABLE_NAME = "MYSITE_DATA_DIR";
    
    private Map<String, SiteConfig> siteConfigByAlias = null;
    
    private File contentRoot = null;
    
    private final String contextPath;
    
    @Inject
    public FileConfigDAO(@ContentRoot File contentRoot, @ContextPath String contextPath) {
        this.contentRoot = contentRoot;
        this.contextPath = contextPath;
        logger.info(String.format("Using '%s' as data directory", contentRoot.getAbsolutePath()));
        this.siteConfigByAlias = getSiteConfigByAlias(this.contentRoot, new File(this.contentRoot, "aliases.properties"));
    }
    
    protected Map<String, SiteConfig> getSiteConfigByAlias(File contentRoot, File aliasesFile) {
        Map<String, SiteConfig> siteConfigByAlias = new HashMap<>();
        Properties aliases = new Properties();
        try (FileReader reader = new FileReader(aliasesFile)) {
            aliases.load(reader);
        } catch (IOException e) {
            logger.error(String.format("Problem loading site aliases from %s", aliasesFile.getAbsolutePath()), e);
        }
        
        //verify that the target directories from the mappings exist
        for (Entry<Object, Object> aliasMapping: aliases.entrySet()) {
            String alias = ((String)aliasMapping.getKey()).trim().toLowerCase();
            String subdirectory = (String)aliasMapping.getValue();
            File location = new File(contentRoot, subdirectory);
            if (!location.isDirectory()) {
                logger.warn(String.format("Wrong entry in %s: Location %s (relative to contentRoot %s) for alias %s does not exist",
                        aliasesFile, subdirectory, contentRoot, alias));
                continue;
            }
            SiteConfig siteConfig = getConfig(subdirectory, siteConfigByAlias.values());
            if (siteConfig == null) {
                siteConfig = new DefaultSiteConfig(subdirectory, readmoduleConfig(location));
            }
            if (siteConfigByAlias.containsKey(alias)) {
                String previousName = this.siteConfigByAlias.get(alias).getSiteName();
                if (!previousName.equals(subdirectory)) {
                    logger.warn(String.format("Alias %s in %s is redefined: using new SiteConfig at %s, "
                            + "dropping previous SiteConfig from %s", alias, aliasesFile, subdirectory, previousName));
                }
            }
            siteConfigByAlias.put(alias, siteConfig);
        }
        return siteConfigByAlias;
    }

    private SiteConfig getConfig(String subdirectory, Collection<SiteConfig> values) {
        if (values != null) {
            for (SiteConfig config: values) {
                if (config.getSiteName().equals(subdirectory)) {
                    return config;
                }
            }
        }
        return null;
    }

    private Map<String, ModuleConfig> readmoduleConfig(File location) {
        Map<String, ModuleConfig> configByMountpoint = new HashMap<>();
        File[] moduleProperties = location.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().equals(".properties")) {
                    return false;   //this file contains the general properties that must be applied to every module and must be processed specifically
                }
                return file.getName().endsWith(".properties") && file.isFile();
            }
        });
        
        if (moduleProperties != null) {
            for (File propertyFile: moduleProperties) {
                String mountpoint = getMountPoint(propertyFile);
                Properties properties = getGeneralProperties(location);
                try (FileReader reader = new FileReader(propertyFile)) {
                    properties.load(reader);
                    
                    //Add generated properties
                    properties.setProperty(MOUNTPOINT_KEY, mountpoint);
                    properties.setProperty(SITENAME_KEY, location.getName());
                    properties.setProperty(SITE_DATA_DIR_KEY, location.getAbsolutePath());
                    properties.setProperty(CONTEXTPATH_KEY, this.contextPath);
                    
                    //Create the ModuleConfig instance; it's type is configured in the properties
                    String configType = properties.getProperty(MODULECONFIGTYPE_KEY);
                    if (configType == null) {
                        throw new ConfigurationException(String.format("Property %s not defined.", MODULECONFIGTYPE_KEY));
                    }
                    @SuppressWarnings("unchecked")
                    Class<? extends ModuleConfig> configClass = (Class<? extends ModuleConfig>) Class.forName(configType);
                    Constructor<? extends ModuleConfig> constructor = configClass.getConstructor(Properties.class);
                    ModuleConfig config = constructor.newInstance(properties);
                    configByMountpoint.put(mountpoint, config);
                    logger.info(String.format("[%s]: mounted module %s on mountpoint '%s'", location.getName(), 
                            config, mountpoint));
                } catch (RuntimeException | IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error(String.format("Problem loading module configuration from %s", propertyFile.getAbsolutePath()), e);
                    configByMountpoint.put(mountpoint, new ErrorModuleConfig(mountpoint, e));
                }
            }
        }
        
        return configByMountpoint;
    }
    
    private Properties getGeneralProperties(File location) {
        Properties generalProperties = new Properties();
        File generalPropertiesFile = new File(location, ".properties");
        if (generalPropertiesFile.isFile()) {
            try (FileReader reader = new FileReader(generalPropertiesFile)) {
                generalProperties.load(reader);
            } catch (IOException e) {
                logger.error(String.format("Problem loading general configuration from %s", generalPropertiesFile.getAbsolutePath()), e);
            }
        }
        return generalProperties;
    }

    private String getMountPoint(File propertyFile) {
        String path = propertyFile.getName();
        path = path.substring(0, path.length() - ".properties".length());
        if (path.equals("root")) {
            return "/";
        }
        try {
            return "/".concat(URLDecoder.decode(path.replace('-', '/'), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException(String.format("Cannot derive mountpoint from %s", propertyFile.getName()), e);
        }
    }

    @Override
    public SiteConfig getConfig(String hostname) {
        if (!siteConfigByAlias.containsKey(hostname)) {
            throw new ConfigurationException(String.format("Not serving: %s", hostname));
        }
        return siteConfigByAlias.get(hostname);
    }
    
}
