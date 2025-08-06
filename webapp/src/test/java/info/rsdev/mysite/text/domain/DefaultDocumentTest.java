package info.rsdev.mysite.text.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class DefaultDocumentTest {

    private static final Path document =
            Paths.get("src", "test", "resources", "info", "rsdev", "mysite", "text", "domain", "demo.adoc");

    private DefaultDocument subjectUnderTest = null;

    @Before
    public void setup() {
        DocumentGroup mockGroup = mock(DocumentGroup.class);
        DocumentCollection mockCollection = mock(DocumentCollection.class);
        when(mockGroup.getCollection()).thenReturn(mockCollection);
        when(mockCollection.getPath()).thenReturn(new File("").getAbsolutePath());
        subjectUnderTest = new DefaultDocument(mockGroup, document.toFile(), Locale.getDefault());
    }

    @Test
    public void test() {
        String html = subjectUnderTest.getContent();
        assertNotNull(html);
    }
    
    @Test
    public void returnDescriptionFromMetadataIfPresent() {
        assertThat(subjectUnderTest.getDescription()).isEqualTo("The meta description");
    }

}
