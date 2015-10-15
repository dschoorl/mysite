package info.rsdev.mysite.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiteServantTest {
    
    SiteServant servletUnderTest = null;
    
    @Before
    public void setup() {
        this.servletUnderTest = new SiteServant();
    }
    
    @Test
    public void contextPathStrippedFromTrainlingWildcard() {
        assertEquals("/mysite/app/", this.servletUnderTest.getContextPath("/mysite", "/app/*"));
    }
    
    @Test
    public void concatenateServletContestPathWithMappingAndEndWithSlash() {
        assertEquals("/mysite/app/", this.servletUnderTest.getContextPath("/mysite", "/app"));
    }
    
    @Test
    public void onlyUseServletContextPathWhenNoMapping() {
        assertEquals("/mysite/", this.servletUnderTest.getContextPath("/mysite", null));
    }
    
    @Test
    public void rootContextDefaultMapping() {
        assertEquals("/", this.servletUnderTest.getContextPath("/", "/*"));
    }
    
    @Test
    public void skipResourceMappingPart() {
        assertEquals("/my/app/", this.servletUnderTest.getContextPath("/", "/my/app/*.html"));
    }
    
    
    
    
}
