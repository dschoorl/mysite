package info.rsdev.mysite.gallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import info.rsdev.mysite.gallery.domain.DefaultImage;
import info.rsdev.mysite.gallery.domain.Image;
import info.rsdev.mysite.gallery.domain.ImageCollection;
import info.rsdev.mysite.gallery.domain.ImageGroup;
import info.rsdev.mysite.gallery.domain.ImageGroupMenuItem;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured image
 * collection, using the configured template.
 */
public class GalleryContentServant implements RequestHandler, ConfigKeys, RequestKeys {
    
    private final ImageCollection imageCollection;
    
    public GalleryContentServant(File siteDir, String collectionPath, String mountPoint) {
        this.imageCollection = new ImageCollection(siteDir, collectionPath, mountPoint);
    }
    
    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof GalleryModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", GalleryModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        GalleryModuleConfig galleryConfig = (GalleryModuleConfig) config;
        
        // analyse request
        Map<String, String> requestParams = getSupportedParameters(request);
        
        // query images to show
        String groupName = requestParams.get(IMAGEGROUP_PARAM);
        if (groupName == null) {
            List<ImageGroup> groups = imageCollection.getImageGroups();
            if (!groups.isEmpty()) {
                if (galleryConfig.showRandomFirstPage()) {
                    // randomly generate which group to show
                    int min = 0;
                    int max = groups.size();
                    groupName = groups.get(min + (int) (Math.random() * (max - min))).getName();
                } else {
                    List<String> groupNamesInDisplayOrder = galleryConfig.getVisibleMenuItems();
                    if (groupNamesInDisplayOrder.isEmpty()) {
                        groupName = groups.get(0).getName();
                    } else {
                        groupName = groupNamesInDisplayOrder.get(0);
                    }
                }
            }
        }
        List<DefaultImage> images = imageCollection.getImages(groupName);
        int imageCount = images.size();
        
        // construct page
        int pageSize = getPageSize(requestParams, galleryConfig, groupName);
        int pageNumber = getPageNumber(requestParams); // base 1
        int pageCount = imageCount / pageSize;
        if (pageNumber == -1) {
            if (galleryConfig.showRandomFirstPage()) {
                // randomly generate which page to show (between 1 and pageCount, inclusive)
                int min = 1;
                int max = pageCount + 1;
                pageNumber = min + (int) (Math.random() * ((max - min) + 1));
            } else {
                pageNumber = 1;
            }
        }
        if ((pageNumber * pageSize) > imageCount) {
            pageNumber = pageCount; // if page out of bounds, show last page
        }
        GalleryPageModel model = new GalleryPageModel(galleryConfig, groupName, pageNumber, pageSize);
        
        List<DefaultImage> imagesOnPage = null;
        if (imageCount == 0) {
            imagesOnPage = Collections.emptyList();
        } else {
            imagesOnPage = images.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize));
        }
        model.setImagesOnPage(imagesOnPage);
        model.setPageCount(pageCount);
        model.setMenu(menu);
        
        int focussedImageIndex = getFocussedImageIndex(requestParams, imagesOnPage.size());
        model.setFocusOn(focussedImageIndex);
        
        String templateName = renderPage(response, model);
        String contentId = determineContentId(templateName, model);
        return new ModuleHandlerResult(templateName, contentId);
    }
    
    private String determineContentId(String templateName, GalleryPageModel model) {
        //TODO: determine more intelligently; without knowledge of used template 
        if (templateName.equals("/slideshow")) {
            return new File(model.getImagesOnPage().get(0).getImagePath()).getName();
        } else {
            Image focussedImage = model.getFocussedImage();
            if (focussedImage != null) {
                return new File(focussedImage.getImagePath()).getName();
            } else {
                return model.getSelectedMenuItemName() + "_" + model.getPageNumber();
            }
        }
    }

    private String renderPage(HttpServletResponse response, GalleryPageModel pageModel) throws ServletException {
        GalleryModuleConfig galleryConfig = pageModel.getConfig();
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
    
    private int getPageNumber(Map<String, String> requestParams) {
        try {
            if (requestParams.containsKey(PAGENUMBER_PARAM)) {
                return Integer.parseInt(requestParams.get(PAGENUMBER_PARAM));
            }
        } catch (NumberFormatException e) {
            // Most likely, someone is tamparing the request - how to handle that situation??
        }
        return -1; // indicate that no valid pagenumber is submitted in the request
    }
    
    private int getPageSize(Map<String, String> requestParams, GalleryModuleConfig galleryConfig, String groupName) {
        try {
            if (requestParams.containsKey(PAGESIZE_PARAM)) {
                return Integer.parseInt(requestParams.get(PAGESIZE_PARAM));
            }
        } catch (NumberFormatException e) {
            // Most likely, someone is tamparing the request - how to handle that situation??
        }
        int pageSizeHint = galleryConfig.getImagesPerPage(groupName);
        if (pageSizeHint > 0) {
            return pageSizeHint;
        }
        return 20; // a hardcoded default (fallback value)
    }
    
    private int getFocussedImageIndex(Map<String, String> requestParams, int size) {
        int imageIndex = -1;
        try {
            if (requestParams.containsKey(IMAGENUMBER_PARAM)) {
                imageIndex = Integer.parseInt(requestParams.get(IMAGENUMBER_PARAM));
                if (imageIndex >= size) {
                    imageIndex = size;  //Most likely, someone is tamparing the request - how to handle that situation??
                }
            }
        } catch (NumberFormatException e) {
            // Most likely, someone is tamparing the request - how to handle that situation??
        }
        return imageIndex;  //No focus set on a specific image
    }

    private Map<String, String> getSupportedParameters(HttpServletRequest request) {
        Map<String, String> wellknownParameters = new HashMap<>();
        wellknownParameters.put(IMAGEGROUP_PARAM, request.getParameter(IMAGEGROUP_PARAM));
        wellknownParameters.put(PAGENUMBER_PARAM, request.getParameter(PAGENUMBER_PARAM));
        wellknownParameters.put(PAGESIZE_PARAM, request.getParameter(PAGESIZE_PARAM));
        wellknownParameters.put(IMAGENUMBER_PARAM, request.getParameter(IMAGENUMBER_PARAM));
        return wellknownParameters;
    }
    
    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        List<ImageGroup> imageGroups = this.imageCollection.getImageGroups();
        List<String> itemFilter = config.getVisibleMenuItems();
        List<MenuItem> visibleItems = null;
        if (itemFilter == null) {
            //No filter: show all menuitems
            visibleItems = new ArrayList<>(imageGroups.size());
            for (ImageGroup imageGroup : imageGroups) {
                visibleItems.add(new ImageGroupMenuItem(imageGroup));
            }
        } else {
            //show only the menuitems present in the filter, in the order they appear in the filter
            visibleItems  = new ArrayList<>(itemFilter.size());
            for (String itemName : itemFilter) {
                for (ImageGroup imageGroup : imageGroups) {
                    if (imageGroup.getName().equals(itemName)) {
                        visibleItems.add(new ImageGroupMenuItem(imageGroup));
                        break;
                    }
                }
            }
        }
        
        String menuTitle = config.getMenugroupTitle();
        int menuPriority = config.getMenuSortingPriority();
        return new DefaultMenuGroup(visibleItems, menuTitle, menuPriority);
    }
    
}
