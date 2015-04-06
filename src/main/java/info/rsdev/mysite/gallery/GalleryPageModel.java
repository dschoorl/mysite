package info.rsdev.mysite.gallery;

import info.rsdev.mysite.gallery.domain.Image;

import java.util.List;

/**
 * The information available to templates to generate the html page of the photo gallery. This is a DTO (Data 
 * Transfer Object).
 */
public class GalleryPageModel {
    
    private int pageNumber;
    
    private int pageSize;
    
    private int pageCount;
    
    private String imageGroup;
    
    private List<Image> imagesOnPage;
    
    private List<String> imageGroups;
    
    private GalleryModuleConfig config;
    
    public GalleryPageModel(GalleryModuleConfig config, String imageGroup, int pageNumber, int pageSize) {
        setConfig(config);
        setImageGroup(imageGroup);
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
        return imageGroup;
    }

    public void setImageGroup(String imageGroup) {
        this.imageGroup = imageGroup;
    }

    public List<Image> getImagesOnPage() {
        return imagesOnPage;
    }

    public void setImagesOnPage(List<Image> imagesOnPage) {
        this.imagesOnPage = imagesOnPage;
    }

    public List<String> getImageGroups() {
        return imageGroups;
    }

    public void setImageGroups(List<String> imageGroups) {
        this.imageGroups = imageGroups;
    }

    public GalleryModuleConfig getConfig() {
        return config;
    }

    public void setConfig(GalleryModuleConfig config) {
        this.config = config;
    }

    public int getPageCount() {
        return this.pageCount;
    }
    
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

}
