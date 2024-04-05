package info.rsdev.mysite.common.domain.resources;

/**
 * A {@link PathResource} represents a single entity in this website that can be
 * pointed at by a url
 */
public interface PathResource {

    /**
     * Get the path to the resource on this server, relative to the internet
     * hostname
     */
    String getPath();

}
