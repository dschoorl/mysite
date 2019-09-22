package info.rsdev.mysite.text.domain;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Locale;

import info.rsdev.mysite.text.asciidoc.AsciidocConverter;

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

    private final DocumentGroup documentGroup;

    private final String documentPath;

    private final File document;

    public DefaultDocument(DocumentGroup group, File document) {
        this.document = document;
        this.documentGroup = group;
        this.documentName = extractName(document);

        // calculate the imagePath relative to the collectionPath
        String collectionPath = this.documentGroup.getCollection().getPath();
        String documentPath = document.getAbsolutePath();
        int index = documentPath.indexOf(collectionPath);
        this.documentPath = documentPath.substring(index);
    }

    private String extractName(File document) {
        String name = document.getName();
        int index = name.lastIndexOf(EXTENSION_SEPARATOR);
        if (index >= 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    private String extractExtension(File document) {
        String name = document.getName();
        int index = name.lastIndexOf(EXTENSION_SEPARATOR);
        if ((index >= 0) && (name.length() - index > 1)) {
            return name.substring(index + 1);
        }
        return name;
    }

    @Override
    public String getPath() {
        return this.documentPath;
    }

    public String getUrl() {
        try {
            return String.format("%s/%s",
                    URLEncoder.encode(documentGroup.getName(), "UTF-8"),
                    URLEncoder.encode(getTitle(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTechnicalName() {
        return this.documentName;
    }

    public Locale getLanguage() {
        return this.language;
    }

    public String getTitle() {
        return this.getTechnicalName();
    }

    public String getContent() {
        if ("adoc".equalsIgnoreCase(extractExtension(document))) {
            return AsciidocConverter.INSTANCE.convertDocument(document);
        } else {
            throw new UnsupportedOperationException("Currently only asciidoctor files are supported");
        }
    }

    public String getSummary() {
        try {
            String rawText = new String(Files.readAllBytes(document.toPath()));
            if (rawText.length() < 300) {
                return rawText;
            } else {
                return rawText.substring(0, 300).concat("...");
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DocumentGroup getGroup() {
        return this.documentGroup;
    }

    @Override
    public int compareTo(DefaultDocument o) {
        if (o == null) {
            return -1;
        }
        return this.documentName.compareTo(o.documentName);
    }

}
