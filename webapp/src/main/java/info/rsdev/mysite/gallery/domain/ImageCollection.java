package info.rsdev.mysite.gallery.domain;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

import info.rsdev.mysite.common.domain.resources.DefaultResourceCollection;
import info.rsdev.mysite.util.ImageFileFilter;

/**
 * <p>
 * The responsibility of this class is to inventory the given collection
 * directory and all of it's subdirectories for images. Supported image types
 * are png, jpg and gif. The images in a subdirectory are considered to be a
 * group, and the name of the subdirectory is considered to be the group name.
 * </p>
 * <p>
 * When there are more subdirectories (on different levels) with the same name,
 * they are considered to form a single group of images. When a group contains
 * multiple images with the same name, only the last image is included in the
 * collection,
 * </p>
 */
public class ImageCollection extends DefaultResourceCollection<ImageGroup, DefaultImage> {

    public ImageCollection(File siteDir, String collectionPath, String mountPoint, Locale language) {
        super(siteDir, collectionPath, mountPoint, language);
    }

    @Override
    public ImageGroup createAndAddNewGroup(String groupName) {
        ImageGroup target = getResourceGroup(groupName);
        if (target == null) {
            target = new ImageGroup(this, groupName);
            this.resourceGroups.add(target);
        }
        return target;
    }

    @Override
    public FileFilter getResourceFilter() {
        return ImageFileFilter.INSTANCE;
    }

}
