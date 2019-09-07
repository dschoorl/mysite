package info.rsdev.mysite.text.domain;

import java.io.File;
import java.util.Locale;

/**
 * This class represents a single text document in a specific language.
 */
public class DefaultDocument implements Document, Comparable<DefaultDocument> {

    public static final String METADATA_INDICATOR = "_meta";
    
    private static final String EXTENSION_SEPARATOR = ".";

    /**
     * The language in which the public content of the document is written.
     */
    private final Locale language = new Locale("nl");

    /**
     * The name of the document known to the application. It is a technical name
     * and not a functional name, E.g. it is not the title. It must be unique
     * within the mountpoint of the website, so that it can be used in an URL
     * (after proper encoding). Enforcement of this constraint is currently not
     * implemented.
     */
    private final String documentName;

    private final DocumentGroup imageGroup;

    private final String documentPath;

    public DefaultDocument(DocumentGroup group, File document) {
        this.imageGroup = group;
        this.documentName = extractName(document);

        // calculate the imagePath relative to the collectionPath
        String collectionPath = this.imageGroup.getCollection().getPath();
        String doumentPath = document.getAbsolutePath();
        int index = doumentPath.indexOf(collectionPath);
        this.documentPath = doumentPath.substring(index);
    }
    
    private String extractName(File document) {
        String name = document.getName();
        int index = name.lastIndexOf(EXTENSION_SEPARATOR);
        if (index >= 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    @Override
    public String getPath() {
        return this.documentPath;
    }

    public String getTechnicalName() {
        return this.documentName;
    }

    public Locale getLanguage() {
        return this.language;
    }

    public String getTitle() {
        return this.getTechnicalName(); // TODO: do some real implementation
    }

    public String getContent() {
        return null; // TODO: do some real implementation
    }

    @Override
    public int compareTo(DefaultDocument o) {
        if (o == null) {
            return -1;
        }
        return this.documentName.compareTo(o.documentName);
    }

}
