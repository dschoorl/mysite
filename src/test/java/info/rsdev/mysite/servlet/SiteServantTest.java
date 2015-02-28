package info.rsdev.mysite.servlet;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiteServantTest {
    
    private SiteServant servlet = null;
    File contentRoot = new File("src/test/resources/sites");
    
    @Before
    public void setup() {
        servlet = new SiteServant();
    }
    
    @Test
    public void testService() {
        File aliasesFile = new File(contentRoot, "aliases.properties");
        Map<String, File> contentPerAlias = servlet.getAliasesContextRoots(contentRoot, aliasesFile);
        assertNotNull(contentPerAlias);
        assertTrue(contentPerAlias.containsKey("www.nightsite.net"));
    }
    
}
