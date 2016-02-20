package info.rsdev.mysite.common.domain.accesslog;

public class ModuleHandlerResult {
    
    public static final ModuleHandlerResult NO_CONTENT = new ModuleHandlerResult(null, null);
    
    private final String contentId;
    
    private final String templateName;
    
    public ModuleHandlerResult(String templateName, String contentId) {
        this.templateName = templateName;
        this.contentId = contentId;
    }
    
    public String getContentId() {
        return contentId;
    }

    public String getTemplateName() {
        return templateName;
    }

}
