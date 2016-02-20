package info.rsdev.mysite.util;

import info.rsdev.mysite.gallery.domain.DefaultImage;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class ImageFileFilter implements FileFilter {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg", ".gif");
    
    public static final ImageFileFilter INSTANCE = new ImageFileFilter();

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) { return false; }
        String filename = pathname.getName();
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0) { return false; }
        
        //do not accept thumbnails! -- their name, the part before the (mandatory) extension, ends with _t
        if ((lastDotIndex > DefaultImage.THUMBNAIL_INDICATOR.length()) && filename.substring(0, lastDotIndex).endsWith(DefaultImage.THUMBNAIL_INDICATOR)) {
            return false;
        }
        
        return SUPPORTED_EXTENSIONS.contains(filename.substring(lastDotIndex).toLowerCase());
    }
    
}
