package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.MenuGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultSiteConfig implements SiteConfig {
    
    private String siteName = null;
    
    private Map<String, ModuleConfig> moduleConfigByMountPoint = new HashMap<>();
    
    public DefaultSiteConfig(String siteName, Map<String, ModuleConfig> configByMountpoint) {
        this.siteName = siteName;
        this.moduleConfigByMountPoint.putAll(configByMountpoint);
    }
     
    @Override
    public ModuleConfig getModuleConfig(String requestPath) {
        for (ModuleConfig candidate: moduleConfigByMountPoint.values()) {
            if (candidate.hasHandlerFor(requestPath)) {
                return candidate;
            }
        }
        return null;
    }
    
    @Override
    public String getSiteName() {
        return siteName;
    }

    @Override
    public List<MenuGroup> getMenu() {
        List<MenuGroup> menu = new LinkedList<>();
        for (ModuleConfig config: moduleConfigByMountPoint.values()) {
            MenuGroup menuGroup = config.getRequestHandler().getMenuItems(config);
            if (!menuGroup.isEmpty()) {
                menu.add(menuGroup);
            }
        }
        return menu;
    }
    
}
