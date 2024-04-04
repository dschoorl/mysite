package info.rsdev.mysite.text.domain;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class RecentDocumentGroup extends DocumentGroup {

    /**
     * Any document younger than this age in days will be part of the collection of recent documents
     */
    private final int daysCutoffPoint;

    private LocalDate lastUpdated = null;

    public RecentDocumentGroup(DocumentCollection collection, String name, int daysCutoffPoint) {
        super(collection, name, collection.getLanguage());
        this.daysCutoffPoint = daysCutoffPoint;
        collectRecentDocuments();
    }
    
    private void collectRecentDocuments() {
        this.lastUpdated = LocalDate.now().minusDays(daysCutoffPoint);
        for (DocumentGroup group: getCollection().getGroups()) {
            for (DefaultDocument document: group.getAll()) {
                if (document.getDateCreated().isAfter(lastUpdated)) {
                    this.documents.add(document);
                }
            }
        }
    }

    @Override
    public List<DefaultDocument> getAll() {
        if (documents.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate cutOffDate = LocalDate.now().minusDays(daysCutoffPoint);
        if ((lastUpdated == null) || this.lastUpdated.isBefore(cutOffDate)) {
            this.lastUpdated = cutOffDate;
            SortedSet<DefaultDocument> copy = new TreeSet<>();
            documents.forEach(document -> {
                if (document.getDateCreated().isAfter(cutOffDate)) {
                    copy.add(document);
                }
            });
            documents = copy;
        }

        return Collections.unmodifiableList(new ArrayList<>(this.documents));
    }

    @Override
    public DefaultDocument createAndAddNewResource(File resourcePath) {
        throw new UnsupportedOperationException("Cannot create resources in a virtual documentgroup");
    }

}
