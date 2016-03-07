package info.rsdev.mysite.writing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtemplate.v4.ST;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured image
 * collection, using the configured template.
 */
public class WritingContentServant implements RequestHandler, ConfigKeys {
    
    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof WritingModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", WritingModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        WritingModuleConfig writingConfig = (WritingModuleConfig) config;
        
        WritingPageModel model = new WritingPageModel(writingConfig, null);
        
        model.setMenu(menu);
        
        String templateName = renderPage(response, model);
        String contentId = determineContentId(templateName, model);
        return new ModuleHandlerResult(templateName, contentId);
    }
    
    private String determineContentId(String templateName, WritingPageModel model) {
        return null;
    }

    private String renderPage(HttpServletResponse response, WritingPageModel pageModel) throws ServletException {
        WritingModuleConfig galleryConfig = pageModel.getConfig();
        ST template = galleryConfig.getTemplate(pageModel.getSelectedMenuItemName());
        try {
            if (template == null) {
                response.sendError(404);
            } else {
                template.add("model", pageModel);
                response.getWriter().write(template.render());
                response.setContentType("text/html");
            }
        } catch (IOException e) {
            throw new ServletException("Error occured during preparation of web page", e);
        }
        return template==null?null:template.getName();
    }
    
    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        List<MenuItem> visibleItems = new ArrayList<>();
        String menuTitle = config.getMenugroupTitle();
        int menuPriority = config.getMenuSortingPriority();
        return new DefaultMenuGroup(visibleItems, menuTitle, menuPriority);
    }
    
}
