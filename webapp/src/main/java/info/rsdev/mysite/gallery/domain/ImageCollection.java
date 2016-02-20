package info.rsdev.mysite.gallery.domain;

import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.util.DirectoryFilter;
import info.rsdev.mysite.util.ImageFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>The responsibility of this class is to inventory the given collection directory and all of it's subdirectories for images.
 * Supported image types are png, jpg and gif. The images in a subdirectory are considered to be a group, and the name of 
 * the subdirectory is considered to be the group name.</p>
 * <p>When there are more subdirectories (on different levels) with the same name, they are considered to form a single 
 * group of images. When a group contains multiple images with the same name, only the last image is included in the collection,
 * </p>
 */
public class ImageCollection {
    
    /**
     * The list of {@link DefaultImage}s in the imagegroup directory.
     */
    private final List<ImageGroup> imageGroups;
    
    private final String collectionPath;
    
    private final String mountPoint;
    
    public ImageCollection(File siteDir, String collectionPath, String mountPoint) {
        if (collectionPath == null) {
            throw new NullPointerException("Directory to image collection cannot be null");
        }
        File collectionDir = new File(siteDir, collectionPath);
        if (!collectionDir.isDirectory()) {
            throw new ConfigurationException(String.format("Not a directory: %s", collectionDir.getAbsolutePath()));
        }
        this.collectionPath = collectionPath;
        this.mountPoint = mountPoint;
        this.imageGroups = inventory(siteDir.toPath(), collectionDir, true);   //TODO: run in a separate thread?
    }
    
    public List<DefaultImage> getImages(String groupName) {
        ImageGroup group = getImageGroup(imageGroups, groupName);
        if (group != null) {
            return Collections.unmodifiableList(group.getImages());
        }
        return Collections.emptyList();
    }
    
    private ImageGroup getImageGroup(List<ImageGroup> imageGroups, String groupName) {
        for (ImageGroup candidate: imageGroups) {
            if (candidate.getName().equals(groupName)) {
                return candidate;
            }
        }
        return null;
    }
    
    public List<ImageGroup> getImageGroups() {
        return new ArrayList<>(imageGroups);
    }
    
    public String getPath() {
        return collectionPath;
    }
    
    public String getMountPoint() {
        return this.mountPoint;
    }
    
    protected List<ImageGroup> inventory(Path sitePath, File groupDir, boolean subdirsOnly) {
        LinkedList<ImageGroup> imageGroups = new LinkedList<>();
        
        //Depth-first: when there are subdirectories present in the directory, visit them first
        File[] subdirectories = groupDir.listFiles(DirectoryFilter.INSTANCE);
        for (File directory: subdirectories) {
            //take inventory of imagegroup in subdirectory and merge with our image group
            Collection<ImageGroup> subtreeImageGroups = inventory(sitePath, directory, false);
            for (ImageGroup group: subtreeImageGroups) {
                int index = imageGroups.indexOf(group);
                if (index >= 0) {
                    imageGroups.get(index).merge(group);
                } else {
                    imageGroups.add(group);
                }
            }
        }
        
        if (!subdirsOnly) {
            //take inventory of the images in the given directory
            ImageGroup newGroup = new ImageGroup(this, groupDir.getName());
            List<DefaultImage> images = new ArrayList<>();
            for (File image: groupDir.listFiles(ImageFileFilter.INSTANCE)) {
                images.add(new DefaultImage(newGroup, image));
            }
            newGroup.addImages(images);
            ImageGroup current = getImageGroup(imageGroups, groupDir.getName());
            if (current == null) {
                imageGroups.add(newGroup);
            } else {
                current.merge(newGroup);
            }
        }
        
        return imageGroups;
    }
}
