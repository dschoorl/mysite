package info.rsdev.mysite.gallery.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ImageGroup implements Comparable<ImageGroup> {
    
    private final ImageCollection collection;
    
    private final String groupName;
    
    private List<Image> images = new LinkedList<>();
    
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
    
    public List<Image> getImages() {
        return this.images;
    }
    
    public void addImages(Collection<Image> newImages) {
        if (newImages != null) {
            this.images = merge(this.images, newImages);
        }
    }

    public ImageGroup merge(ImageGroup other) {
        if (other != null) {
            if (!other.equals(this)) {
                throw new IllegalArgumentException(String.format("Cannot merge incompatible ImageGroups: %s vs. %s",
                        this, other));
            }
            this.images = merge(this.images, other.images);
        }
        return this;
    }
    
    private List<Image> merge(Collection<Image> master, Collection<Image> other) {
        Set<Image> set = new HashSet<>(master.size() + other.size());
        set.addAll(master);
        set.addAll(other);

        List<Image> result = new ArrayList<>(set);
        Collections.sort(result);
        return result;
    }
    
    @Override
    public int compareTo(ImageGroup o) {
        if (o == null) { return -1; }
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
