package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.domain.Image;
import info.rsdev.mysite.gallery.domain.ImageCollection;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtemplate.v4.ST;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured 
 * image collection, using the configured template.
 */
public class GalleryContentServant implements RequestHandler, ConfigKeys, RequestKeys {
    
    private final ImageCollection imageCollection;
    
    public GalleryContentServant(File siteDir, String collectionPath, String mountPoint) {
        this.imageCollection = new ImageCollection(siteDir, collectionPath, mountPoint);
    }
    
    @Override
    public void handle(ModuleConfig config, HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof GalleryModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", GalleryModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        GalleryModuleConfig galleryConfig = (GalleryModuleConfig)config;
        
        // analyse request
        Map<String, String> requestParams = getSupportedParameters(request);
        
        // query images to show
        String groupName = requestParams.get(IMAGEGROUP_PARAM);
        List<Image> images = imageCollection.getImages(groupName);
        int imageCount = images.size();
        
        // construct page
        int pageSize = getPageSize(requestParams, galleryConfig);
        int pageNumber = getPageNumber(requestParams);  //base 1
        int pageCount = imageCount / pageSize;
        if (pageNumber == -1) {
            if (galleryConfig.showRandomFirstPage()) {
                //randomly generate which page to show (between 1 and pageCount, inclusive)
                int min = 1;
                int max = pageCount + 1;
                pageNumber = min + (int)(Math.random() * ((max - min) + 1));
            } else {
                pageNumber = 1;
            }
        }
        if ((pageNumber * pageSize) > imageCount) {
            pageNumber = pageCount; //if page out of bounds, show last page
        }
        GalleryPageModel model = new GalleryPageModel(galleryConfig, groupName, pageNumber, pageSize);
        
        List<Image> imagesOnPage = null;
        if (imageCount==0) {
            imagesOnPage = Collections.emptyList();
        } else { 
            imagesOnPage = images.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize));
        }
        model.setImagesOnPage(imagesOnPage);
        model.setPageCount(pageCount);
        model.setImageGroups(imageCollection.getImageGroups());
        
        renderPage(response, model);
    }
    
    private void renderPage(HttpServletResponse response, GalleryPageModel pageModel) throws ServletException {
        GalleryModuleConfig galleryConfig = pageModel.getConfig();
        ST template = galleryConfig.getTemplate();
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
    }

    private int getPageNumber(Map<String, String> requestParams) {
        try {
            if (requestParams.containsKey(PAGENUMBER_PARAM)) {
                return Integer.parseInt(requestParams.get(PAGENUMBER_PARAM));
            }
        } catch (NumberFormatException e) {
            //Most likely, someone is tamparing the request - how to handle that situation??
        }
        return -1;  //indicate that no valid pagenumber is submitted in the request
    }

    private int getPageSize(Map<String, String> requestParams, GalleryModuleConfig galleryConfig) {
        try {
            if (requestParams.containsKey(PAGESIZE_PARAM)) {
                return Integer.parseInt(requestParams.get(PAGESIZE_PARAM));
            }
        } catch (NumberFormatException e) {
            //Most likely, someone is tamparing the request - how to handle that situation??
        }
        int pageSizeHint = galleryConfig.getInteger(IMAGES_PER_PAGE_HINT_KEY);
        if (pageSizeHint > 0) { return pageSizeHint; }
        return 20;  //a hardcoded default (fallback value)
    }

    private Map<String, String> getSupportedParameters(HttpServletRequest request) {
        Map<String, String> wellknownParameters = new HashMap<>();
        wellknownParameters.put(IMAGEGROUP_PARAM, request.getParameter(IMAGEGROUP_PARAM));
        wellknownParameters.put(PAGENUMBER_PARAM, request.getParameter(PAGENUMBER_PARAM));
        wellknownParameters.put(PAGESIZE_PARAM, request.getParameter(PAGESIZE_PARAM));
        return wellknownParameters;
    }
    
}
