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
        super(config, selectedDocument.getTechnicalName());
        this.selectedDocument = selectedDocument;
    }

    public String getDocumentTitle() {
        return this.selectedDocument.getTitle();
    }

    public String getPage() {
        return this.selectedDocument.getContent();
    }

}
