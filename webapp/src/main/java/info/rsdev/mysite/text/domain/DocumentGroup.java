package info.rsdev.mysite.text.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import info.rsdev.mysite.common.domain.resources.ResourceGroup;

public class DocumentGroup implements ResourceGroup<DefaultDocument> {

    private final DocumentCollection collection;

    private final String groupName;
    
    private final Locale language;

    protected SortedSet<DefaultDocument> documents = new TreeSet<>();

    public DocumentGroup(DocumentCollection collection, String name, Locale language) {
        this.language = Objects.requireNonNull(language);
        this.collection = collection;
        this.groupName = name;
    }

    @Override
    public String getName() {
        return groupName;
    }

    @Override
    public List<DefaultDocument> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.documents));
    }

    @Override
    public DocumentCollection getCollection() {
        return this.collection;
    }

    @Override
    public DefaultDocument createAndAddNewResource(File resourcePath) {
        DefaultDocument newDocument = new DefaultDocument(this, resourcePath, language);
        this.documents.add(newDocument);
        return newDocument;
    }

    public DefaultDocument getDocument(String documentName) {
        for (DefaultDocument candidate : this.documents) {
            if (candidate.getTechnicalName().equalsIgnoreCase(documentName)) {
                return candidate;
            }
        }
        return null;
    }

}
