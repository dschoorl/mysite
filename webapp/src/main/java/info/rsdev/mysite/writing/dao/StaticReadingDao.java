package info.rsdev.mysite.writing.dao;

import java.util.Locale;

import javax.inject.Singleton;

import info.rsdev.mysite.writing.domain.Document;
import info.rsdev.mysite.writing.domain.Version;

/**
 * This implementation is for mockup purposes, so we can work on the domain / UI before the data layer is finished and filled with
 * documents.
 */
@Singleton
public class StaticReadingDao implements IReadingDao {

    @Override
    public Document getDocumentByName(String name) {
        if (!"Er was eens...".equals(name)) {
            return null;
        }
        
        Document document = new Document(new Locale("nl"), "Er was eens...");
        Version onlyVersion = document.newVersion("original");
        onlyVersion.setAuthor("David Es").setAuthorEmailAddress("dave@cyber-d.com");
        document.publishVersion(onlyVersion.getVersionName());
        return document;
    }
    
}
