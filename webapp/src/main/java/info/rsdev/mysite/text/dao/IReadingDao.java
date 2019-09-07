package info.rsdev.mysite.text.dao;

import info.rsdev.mysite.text.domain.DefaultDocument;

/**
 * This interface defines the interactions with the datalayer for the purpose of presenting the reader the possibility to read 
 * documents and to comment on them or participate in a feedback discussion.
 */
public interface IReadingDao {

    DefaultDocument getDocumentByName(String name);
    
}
