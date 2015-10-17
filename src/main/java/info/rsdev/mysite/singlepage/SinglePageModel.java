package info.rsdev.mysite.singlepage;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.singlepage.domain.SinglePage;

public class SinglePageModel extends BasicPageModel<SinglePageModuleConfig> {
    
    private final SinglePage content;
    
    public SinglePageModel(SinglePageModuleConfig config, SinglePage content) {
        super(config, content.getName());
        this.content = content;
    }

    public String getPageName() {
        return getSelectedMenuItemName();
    }

    public String getContent() {
        return content.getContent();
    }
    
}
