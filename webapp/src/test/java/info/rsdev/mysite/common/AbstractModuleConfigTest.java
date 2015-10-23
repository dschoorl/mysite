package info.rsdev.mysite.common;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractModuleConfigTest {
    
    private AbstractModuleConfig configUnderTest;
    
    private Properties properties = null;
    
    @Before
    public void setup() {
        this.properties = new Properties();
        this.configUnderTest = new AbstractModuleConfig(properties) {
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
