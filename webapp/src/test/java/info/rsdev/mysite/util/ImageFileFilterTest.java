package info.rsdev.mysite.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ImageFileFilterTest {
    
    @Test
    public void doNotAcceptDirectories() {
        assertFalse(ImageFileFilter.INSTANCE.accept(new File(".")));
    }
    
    @Test
    public void acceptFilesWithExtensionPng() {
        assertTrue(ImageFileFilter.INSTANCE.accept(new File("nonexistent.png")));
    }
    
    @Test
    public void acceptFilesWithExtensionJpg() {
        assertTrue(ImageFileFilter.INSTANCE.accept(new File("nonexistent.jpg")));
    }
    
    @Test
    public void acceptFilesWithExtensionJpeg() {
        assertTrue(ImageFileFilter.INSTANCE.accept(new File("nonexistent.jpeg")));
    }
    
    @Test
    public void acceptFilesWithExtensionGif() {
        assertTrue(ImageFileFilter.INSTANCE.accept(new File("nonexistent.gif")));
    }
    
    @Test
    public void doNotAcceptThumbnailFiles() {
        assertFalse(ImageFileFilter.INSTANCE.accept(new File("1_t.png")));
    }
    
    @Test
    public void ignoreDotsInDirectoryPath() {
        assertFalse(ImageFileFilter.INSTANCE.accept(new File("images.gif", "nonexistent")));
    }
    
    @Test
    public void doNotChokeOnFilenamesEndingWithDot() {
        assertFalse(ImageFileFilter.INSTANCE.accept(new File("nonexistent.")));
    }
    
    @Test
    public void beCaseInsensitive() {
        assertTrue(ImageFileFilter.INSTANCE.accept(new File("noneXistent.PnG")));
    }
}
