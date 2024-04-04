package info.rsdev.mysite.text.domain;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

import info.rsdev.mysite.common.domain.resources.DefaultResourceCollection;
import info.rsdev.mysite.util.DocumentFileFilter;

public class DocumentCollection extends DefaultResourceCollection<DocumentGroup, DefaultDocument> {
    
    private final Locale language;

    public DocumentCollection(File siteDir, String collectionPath, String mountPoint, Locale language) {
        super(siteDir, collectionPath, mountPoint);
        this.language = language;
    }

    @Override
    public DocumentGroup createAndAddNewGroup(String groupName) {
        DocumentGroup target = getResourceGroup(groupName);
        if (target == null) {
            target = new DocumentGroup(this, groupName, language);
            this.resourceGroups.add(target);
        }
        return target;
    }

    @Override
    public FileFilter getResourceFilter() {
        return DocumentFileFilter.INSTANCE;
    }

    public void addVirtualDocumentGroup(DocumentGroup virtualGroup) {
        this.resourceGroups.addFirst(virtualGroup);
    }
    
    public Locale getLanguage() {
        return this.language;
    }

}
