package info.rsdev.mysite.gallery.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import info.rsdev.mysite.common.domain.resources.ResourceGroup;

public class ImageGroup implements ResourceGroup<DefaultImage>, Comparable<ImageGroup> {

    private final ImageCollection collection;

    private final String groupName;

    private SortedSet<DefaultImage> images = new TreeSet<DefaultImage>();

    public ImageGroup(ImageCollection collection, String name) {
        this.collection = collection;
        this.groupName = name;
    }

    public String getName() {
        return groupName;
    }

    public ImageCollection getCollection() {
        return this.collection;
    }

    public List<DefaultImage> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.images));
    }

    // public void addImages(Collection<DefaultImage> newImages) {
    // if (newImages != null) {
    // this.images = merge(this.images, newImages);
    // }
    // }

    @Override
    public DefaultImage createAndAddNewResource(File resourcePath) {
        DefaultImage newImage = new DefaultImage(this, resourcePath);
        this.images.add(newImage);
        return newImage;
    }

    @Override
    public int compareTo(ImageGroup o) {
        if (o == null) {
            return -1;
        }
        return this.groupName.compareTo(o.groupName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collection == null) ? 0 : collection.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ImageGroup other = (ImageGroup) obj;
        if (collection == null) {
            if (other.collection != null) return false;
        } else if (!collection.equals(other.collection)) return false;
        if (groupName == null) {
            if (other.groupName != null) return false;
        } else if (!groupName.equals(other.groupName)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("ImageGroup[collection=%s, name=%s, size=%d]", collection.getPath(), groupName, images.size());
    }

}
