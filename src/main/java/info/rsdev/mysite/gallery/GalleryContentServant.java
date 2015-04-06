package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.domain.Image;
import info.rsdev.mysite.gallery.domain.ImageCollection;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
public class GalleryContentServant implements RequestHandler, ConfigKeys {
    
    private static final String PAGESIZE_PARAM = "pageSize";
    private static final String PAGENUMBER_PARAM = "pageNumber";
    
    private final ImageCollection imageCollection;
    
    public GalleryContentServant(File collectionDir) {
        this.imageCollection = new ImageCollection(collectionDir);
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
        Map<String, String> requestParams = getRequestParameters(request);
        
        // query images to show
        String groupName = requestParams.get("imagegroup");
        List<Image> images = imageCollection.getImages(groupName);
        int imageCount = images.size();
        
        // construct page
        int pageSize = getPageSize(requestParams, galleryConfig);
        int pageNumber = getPageNumber(requestParams);  //base 1
        if (pageNumber == -1) {
            if (galleryConfig.showRandomFirstPage()) {
                //TODO: randomly generate which page to show
            } else {
                pageNumber = 1;
            }
        }
        int pageCount = imageCount / pageSize + 1;
        if ((pageNumber * pageSize) >= imageCount) {
            pageNumber = pageCount; //if page out of bounds, show last page
        }
        GalleryPageModel model = new GalleryPageModel(galleryConfig, groupName, pageNumber, pageSize);
        
        List<Image> imagesOnPage = images.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize) - 1);
        model.setImagesOnPage(imagesOnPage);
        model.setPageCount(pageCount);
        
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
        return 20;
    }

    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        return Collections.emptyMap();
    }
    
}
