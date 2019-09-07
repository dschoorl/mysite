package info.rsdev.mysite.common.domain.resources;

import java.io.File;
import java.util.List;

public interface ResourceGroup<T extends PathResource> {

    String getName();

    List<T> getAll();

    ResourceCollection<T> getCollection();

    T createAndAddNewResource(File resourcePath);
}
