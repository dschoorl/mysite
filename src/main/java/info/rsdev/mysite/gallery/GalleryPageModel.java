package info.rsdev.mysite.gallery;

import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.gallery.domain.Image;
import info.rsdev.mysite.gallery.domain.ImageGroup;
import info.rsdev.mysite.gallery.domain.ImageGroupMenuItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The information available to templates to generate the html page of the photo gallery. This is a DTO (Data 
 * Transfer Object).
 */
public class GalleryPageModel {
    
    private int pageNumber;
    
    private int pageSize;
    
    private int pageCount;
    
    private String imageGroupName;
    
    private List<Image> imagesOnPage;
    
    private List<ImageGroup> imageGroups;
    
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
        return imageGroupName;
    }

    public void setImageGroup(String imageGroup) {
        this.imageGroupName = imageGroup;
    }

    public List<Image> getImagesOnPage() {
        return imagesOnPage;
    }

    public void setImagesOnPage(List<Image> imagesOnPage) {
        this.imagesOnPage = imagesOnPage;
    }

    public List<ImageGroup> getImageGroups() {
        return imageGroups;
    }

    public void setImageGroups(List<ImageGroup> imageGroups) {
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
    
    protected List<String> getSortedVisibleItems() {
        String itemString = config.getString(ConfigKeys.APPROVED_MENUITEMS);
        if (itemString == null) {
            List<String> visibleItemsInOrder = new LinkedList<>();
            for (ImageGroup imageGroup: imageGroups) {
                //assumption: name of the group will be the caption for the menuitem
                visibleItemsInOrder.add(imageGroup.getName());
            }
            return visibleItemsInOrder;
        }
        return Arrays.asList(itemString.split(":"));
    }
    
    public String getNextPageUrl() {
        if (pageNumber+1 > pageCount) {
            return null;
        }
        try {
            return String.format("?imagegroup=%s&%s=%d", 
                    URLEncoder.encode(this.imageGroupName, "UTF-8"),
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
                    URLEncoder.encode(this.imageGroupName, "UTF-8"),
                    RequestKeys.PAGENUMBER_PARAM,
                    pageNumber - 1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<MenuItem> getMenuItems() {
        List<String> visibleItemsInOrder = getSortedVisibleItems();
        ArrayList<MenuItem> visibleItems = new ArrayList<>(visibleItemsInOrder.size());
        for (String itemName: visibleItemsInOrder) {
            for (ImageGroup imageGroup: imageGroups) {
                if (imageGroup.getName().equals(itemName)) {
                    visibleItems.add(new ImageGroupMenuItem(imageGroup));
                    break;
                }
            }
        }
        return visibleItems;
    }

}
