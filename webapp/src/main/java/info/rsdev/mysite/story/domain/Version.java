package info.rsdev.mysite.story.domain;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Version {
    
    private int versionNumber = -1;
    
    private String versionName = null;
    
    private String title = null;
    
    private String summary = null;
    
    private String summaryOfChanges = null;
    
    private Date dateFinished = null;
    
    private Date datePublished = null;
    
    private String author = null;
    
    private Locale language = null;
    
    private List<Page> content = null;
    
    private Map<String, String> otherProperties = null;
    
}
