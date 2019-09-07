package info.rsdev.mysite.common.domain.resources;

import java.util.List;

public interface ResourceCollection<T extends PathResource> {

    List<? extends ResourceGroup<T>> getGroups();
}
