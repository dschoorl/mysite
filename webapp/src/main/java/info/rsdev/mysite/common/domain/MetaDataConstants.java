package info.rsdev.mysite.common.domain;

/**
 * The names of properties as they may appear in metadata properties files
 */
public abstract class MetaDataConstants {
    
    /**
     * The LocalDate on which the document was created on formated as yyyy-MM-dd
     */
    public static final String CREATED_ON_METAKEY = "createDate";
    
    /**
     * The LocalDate on which the document was last modified formated as yyyy-MM-dd
     */
    public static final String LAST_MODIFIED_ON_METAKEY = "lastModifiedDate";
    
    public static final String AUTHOR_METAKEY = "author";
    
    public static final String TEASER_METAKEY = "teaser";
    
    private MetaDataConstants() {
        //Do not instantiate this collection of constants
    }

}
