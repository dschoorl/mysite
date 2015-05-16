package info.rsdev.mysite.singlepage;

import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.singlepage.domain.SinglePage;

import java.util.Collections;
import java.util.List;

public class SinglePageModel {
    
    private List<MenuGroup> menu = Collections.emptyList();
    
    private final SinglePageModuleConfig config;
    
    private final SinglePage content;
    
    public SinglePageModel(SinglePageModuleConfig config, SinglePage content) {
        this.config = config;
        this.content = content;
    }

    public List<MenuGroup> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuGroup> menu) {
        this.menu = menu;
        markActiveItem(this.menu, this.content.getName());
    }

    private void markActiveItem(List<MenuGroup> menu, String pageName) {
        for (MenuGroup menuGroup: menu) {
            if (menuGroup.markActive(pageName)) {
                break;
            }
        }
    }
    
    public SinglePageModuleConfig getConfig() {
        return config;
    }

    public String getPageName() {
        return content.getName();
    }

    public String getContent() {
        return content.getContent();
    }
    
}
