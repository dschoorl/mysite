package info.rsdev.mysite.util;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFilter implements FileFilter {

    public static final DirectoryFilter INSTANCE = new DirectoryFilter();

    @Override
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
    
}
