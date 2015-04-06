package info.rsdev.mysite.gallery.domain;

import java.io.File;

/**
 * This class represents an image that is present on this server
 */
public class Image implements Comparable<Image> {
    
    public static final String THUMBNAIL_INDICATOR = "_thumb";
    
    private final File location;
    
    public Image(File imageFile) {
        this.location = imageFile;
    }
    
    /**
     * Get the path to the image on this server, relative to the internet hostname
     */
    public void getImagePath() {
        
    }
    
    public void getThumbnailPath() {
        
    }
    
    public void getWidth() {
        
    }
    
    public void getHeight() {
        
    }

    @Override
    public int compareTo(Image o) {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
