package info.rsdev.mysite.common;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileConfigDAOTest {
    
    private FileConfigDAO dao = null;
    File contentRoot = new File("src/test/resources/sites");
    
    @Before
    public void setup() {
        dao = new FileConfigDAO("/context");
    }
    
    @Test
    public void testService() {
        File aliasesFile = new File(contentRoot, "aliases.properties");
        Map<String, SiteConfig> contentPerAlias = dao.getSiteConfigByAlias(contentRoot, aliasesFile);
        assertNotNull(contentPerAlias);
        assertTrue(contentPerAlias.containsKey("www.nightsite.net"));
    }
    
}
