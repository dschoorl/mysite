package info.rsdev.mysite.singlepage.domain;

import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.util.HtmlFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SinglePageCollection {
    
    private final List<SinglePage> pages;
    
    public SinglePageCollection(File siteDir, String pageCollectionPath, String mountPoint) {
        if (pageCollectionPath == null) {
            throw new NullPointerException("Directory to singlepage collection cannot be null");
        }
        File collectionDir = new File(siteDir, pageCollectionPath);
        if (!collectionDir.isDirectory()) {
            throw new ConfigurationException(String.format("Not a directory: %s", collectionDir.getAbsolutePath()));
        }
        this.pages = Collections.unmodifiableList(inventory(collectionDir, mountPoint));
    }
    
    public SinglePage getPage(String name) {
        for (SinglePage page: pages) {
            if (page.getName().equals(name)) {
                return page;
            }
        }
        return null;
    }
    
    public List<SinglePage> getPages() {
        return this.pages;
    }
    
    private List<SinglePage> inventory(File collectionDir, String mountPoint) {
        File[] pageFiles = collectionDir.listFiles(HtmlFileFilter.INSTANCE);
        if (pageFiles == null) {
            throw new NullPointerException(String.format("Could not read directory with singlepage files: %s",
                    collectionDir.getAbsolutePath()));
        }
        
        List<SinglePage> pages = new ArrayList<>(pageFiles.length);
        for (File pageFile: pageFiles) {
            pages.add(new SinglePage(pageFile, mountPoint));
        }
        return pages;
    }
}
