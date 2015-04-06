package info.rsdev.mysite.gallery.domain;

import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.util.DirectoryFilter;
import info.rsdev.mysite.util.ImageFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>The responsibility of this class is to inventory the given collection directory and all of it's subdirectories for images.
 * Supported image types are png, jpg and gif. The images in a subdirectory are considered to be a group, and the name of 
 * the subdirectory is considered to be the group name.</p>
 * <p>When there are more subdirectories (on different levels) with the same name, they are considered to form a single 
 * group of images. When a group contains multiple images with the same name, only the last image is included in the collection,
 * </p>
 */
public class ImageCollection {
    
    private final Map<String, List<Image>> imagesByGroup;
    
    public ImageCollection(File collectionDir) {
        if (collectionDir == null) {
            throw new NullPointerException("Directory to image collection cannot be null");
        }
        if (!collectionDir.isDirectory()) {
            throw new ConfigurationException(String.format("Not a directory: %s", collectionDir.getAbsolutePath()));
        }
        imagesByGroup = inventory(collectionDir);   //TODO: run in a separate thread?
    }
    
    public List<Image> getImages(String groupName) {
        if (!imagesByGroup.containsKey(groupName)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(imagesByGroup.get(groupName));
    }
    
    protected Map<String, List<Image>> inventory(File groupDir) {
        Map<String, List<Image>> imagesByGroup = new HashMap<>();
        
        //Depth-first: when there are subdirectories present in the directory, visit them first
        File[] subdirectories = groupDir.listFiles(DirectoryFilter.INSTANCE);
        for (File directory: subdirectories) {
            //take inventory of imagegroup in subdirectory and merge with our image group
            Map<String, List<Image>> subtreeImageGroups = inventory(directory);
            for (Entry<String, List<Image>> entry: subtreeImageGroups.entrySet()) {
                if (imagesByGroup.containsKey(entry.getKey())) {
                    imagesByGroup.get(entry.getKey()).addAll(entry.getValue());
                } else {
                    imagesByGroup.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        //take inventory of the images in the given directory
        String groupName = groupDir.getName();
        List<Image> imageGroup = imagesByGroup.get(groupName);
        if (imageGroup == null) {
            imageGroup = new ArrayList<Image>();
            imagesByGroup.put(groupName, imageGroup);
        }
        for (File image: groupDir.listFiles(ImageFileFilter.INSTANCE)) {
            imageGroup.add(new Image(image));
        }
        return imagesByGroup;
    }
}
