package info.rsdev.mysite.gallery.domain;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImageCollectionTest {

    private ImageCollection collection = null;

    @Before
    public void setup() {
        this.collection = new ImageCollection(new File("src/test/resources/sites/site1.com"), "images", "", Locale.US);
    }

    @Test
    public void rootDirIsNotInventoriedByDefault() {
        List<DefaultImage> groupImages = collection.getAll("images");
        assertNotNull(groupImages);
        assertEquals(0, groupImages.size());
    }

    @Test
    public void differentDirsWithSameNameAreNotMergedInOneGroup() {
        List<DefaultImage> groupImages = collection.getAll("fun");
        assertNotNull(groupImages);
        assertEquals(3, groupImages.size());
    }

    @Test
    public void dirsWithNoImagesAreInventoriedAsEmptyCollections() {
        List<DefaultImage> groupImages = collection.getAll("serious");
        assertNotNull(groupImages);
        assertEquals(0, groupImages.size());
    }

}
