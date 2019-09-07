package info.rsdev.mysite.gallery.domain;

import info.rsdev.mysite.common.domain.resources.PathResource;

public interface Image extends PathResource {
    
    /**
     * Get the path to the thumbnail on this server, relative to the internet hostname. It is calculated from the
     * image name and the thumbnail may or may not exist (yet). Thumbnails are always in png-format.
     */
    String getThumbnailPath();
    
}