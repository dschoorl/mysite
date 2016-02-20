package info.rsdev.mysite.gallery;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.gallery.domain.DefaultImage;
import info.rsdev.mysite.gallery.domain.FocussedImage;
import info.rsdev.mysite.gallery.domain.Image;

/**
 * The information available to templates to generate the html page of the photo gallery. This is a DTO (Data 
 * Transfer Object).
 */
public class GalleryPageModel extends BasicPageModel<GalleryModuleConfig>{
    
    /**
     * The page number of the current page, base = 1.
     */
    private int pageNumber;
    
    private int pageSize;
    
    private int pageCount;
    
    private List<DefaultImage> imagesOnPage;
    
    private int focussedImageIndex = -1;
    
    public GalleryPageModel(GalleryModuleConfig config, String imageGroup, int pageNumber, int pageSize) {
        super(config, imageGroup);
        setPageNumber(pageNumber);
        setPageSize(pageSize);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getImageGroup() {
        return getSelectedMenuItemName();
    }

    public List<DefaultImage> getImagesOnPage() {
        return imagesOnPage;
    }

    public void setImagesOnPage(List<DefaultImage> imagesOnPage) {
        this.imagesOnPage = imagesOnPage;
    }
    
    public Image getFocussedImage() {
        if (focussedImageIndex >= 0) {
            return new FocussedImage(imagesOnPage.get(focussedImageIndex), focussedImageIndex + 1);
        }
        return null;
    }
    
    public void setFocusOn(int imageIndex) {
        focussedImageIndex = imageIndex;
    }
    
    public int getPageCount() {
        return this.pageCount;
    }
    
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    
    public String getBaseUrl() {
        try {
            return String.format("?imagegroup=%s&%s=%d&%s=", 
                    URLEncoder.encode(getSelectedMenuItemName(), "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    pageNumber,
                    RequestKeys.IMAGENUMBER_PARAM);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getNextPageUrl() {
        if (pageNumber+1 > pageCount) {
            return null;
        }
        try {
            return String.format("?imagegroup=%s&%s=%d", 
                    URLEncoder.encode(getSelectedMenuItemName(), "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    pageNumber + 1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getPreviousPageUrl() {
        if (pageNumber <= 1) {
            return null;
        }
        try {
            return String.format("?imagegroup=%s&%s=%d", 
                    URLEncoder.encode(getSelectedMenuItemName(), "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    pageNumber - 1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getNextImageUrl() {
        int nextImageIndex = focussedImageIndex+1;
        int nextPageIndex = pageNumber;
        if (nextImageIndex >= imagesOnPage.size()) {
            if (nextPageIndex < pageCount) {
                nextPageIndex++;
                nextImageIndex = 0; //set focus on first image of next page
            } else {
                return null;    //We are at the last image of the image group: there is no next image
            }
        }
        
        try {
            return String.format("?imagegroup=%s&%s=%d&%s=%d", 
                    URLEncoder.encode(getSelectedMenuItemName(), "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    nextPageIndex,
                    RequestKeys.IMAGENUMBER_PARAM,
                    nextImageIndex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getPreviousImageUrl() {
        //this is copied from getPreviousPageUrl (for inspiration): TODO: implement this method
        int prevImageIndex = focussedImageIndex-1;
        int prevPageIndex = pageNumber;
        if (prevImageIndex < 0) {
            if (prevPageIndex > 1) {
                prevPageIndex--;
                prevImageIndex = pageSize-1; //set focus on last image of previous page
            } else {
                return null;    //We are at the first image of the image group: there is no previous image
            }
        }
        
        try {
            return String.format("?imagegroup=%s&%s=%d&%s=%d", 
                    URLEncoder.encode(getSelectedMenuItemName(), "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    prevPageIndex,
                    RequestKeys.IMAGENUMBER_PARAM,
                    prevImageIndex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
}
