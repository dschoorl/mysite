package info.rsdev.mysite.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class DocumentFileFilter implements FileFilter {

    /**
     * The .adoc extension represents an asciidoc file
     */
    public static final String ADOC_EXT = "adoc";

    /**
     * the .metaOnly extension a Java xml properties file that contains the content
     * in the teaser section (when there is no document)
     */
    public static final String META_ONLY_EXT = "meta-only";

    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(ADOC_EXT, META_ONLY_EXT);

    public static final DocumentFileFilter INSTANCE = new DocumentFileFilter();

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return false;
        }
        String filename = pathname.getName();
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return false;
        }

        return SUPPORTED_EXTENSIONS.contains(filename.substring(lastDotIndex + 1).toLowerCase());
    }

}
