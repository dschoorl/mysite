package info.rsdev.mysite.writing.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Version} represents the formal version of a document, as it existed in time. The lifecycle of a version has a 
 * number of consecutive stages; it is not possible for a Version to go back to a stage that it has already gone through. The
 * stages are: draft, published and withdrawn. Only versions that are in draft can be edited. 
 * in order for it to be active (see: {@link Document#getPublishedVersion()}. Only one version can be active at a time.
 * The document 
 */
public class Version {
    
    /**
     * The internal name of the version. This reference is targeted at the authors of the document. It must be unique 
     * within the document
     */
    private final String versionName;
    
    /**
     * The name of the version that was the starting point of this version.
     */
    private final String basedOnVersion;
    
    private String summaryOfChanges = null;
    
    /**
     * The title of the version. This is targeted at the readers of the document. It does not need to be unique.
     */
    private String title = null;
    
    private String teaser = null;
    
    private String summary = null;
    
    private Date dateFinished = null;
    
    private Date datePublished = null;
    
    private Date dateWithdrawn = null;
    
    /**
     * The name of the author or authors of this version. 
     */
    private String author = null;
    
    /**
     * E-mail address of the author to foreward {@link Comment} and {@link Feedback} to. May be a comma separated list to provide
     * more than one e-mail address.
     */
    private String authorEmailAddress = null;
    
    private ArrayList<Part> preface = null;
    
    /**
     * The content of this version, consisting of a sequence of {@link Part} instances. These could include preface and
     * epilogue parts, however, at present these are not acknowledged as parts that should be treated specifically.
     */
    private ArrayList<Part> content = null;
    
    private ArrayList<Part> epilogue = null;
    
    private Map<String, String> otherProperties = null;
    
    protected Version(String versionName) {
        this.versionName = versionName;
        this.basedOnVersion = null;
        this.preface = new ArrayList<>();
        this.content = new ArrayList<>();
        this.epilogue = new ArrayList<>();
        this.otherProperties = new HashMap<>();
    }
    
    protected Version(Version original, String newVersionName) {
        this.versionName = newVersionName;
        this.basedOnVersion = original.getVersionName();
        this.author = original.author;
        this.authorEmailAddress = original.authorEmailAddress;
        this.content = new ArrayList<>(original.content.size());
        for (Part page: original.content) {
            this.content.add(new Part(page));
        }
        this.dateFinished = null;
        this.datePublished = null;
        this.dateWithdrawn = null;
        this.epilogue = new ArrayList<>(original.epilogue.size());
        for (Part page: original.epilogue) {
            this.epilogue.add(new Part(page));
        }
        this.otherProperties = new HashMap<>(original.otherProperties);
        this.preface = new ArrayList<>(original.preface.size());
        for (Part page: original.preface) {
            this.preface.add(new Part(page));
        }
        this.summary = original.summary;
        this.summaryOfChanges = null;   //specific for this version
        this.title = original.title;
    }
    
    public String getVersionName() {
        return this.versionName;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public Version setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public Version setAuthorEmailAddress(String emailAddress) {
        this.authorEmailAddress = emailAddress;
        return this;
    }
    
    public String getAuthorEmailAddress() {
        return this.authorEmailAddress;
    }
    
    public boolean isPublished() {
        return (datePublished != null) && (dateWithdrawn == null);
    }
    
    protected Version publish() {
        if (!isPublished()) {
            this.datePublished = new Date();
            this.dateWithdrawn = null;
        }
        return this;
    }
    
    protected Version withdraw() {
        if (isPublished()) {
            this.dateWithdrawn = new Date();
        }
        return this;
    }
    
    public String getSummaryOfChanges() {
        return summaryOfChanges;
    }

    public Version setSummaryOfChanges(String summaryOfChanges) {
        this.summaryOfChanges = summaryOfChanges;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Version setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public Version setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public String getTeaser() {
        return teaser;
    }

    public Version setTeaser(String teaser) {
        this.teaser = teaser;
        return this;
    }

    public Date getDateFinished() {
        return dateFinished==null?null:(Date)dateFinished.clone();
    }

    public Version setDateFinished(Date dateFinished) {
        if (dateFinished != null) {
            this.dateFinished = (Date)dateFinished.clone();
        } else {
            this.dateFinished = null;
        }
        return this;
    }

    public List<Part> getPreface() {
        return Collections.unmodifiableList(preface);
    }
    
    public Part newPrefacePart() {
        return appendPart(this.preface);
    }
    
    public Part newPrefacePartAt(int index) {
        return newPartAt(this.preface, index);
    }
    
    public List<Part> getContent() {
        return Collections.unmodifiableList(content);
    }

    public Part newContentPart() {
        return appendPart(this.content);
    }
    
    public Part newContentPartAt(int index) {
        return newPartAt(this.content, index);
    }
    
    public List<Part> getEpilogue() {
        return Collections.unmodifiableList(epilogue);
    }
    
    public Part newEpiloguePart() {
        return appendPart(this.epilogue);
    }
    
    public Part newEpiloguePartAt(int index) {
        return newPartAt(this.epilogue, index);
    }
    
    public Map<String, String> getOtherProperties() {
        return Collections.unmodifiableMap(otherProperties);
    }

    public Date getDatePublished() {
        return datePublished==null?null:(Date)datePublished.clone();
    }

    public String getBasedOnVersion() {
        return basedOnVersion;
    }
    
    private Part appendPart(List<Part> parts) {
        Part newPart = new Part();
        parts.add(newPart);
        return newPart;
    }
    
    private Part newPartAt(List<Part> parts, int index) {
        Part newPart = new Part();
        parts.add(index, newPart);
        return newPart;
    }

}
