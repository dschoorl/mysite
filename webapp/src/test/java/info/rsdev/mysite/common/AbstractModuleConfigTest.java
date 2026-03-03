package info.rsdev.mysite.common;

import static org.junit.Assert.assertFalse;

import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class AbstractModuleConfigTest {
    
    private AbstractModuleConfig configUnderTest;
    
    private Properties properties = null;
    
    @Before
    public void setup() {
        this.properties = new Properties();
        this.configUnderTest = new AbstractModuleConfig(properties, Set.of()) {
            @Override
            public boolean hasHandlerFor(String requestPath) {
                return false;
            }
            @Override
            public RequestHandler getRequestHandler() {
                return null;
            }
        };
    }
    
    @Test
    public void returnFalseForUndefinedBooleanProperty() {
        assertFalse(configUnderTest.getBoolean("undefined.property"));
    }
    
    @Test(expected = NullPointerException.class)
    public void exceptionWhenPropertynameIsMissing() {
        configUnderTest.getBoolean(null);
    }
    
}
