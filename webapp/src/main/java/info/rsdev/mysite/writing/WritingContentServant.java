package info.rsdev.mysite.writing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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
import info.rsdev.mysite.writing.dao.IReadingDao;
import info.rsdev.mysite.writing.domain.Document;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating
 * access to the images from the configured image collection, using the
 * configured template.
 */
public class WritingContentServant implements RequestHandler, ConfigKeys {

    private final IReadingDao dao;

    @Inject
    public WritingContentServant(IReadingDao dao) {
        this.dao = dao;
    }

    @Override
    public ModuleHandlerResult handle(ModuleConfig moduleConfig, List<MenuGroup> menu, HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (moduleConfig == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(moduleConfig instanceof WritingModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", WritingModuleConfig.class.getSimpleName(), moduleConfig, MODULECONFIGTYPE_KEY));
        }
        WritingModuleConfig writingConfig = (WritingModuleConfig) moduleConfig;

        String path = getPathPartAfterMountpoint(request.getPathInfo(), moduleConfig.getMountPoint());
        Document selectedDocument = dao.getDocumentByName(path);
        if (selectedDocument == null) {
            return ModuleHandlerResult.NO_CONTENT;
        }
        WritingPageModel model = new WritingPageModel(writingConfig, selectedDocument);

        model.setMenu(menu);

        String templateName = renderPage(response, model);
        String contentId = determineContentId(templateName, model);
        return new ModuleHandlerResult(templateName, contentId);
    }

    private String getPathPartAfterMountpoint(String pathInfo, String mountpoint) {
        mountpoint = "/" + mountpoint;
        if (pathInfo.startsWith(mountpoint)) {
            return pathInfo.substring(mountpoint.length());
        }
        return null;
    }

    private String determineContentId(String templateName, WritingPageModel model) {
        return model.getSelectedMenuItemName();
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
        return template == null ? null : template.getName();
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        List<MenuItem> visibleItems = new ArrayList<>();
        String menuTitle = config.getMenugroupTitle();
        int menuPriority = config.getMenuSortingPriority();
        return new DefaultMenuGroup(visibleItems, menuTitle, menuPriority);
    }

}
