package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.gallery.domain.Image;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * The information available to templates to generate the html page of the photo gallery. This is a DTO (Data 
 * Transfer Object).
 */
public class GalleryPageModel extends BasicPageModel<GalleryModuleConfig>{
    
    private int pageNumber;
    
    private int pageSize;
    
    private int pageCount;
    
    private List<Image> imagesOnPage;
    
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

    public List<Image> getImagesOnPage() {
        return imagesOnPage;
    }

    public void setImagesOnPage(List<Image> imagesOnPage) {
        this.imagesOnPage = imagesOnPage;
    }
    
    public int getPageCount() {
        return this.pageCount;
    }
    
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
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
    
}
