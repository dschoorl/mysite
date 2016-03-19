package info.rsdev.mysite.writing.domain;

import static org.junit.Assert.*;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import info.rsdev.mysite.writing.exceptions.InconsistentDocumentException;

public class DocumentTest {
    
    private Document documentUnderTest = null;
    private final static String FIRST_VERSION = "My First Version";
    
    @Before
    public void setup() {
        this.documentUnderTest = new Document(new Locale("nl"), "My first story");
        this.documentUnderTest.newVersion(FIRST_VERSION).setAuthor("junit");
        this.documentUnderTest.publishVersion(FIRST_VERSION);
    }
    
    @Test(expected=InconsistentDocumentException.class)
    public void youCannotPublishAVersionThatIsNotPartOfTheDocument() {
        documentUnderTest.publishVersion("The great unknown");
    }
    
    @Test
    public void thereCanBeOnlyOneVersionOfADocumentPublishedAtATime() {
        assertEquals(FIRST_VERSION, this.documentUnderTest.getPublishedVersion().getVersionName());
        
        this.documentUnderTest.newVersion("Latest");
        this.documentUnderTest.publishVersion("Latest");
        assertEquals("Latest", this.documentUnderTest.getPublishedVersion().getVersionName());
        
        assertFalse(this.documentUnderTest.getVersion(FIRST_VERSION).isPublished());
        assertTrue(this.documentUnderTest.getVersion("Latest").isPublished());
    }
    
    @Test(expected=InconsistentDocumentException.class)
    public void newVersionMustHaveUniqueNameWithinDocument() {
        this.documentUnderTest.newVersion(FIRST_VERSION);
    }
    
    @Test(expected=InconsistentDocumentException.class)
    public void newCopyOfVersionMustHaveUniqueNameWithinDocument() {
        this.documentUnderTest.newVersion(FIRST_VERSION, FIRST_VERSION);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void theNameOfANewVersionMustNotBeNull() {
        this.documentUnderTest.newVersion(null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void theNameOfANewVersionMustNotBeEmpty() {
        this.documentUnderTest.newVersion("");
    }
}
