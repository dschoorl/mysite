package info.rsdev.mysite.gallery.domain;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImageCollectionTest {
    
    private ImageCollection collection = null;
    
    @Before
    public void setup() {
        this.collection = new ImageCollection(new File("src/test/resources/sites/site1.com/images"));
    }
    
    @Test
    public void rootDirIsAlsoInventoried() {
        List<Image> groupImages = collection.getImages("images");
        assertNotNull(groupImages);
        assertEquals(1, groupImages.size());
    }
    
    @Test
    public void differentDirsWithSameNameAreMergesInOneGroup() {
        List<Image> groupImages = collection.getImages("fun");
        assertNotNull(groupImages);
        assertEquals(3, groupImages.size());
    }
    
    @Test
    public void dirsWithNoImagesAreInventoriedAsEmptyCollections() {
        List<Image> groupImages = collection.getImages("serious");
        assertNotNull(groupImages);
        assertEquals(0, groupImages.size());
    }
    
}