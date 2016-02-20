package info.rsdev.mysite.gallery.domain;

public interface Image {
    
    /**
     * Get the path to the image on this server, relative to the internet hostname
     */
    String getImagePath();
    
    /**
     * Get the path to the thumbnail on this server, relative to the internet hostname. It is calculated from the
     * image name and the thumbnail may or may not exist (yet). Thumbnails are always in png-format.
     */
    String getThumbnailPath();
    
}