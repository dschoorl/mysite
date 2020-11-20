package info.rsdev.mysite.text.domain;

import java.io.File;
import java.io.FileFilter;

import info.rsdev.mysite.common.domain.resources.DefaultResourceCollection;
import info.rsdev.mysite.util.DocumentFileFilter;

public class DocumentCollection extends DefaultResourceCollection<DocumentGroup, DefaultDocument> {

    public DocumentCollection(File siteDir, String collectionPath, String mountPoint) {
        super(siteDir, collectionPath, mountPoint);
    }

    @Override
    public DocumentGroup createAndAddNewGroup(String groupName) {
        DocumentGroup target = getResourceGroup(groupName);
        if (target == null) {
            target = new DocumentGroup(this, groupName);
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

}
