package info.rsdev.mysite.gallery.domain;

import java.io.File;

/**
 * This class represents an image that is present on this server
 */
public class DefaultImage implements Comparable<DefaultImage>, Image {
    
    public static final String THUMBNAIL_INDICATOR = "_t";
    public static final String THUMBNAIL_EXTENSION = ".png";
    
    private final ImageGroup imageGroup;
    
    private final String imagePath;
    
    private final String imageName;
    
    public DefaultImage(ImageGroup group, File image) {
        this.imageGroup = group;
        this.imageName = image.getName();
        
        //calculate the imagePath relative to the collectionPath
        String collectionPath = this.imageGroup.getCollection().getPath();
        String imagePath = image.getAbsolutePath();
        int index = imagePath.indexOf(collectionPath);
        this.imagePath = imagePath.substring(index);
    }
    
    /**
     * Get the path to the image on this server, relative to the internet hostname
     */
    @Override
    public String getImagePath() {
        return this.imagePath;
    }
    
    /**
     * Get the path to the thumbnail on this server, relative to the internet hostname. It is calculated from the
     * image name and the thumbnail may or may not exist (yet). Thumbnails are always in png-format.
     */
    @Override
    public String getThumbnailPath() {
        int dotIndex = this.imagePath.lastIndexOf('.');
        return this.imagePath.substring(0,  dotIndex).concat(THUMBNAIL_INDICATOR + THUMBNAIL_EXTENSION);
    }
    
    public int getWidth() {
        return 0;
    }
    
    public int getHeight() {
        return 0;
    }

    @Override
    public int compareTo(DefaultImage o) {
        if (o == null) {
            return -1;
        }
        return this.imageName.compareTo(o.imageName);
    }
    
    public static boolean isThumbnail(String fileName) {
        return fileName.endsWith(THUMBNAIL_INDICATOR + THUMBNAIL_EXTENSION);
    }
    
}
