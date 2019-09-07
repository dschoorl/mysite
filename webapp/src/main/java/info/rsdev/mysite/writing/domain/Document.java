package info.rsdev.mysite.writing.domain;

import java.util.Locale;

/**
 * This class represents a single text document in a specific language.
 */
public class Document {

    /**
     * The language in which the public content of the document is written.
     */
    private final Locale language;

    /**
     * The name of the document known to the application. It is a technical name
     * and not a functional name, E.g. it is not the title. It must be unique
     * within the mountpoint of the website, so that it can be used in an URL
     * (after proper encoding). Enforcement of this constraint is currently not
     * implemented.
     */
    private final String technicalName;

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

    public String getTitle() {
        return this.getTechnicalName(); //TODO: do some real implementation
    }

    public String getContent() {
        return null;    //TODO: do some real implementation
    }

}
