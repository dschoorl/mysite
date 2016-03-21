package info.rsdev.mysite.writing.domain;

import java.util.ArrayList;
import java.util.Locale;

import info.rsdev.mysite.writing.exceptions.InconsistentDocumentException;

/**
 * This class represents a single text document in a specific language and incorporates version management. 
 */
public class Document {
    
    /**
     * The language in which the public content of the document is written.
     */
    private final Locale language;
    
    /**
     * The name of the document known to the application. It is a technical name and not a functional name, E.g. it is not the 
     * title. It must be unique within the mountpoint of the website, so that it can be used in an URL (after proper encoding). 
     * Enforcement of this constraint is currently not implemented.
     */
    private final String technicalName;
    
    /**
     * One or more versions of the document that exist
     */
    private ArrayList<Version> versions = new ArrayList<>();

    public Document(Locale language, String technicalName) {
        this.technicalName = technicalName;
        this.language = language;
    }
    
    public String getTechnicalName() {
        return this.technicalName;
    }
    
    public Locale getLanguage() {
        return this.language;
    }
    
    public Document publishVersion(String versionName) {
        //TODO: consider if we want to call publish on the Version itself instead; but then there must be a bidirectional relationship
        if (versionName == null) {
            throw new NullPointerException("Name of the version to publish cannot be null");
        }
        boolean processed = false;
        for (Version version: this.versions) {
            if (version.getVersionName().equals(versionName)) {
                version.publish();
                processed = true;
            } else if (version.isPublished()) {
                version.withdraw();
            }
        }
        
        if (!processed) {
            throw new InconsistentDocumentException(String.format("Cannot publish version %s because it is not part of document %s",
                    versionName, this.technicalName));
        }
        return this;
    }
    
    /**
     * Create an empty new {@link Version} of this document with the given identifying name.
     * @param versionName the name of the version
     * @return the new {@link Version} instance
     */
    public Version newVersion(String versionName) {
        checkNewVersionName(versionName);
        Version newVersion = new Version(versionName);
        versions.add(newVersion);
        return newVersion;
    }
    
    public Version newVersion(String basedOnVersion, String versionName) {
        checkNewVersionName(versionName);
        Version base = getVersion(basedOnVersion);
        Version newVersion = new Version(base, versionName);
        versions.add(newVersion);
        return newVersion;
    }
    
    public Version getVersion(String versionName) {
        for (Version version: this.versions) {
            if (version.getVersionName().equals(versionName)) {
                return version;
            }
        }
        return null;
    }
    
    public Version getPublishedVersion() {
        for (Version version: this.versions) {
            if (version.isPublished()) {
                return version;
            }
        }
        return null;
    }
    
    private void checkNewVersionName(String versionName) {
        if ((versionName == null) || versionName.isEmpty()) {
            throw new IllegalArgumentException("The name of a new document version cannot be null or empty");
        }
        for (Version version: this.versions) {
            if (version.getVersionName().equals(versionName)) {
                throw new InconsistentDocumentException(String.format("A version named '%s' already exists within document '%s'",
                        versionName, this.technicalName));
            }
        }
    }
    
}
