package info.rsdev.mysite.common.startup;

import org.junit.Before;
import org.junit.Test;

import info.rsdev.mysite.common.startup.GuiceServletConfig;
import static org.junit.Assert.*;


public class GuiceServletConfigTest {
    
    GuiceServletConfig objectUnderTest = null;
    
    @Before
    public void setup() {
        this.objectUnderTest = new GuiceServletConfig();
    }
    
    @Test
    public void contextPathStrippedFromTrainlingWildcard() {
        assertEquals("/mysite/app/", this.objectUnderTest.getContextPath("/mysite", "/app/*"));
    }
    
    @Test
    public void concatenateServletContestPathWithMappingAndEndWithSlash() {
        assertEquals("/mysite/app/", this.objectUnderTest.getContextPath("/mysite", "/app"));
    }
    
    @Test
    public void onlyUseServletContextPathWhenNoMapping() {
        assertEquals("/mysite/", this.objectUnderTest.getContextPath("/mysite", null));
    }
    
    @Test
    public void rootContextDefaultMapping() {
        assertEquals("/", this.objectUnderTest.getContextPath("/", "/*"));
    }
    
    @Test
    public void skipResourceMappingPart() {
        assertEquals("/my/app/", this.objectUnderTest.getContextPath("/", "/my/app/*.html"));
    }
    
}
