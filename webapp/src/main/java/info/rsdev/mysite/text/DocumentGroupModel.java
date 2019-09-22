package info.rsdev.mysite.text;

import java.util.List;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.text.domain.DefaultDocument;
import info.rsdev.mysite.text.domain.DocumentGroup;

/**
 * The information available to templates to generate the html page of one or
 * more documents. This is a DTO (Data Transfer Object).
 */
public class DocumentGroupModel extends BasicPageModel<DocumentModuleConfig> {

    private final DocumentGroup selectedGroup;

    public DocumentGroupModel(DocumentModuleConfig config, DocumentGroup selectedGroup) {
        super(config, selectedGroup.getName());
        this.selectedGroup = selectedGroup;
    }

    public List<DefaultDocument> getDocuments() {
        return selectedGroup.getAll();
    }
    
    /**
     * @return false, to indicate that this model contains a collection of documents
     */
    public boolean isSingle() {
        return false;
    }
}
