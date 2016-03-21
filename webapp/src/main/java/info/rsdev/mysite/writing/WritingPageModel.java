package info.rsdev.mysite.writing;

import java.util.List;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.writing.domain.Document;
import info.rsdev.mysite.writing.domain.Part;
import info.rsdev.mysite.writing.domain.Version;

/**
 * The information available to templates to generate the html page of one or more documents. This is a DTO (Data 
 * Transfer Object).
 */
public class WritingPageModel extends BasicPageModel<WritingModuleConfig>{
    
    private final Document selectedDocument;
    
    private final int activePageIndex = 0;  //TODO: set dynamically
    
    public WritingPageModel(WritingModuleConfig config, Document selectedDocument) {
        super(config, selectedDocument.getTechnicalName());
        this.selectedDocument = selectedDocument;
    }
    
    public String getDocumentTitle() {
        return this.selectedDocument.getPublishedVersion().getTitle();
    }
    
    public Part getPage() {
        return this.selectedDocument.getPublishedVersion().getContent().get(activePageIndex);
    }
    
    public String getPageAsHtml() {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
    
    public int getPageCount() {
        Version active = this.selectedDocument.getPublishedVersion();
        int pageCount = countPageBreaks(active.getPreface());
        pageCount += countPageBreaks(active.getContent());
        pageCount += countPageBreaks(active.getEpilogue());
        return pageCount + 1;   //there is at least one page, even if it's an empty page
    }
    
    private int countPageBreaks(List<Part> parts) {
        int pageBreakCount = 0;
        for (Part part: parts) {
            if (part.isAppendPageBreak()) {
                pageBreakCount++;
            }
        }
        return pageBreakCount;
    }
    
}
