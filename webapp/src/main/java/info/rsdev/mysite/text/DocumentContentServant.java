package info.rsdev.mysite.text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.stringtemplate.v4.ST;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.CorePageModel;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.text.domain.DefaultDocument;
import info.rsdev.mysite.text.domain.DocumentCollection;
import info.rsdev.mysite.text.domain.DocumentGroup;
import info.rsdev.mysite.text.domain.DocumentGroupMenuItem;
import info.rsdev.mysite.text.domain.RecentDocumentGroup;
import info.rsdev.mysite.util.ServletUtils;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating
 * access to the documents from the configured document collection, rendering them
 * with the configured template.
 */
public class DocumentContentServant implements RequestHandler, ConfigKeys {

    private final DocumentCollection documentCollection;

    public DocumentContentServant(File siteDir, String collectionPath, String mountPoint) {
        this.documentCollection = new DocumentCollection(siteDir, collectionPath, mountPoint);
    }

    @Override
    public ModuleHandlerResult handle(ModuleConfig moduleConfig, List<MenuGroup> menu, HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if (moduleConfig == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(moduleConfig instanceof DocumentModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", DocumentModuleConfig.class.getSimpleName(), moduleConfig,
                    MODULECONFIGTYPE_KEY));
        }
        DocumentModuleConfig writingConfig = (DocumentModuleConfig) moduleConfig;

        CorePageModel<?> model = null;
        String path = getPathPartAfterMountpoint(request.getPathInfo(), moduleConfig.getMountPoint());
        String groupName = getGroupName(path);
        if (groupName == null) {
            // set a default group and continue
            DocumentGroup group = documentCollection.getGroups().get(0);
            model = new DocumentGroupModel(writingConfig, group);
        } else {
            DocumentGroup documentGroup = documentCollection.getResourceGroup(groupName);
            if (documentGroup == null) {
                return ModuleHandlerResult.NO_CONTENT;
            }
            String documentName = getDocumentName(path.substring(groupName.length()));
            if (documentName == null) {
                model = new DocumentGroupModel(writingConfig, documentGroup);
            } else {
                DefaultDocument selectedDocument = documentGroup.getDocument(documentName);
                if (selectedDocument == null) {
                    return ModuleHandlerResult.NO_CONTENT;
                }
                model = new DocumentPageModel(writingConfig, selectedDocument);
            }
        }

        model.setMenu(menu);

        String templateName = renderPage(response, model);
        String contentId = determineContentId(templateName, model);
        return new ModuleHandlerResult(templateName, contentId);
    }

    private String getPathPartAfterMountpoint(String pathInfo, String mountpoint) {
        mountpoint = "/" + mountpoint;
        if (pathInfo.startsWith(mountpoint)) {
            // TODO: strip any posible trailing forward slash
            String targetPath = pathInfo.substring(mountpoint.length());
            if(targetPath.startsWith("/")) {
                targetPath = targetPath.substring(1);
            }
            if (targetPath.endsWith("/")) {
                targetPath = targetPath.substring(0, targetPath.length() - 1);
            }
            return targetPath;
        }
        return null;
    }

    private String getGroupName(String path) {
        return ServletUtils.getFirstPathElement(path);
    }

    private String getDocumentName(String path) {
        return ServletUtils.getFirstPathElement(path);
    }

    private String determineContentId(String templateName, CorePageModel<?> model) {
        if (model instanceof DocumentPageModel) {
            return ((DocumentPageModel)model).getDocument().getTitle();
        }
        return model.getSelectedMenuItemName();
    }

    private String renderPage(HttpServletResponse response, CorePageModel<?> pageModel) throws ServletException {
        ModuleConfig documentConfig = pageModel.getConfig();
        ST template = documentConfig.getTemplate(pageModel.getSelectedMenuItemName());
        try {
            if (template == null) {
                response.sendError(404);
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                template.add("model", pageModel);
                response.getWriter().write(template.render());
            }
        } catch (IOException e) {
            throw new ServletException("Error occured during preparation of web page", e);
        }
        return template == null ? null : template.getName();
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        List<DocumentGroup> imageGroups = this.documentCollection.getGroups();
        List<String> itemFilter = config.getVisibleMenuItems();
        List<MenuItem> visibleItems = null;
        if (itemFilter == null) {
            // No filter: show all menuitems
            visibleItems = new ArrayList<>(imageGroups.size());
            for (DocumentGroup imageGroup : imageGroups) {
                visibleItems.add(new DocumentGroupMenuItem(imageGroup));
            }
        } else {
            // show only the menuitems present in the filter, in the order they
            // appear in the filter
            visibleItems = new ArrayList<>(itemFilter.size());
            for (String itemName : itemFilter) {
                for (DocumentGroup imageGroup : imageGroups) {
                    if (imageGroup.getName().equals(itemName)) {
                        visibleItems.add(new DocumentGroupMenuItem(imageGroup));
                        break;
                    }
                }
            }
        }

        String menuTitle = config.getMenugroupTitle();
        int menuPriority = config.getMenuSortingPriority();
        return new DefaultMenuGroup(visibleItems, menuTitle, menuPriority);
    }

    public void addRecentDocumentsGroup(String recentDocumentsGroupName, int daysCutoffPoint) {
        RecentDocumentGroup virualGroup = new RecentDocumentGroup(documentCollection, recentDocumentsGroupName, daysCutoffPoint);
        if (!virualGroup.getAll().isEmpty()) {
            documentCollection.addVirtualDocumentGroup(virualGroup);
        }
    }

}
