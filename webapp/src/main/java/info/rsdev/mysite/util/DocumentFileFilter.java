package info.rsdev.mysite.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class DocumentFileFilter implements FileFilter {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".adoc");
    
    public static final DocumentFileFilter INSTANCE = new DocumentFileFilter();

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) { return false; }
        String filename = pathname.getName();
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0) { return false; }
        
        return SUPPORTED_EXTENSIONS.contains(filename.substring(lastDotIndex).toLowerCase());
    }
    
}
