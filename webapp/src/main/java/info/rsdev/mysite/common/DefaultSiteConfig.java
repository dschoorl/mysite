package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.MenuGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultSiteConfig implements SiteConfig {
    
    private String siteName = null;
    
    private Map<String, ModuleConfig> moduleConfigByMountPoint = new HashMap<>();
    
    public DefaultSiteConfig(String siteName, Map<String, ModuleConfig> configByMountpoint) {
        this.siteName = siteName;
        this.moduleConfigByMountPoint.putAll(configByMountpoint);
    }
     
    @Override
    public ModuleConfig getModuleConfig(String requestPath) {
        //TODO: handle overlapping paths...
        LinkedList<ModuleConfig> candidates = new LinkedList<>();
        for (ModuleConfig candidate: moduleConfigByMountPoint.values()) {
            if (candidate.hasHandlerFor(requestPath)) {
                candidates.add(candidate);
            }
        }
        return selectBestFit(candidates);
    }
    
    /**
     * when there are multiple candidates, select the one with the most path segments in the mountpoint, because
     * mountpoint takes precendence over content path when there is overlap
     * @param candidates
     * @return
     */
    ModuleConfig selectBestFit(List<ModuleConfig> candidates) {
        ModuleConfig bestFit = null;
        for (ModuleConfig candidate: candidates) {
            if ((bestFit == null) || (countPathElements(bestFit.getMountPoint()) < countPathElements(candidate.getMountPoint()))) {
                bestFit = candidate;
            }
        }
        return bestFit;
    }
    
    private int countPathElements(String path) {
        if ((path == null) || path.isEmpty()) {
            return 0;
        }
        return path.split(Pattern.quote("/")).length;
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
            if ((menuGroup != null) && !menuGroup.isEmpty()) {
                menu.add(menuGroup);
            }
        }
        return menu;
    }
    
}
