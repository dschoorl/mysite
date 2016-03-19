package info.rsdev.mysite.writing.dao;

import info.rsdev.mysite.writing.domain.Document;

/**
 * This interface defines the interactions with the datalayer for the purpose of presenting the reader the possibility to read 
 * documents and to comment on them or participate in a feedback discussion.
 */
public interface IReadingDao {

    Document getDocumentByName(String name);
    
}
