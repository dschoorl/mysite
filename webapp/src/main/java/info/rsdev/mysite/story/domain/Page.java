package info.rsdev.mysite.story.domain;

import java.util.List;

public class Page {
    
    private String text = null;
    
    private List<Comment> comments = null;
    
    private int wordCount = 0;
    
    public int getWordCount() {
        return wordCount;
    }

}
