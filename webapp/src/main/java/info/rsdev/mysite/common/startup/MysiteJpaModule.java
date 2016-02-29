package info.rsdev.mysite.common.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class MysiteJpaModule extends AbstractModule {
    
    private static final Logger logger = LoggerFactory.getLogger(MysiteJpaModule.class);
    
    public static final String DRIVER_KEY = "jdbc.driver.name";
    public static final String JDBC_URL_KEY = "jdbc.url";
    public static final String USERNAME_KEY = "jdbc.username";
    public static final String PASSWORD_KEY = "jdbc.password";
    public static final String SHOW_SQL_KEY = "db.showsql";
    public static final String VALIDATION_QUERY_KEY = "db.pool.validation.sql";
    
    private final File contentRoot;
    
    public MysiteJpaModule(File contentRoot) {
        this.contentRoot = contentRoot;
    }
    
    @Override
    protected void configure() {
        Properties jdbcProperties = new Properties();
        File propertyFileLocation = new File(contentRoot, "datalayer.properties");
        jdbcProperties.put("propertyfile.location", propertyFileLocation.getAbsolutePath());
        try (FileInputStream stream = new FileInputStream(propertyFileLocation)) { 
            jdbcProperties.load(stream);
        } catch (IOException e) {
            logger.error(String.format("Could not load datalayer properties from %s", propertyFileLocation), e);
        }
        install(new JpaPersistModule("mysite").properties(getDatasourceProperties(jdbcProperties)));
    }
    
    private Map<String, Object> getDatasourceProperties(Properties jdbcProperties) {
        // Create the properties for the Tomcat DataSource
        PoolProperties poolConfiguration = new PoolProperties();
        poolConfiguration.setDriverClassName(jdbcProperties.getProperty(DRIVER_KEY));
        poolConfiguration.setUrl(jdbcProperties.getProperty(JDBC_URL_KEY));
        poolConfiguration.setUsername(jdbcProperties.getProperty(USERNAME_KEY));
        poolConfiguration.setPassword(jdbcProperties.getProperty(PASSWORD_KEY));
        poolConfiguration.setTimeBetweenEvictionRunsMillis(300000);
        poolConfiguration.setMinEvictableIdleTimeMillis(600000);
        poolConfiguration.setMaxActive(50);
        poolConfiguration.setMaxIdle(10);
        poolConfiguration.setMinIdle(5);
        poolConfiguration.setMaxWait(1000);
        poolConfiguration.setTestOnBorrow(true);
        poolConfiguration.setValidationQuery(jdbcProperties.getProperty(VALIDATION_QUERY_KEY));

        DataSource dataSource = new DataSource(poolConfiguration);
        try {
            dataSource.createPool();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to initialize pool", e);
        }

        Map<String, Object> hibernateConfiguration = new HashMap<String, Object>();

        // Configure the created datasource
        hibernateConfiguration.put("hibernate.connection.datasource", dataSource);

        // We don't want to use the second level cache in our clustered environment
        hibernateConfiguration.put("hibernate.cache.use_second_level_cache", "false");

        // Must be set to true according to the Hibernate documentation:
        // https://docs.jboss.org/hibernate/stable/annotations/reference/en/html/ch01.html#ann-setup-properties
        hibernateConfiguration.put("hibernate.id.new_generator_mappings", "true");

        if (Boolean.parseBoolean(jdbcProperties.getProperty(SHOW_SQL_KEY))) {
            hibernateConfiguration.put("hibernate.show_sql", "true");
            hibernateConfiguration.put("hibernate.format_sql", "true");
        }
        return hibernateConfiguration;
    }
    
}
