package info.rsdev.mysite.common.domain.resources;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.domain.DefaultImage;
import info.rsdev.mysite.util.DirectoryFilter;

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
public abstract class DefaultResourceCollection<G extends ResourceGroup<T>, T extends PathResource>
        implements ResourceCollection<T> {

    /**
     * The list of {@link DefaultImage}s in the imagegroup directory.
     */
    protected final List<G> resourceGroups = new LinkedList<>();

    private final String collectionPath;

    private final String mountPoint;

    public DefaultResourceCollection(File siteDir, String collectionPath, String mountPoint) {
        if (collectionPath == null) {
            throw new NullPointerException("Directory to resource collection cannot be null");
        }
        File collectionDir = new File(siteDir, collectionPath);
        if (!collectionDir.isDirectory()) {
            throw new ConfigurationException(String.format("Not a directory: %s", collectionDir.getAbsolutePath()));
        }
        this.collectionPath = collectionPath;
        this.mountPoint = mountPoint;

        // TODO: run in a separate thread?
        this.resourceGroups.addAll(inventory(siteDir.toPath(), collectionDir, true));
    }

    public List<T> getAll(String groupName) {
        G group = getResourceGroup(groupName);
        if (group != null) {
            return Collections.unmodifiableList(group.getAll());
        }
        return Collections.emptyList();
    }

    public G getResourceGroup(String groupName) {
        for (G candidate : this.resourceGroups) {
            if (candidate.getName().equals(groupName)) {
                return candidate;
            }
        }
        return null;
    }

    public List<G> getGroups() {
        return new ArrayList<>(resourceGroups);
    }

    public String getPath() {
        return collectionPath;
    }

    public String getMountPoint() {
        return this.mountPoint;
    }

    public abstract G createAndAddNewGroup(String groupName);

    public abstract FileFilter getResourceFilter();

    protected List<G> inventory(Path sitePath, File groupDir, boolean subdirsOnly) {
        LinkedList<G> groups = new LinkedList<>();

        // Depth-first: when there are subdirectories present in the directory,
        // visit them first
        File[] subdirectories = groupDir.listFiles(DirectoryFilter.INSTANCE);
        for (File subDirectory : subdirectories) {
            // take inventory of resourcegroup in subdirectory and merge with
            // our
            // resource group
            groups.addAll(inventory(sitePath, subDirectory, false));
        }

        if (!subdirsOnly) {
            // take inventory of the resources in the given directory
            G newGroup = createAndAddNewGroup(groupDir.getName());
            for (File resourcePath : groupDir.listFiles(getResourceFilter())) {
                newGroup.createAndAddNewResource(resourcePath);
            }
        }

        return groups;
    }
}
