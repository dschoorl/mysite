package info.rsdev.mysite.text;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.text.domain.DefaultDocument;

/**
 * The information available to templates to generate the html page of one or
 * more documents. This is a DTO (Data Transfer Object).
 */
public class DocumentPageModel extends BasicPageModel<DocumentModuleConfig> {

    private final DefaultDocument selectedDocument;

    public DocumentPageModel(DocumentModuleConfig config, DefaultDocument selectedDocument) {
        super(config, selectedDocument.getGroup().getName());
        this.selectedDocument = selectedDocument;
    }

    public DefaultDocument getDocument() {
        return this.selectedDocument;
    }

    public String getPage() {
        return this.selectedDocument.getContent();
    }

    /**
     * @return true to indicate that this model contains only a single document
     */
    public boolean isSingle() {
        return true;
    }
}
