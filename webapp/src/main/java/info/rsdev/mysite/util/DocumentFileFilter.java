package info.rsdev.mysite.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import info.rsdev.mysite.text.domain.DefaultDocument;

public class DocumentFileFilter implements FileFilter {
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".adoc");
    
    public static final DocumentFileFilter INSTANCE = new DocumentFileFilter();

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) { return false; }
        String filename = pathname.getName();
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0) { return false; }
        
        //do not accept metadata files! -- their name, the part before the (mandatory) extension, ends with _t
        if ((lastDotIndex > DefaultDocument.METADATA_INDICATOR.length()) && filename.substring(0, lastDotIndex).endsWith(DefaultDocument.METADATA_INDICATOR)) {
            return false;
        }
        
        return SUPPORTED_EXTENSIONS.contains(filename.substring(lastDotIndex).toLowerCase());
    }
    
}
