package info.rsdev.mysite.fixed;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixedContentServantTest {
    
    @Test
    public void getMimeTypeHtmlFile() {
        FixedContentServant servant = new FixedContentServant();
        Path resource = Paths.get("src", "test", "resources", "sites", "site1.com", "root.html");
        assertEquals("text/html", servant.getMimeType(resource));
    }
    
    @Test
    public void getMimeTypeJpegFile() {
        FixedContentServant servant = new FixedContentServant();
        Path resource = Paths.get("src", "test", "resources", "sites", "site1.com", "images", "test-picture.jpg");
        assertEquals("image/jpeg", servant.getMimeType(resource));
    }
    
    @Test
    public void getMimeTypeCssFile() {
        FixedContentServant servant = new FixedContentServant();
        Path resource = Paths.get("src", "test", "resources", "sites", "site1.com", "branding.css");
        assertEquals("text/css", servant.getMimeType(resource));
    }
    
}
