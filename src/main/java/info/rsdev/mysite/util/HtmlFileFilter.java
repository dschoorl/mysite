package info.rsdev.mysite.util;

import java.io.File;
import java.io.FileFilter;

public class HtmlFileFilter implements FileFilter {
    
    public static final HtmlFileFilter INSTANCE = new HtmlFileFilter();

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) { return false; }
        return pathname.getName().toLowerCase().endsWith(".html");
    }
    
}
