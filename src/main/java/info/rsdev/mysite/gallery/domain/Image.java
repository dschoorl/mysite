package info.rsdev.mysite.gallery.domain;

import java.io.File;

/**
 * This class represents an image that is present on this server
 */
public class Image implements Comparable<Image> {
    
    public static final String THUMBNAIL_INDICATOR = "_thumb";
    
    private final ImageGroup imageGroup;
    
    private final String imagePath;
    
    public Image(ImageGroup group, File image) {
        this.imageGroup = group;
        
        //calculate the imagePath relative to the collectionPath
        String collectionPath = this.imageGroup.getCollection().getPath();
        String imagePath = image.getAbsolutePath();
        int index = imagePath.indexOf(collectionPath);
        this.imagePath = imagePath.substring(index);
    }
    
    /**
     * Get the path to the image on this server, relative to the internet hostname
     */
    public String getImagePath() {
        return this.imagePath;
    }
    
    public File getThumbnailPath() {
        return null; //not yet supported
    }
    
    public int getWidth() {
        return 0;
    }
    
    public int getHeight() {
        return 0;
    }

    @Override
    public int compareTo(Image o) {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
