package info.rsdev.mysite.text.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import info.rsdev.mysite.common.domain.MenuItem;

public class DocumentGroupMenuItem implements MenuItem {

    private static final String UTF8 = "UTF-8";

    private final DocumentGroup documentGroup;

    private boolean active = false;

    public DocumentGroupMenuItem(DocumentGroup documentGroup, boolean isActive) {
        this.documentGroup = documentGroup;
        this.active = isActive;
    }

    public DocumentGroupMenuItem(DocumentGroup documentGroup) {
        this.documentGroup = documentGroup;
    }

    @Override
    public String getTargetUrl() {
        try {
            String targetUrl = documentGroup.getCollection().getMountPoint();
            if (!targetUrl.endsWith("/")) {
                targetUrl= targetUrl.concat("/");
            }
            return targetUrl.concat(URLEncoder.encode(documentGroup.getName(), UTF8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCaption() {
        return documentGroup.getName();
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public MenuItem setActive(boolean isActive) {
        this.active = isActive;
        return this;
    }

}
