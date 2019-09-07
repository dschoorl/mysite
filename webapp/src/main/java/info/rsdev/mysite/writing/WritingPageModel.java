package info.rsdev.mysite.writing;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.writing.domain.Document;

/**
 * The information available to templates to generate the html page of one or
 * more documents. This is a DTO (Data Transfer Object).
 */
public class WritingPageModel extends BasicPageModel<WritingModuleConfig> {

    private final Document selectedDocument;

    public WritingPageModel(WritingModuleConfig config, Document selectedDocument) {
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
