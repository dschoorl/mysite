package info.rsdev.mysite.writing.domain;

import java.util.ArrayList;

public class Page {
    
    private String text = null;
    
    private ArrayList<Comment> comments = null;
    
    private int wordCount = 0;
    
    public int getWordCount() {
        return wordCount;
    }

    public Page() {
        comments = new ArrayList<>();
    }
    
    protected Page(Page original) {
       this.text = original.text;
       this.wordCount = original.wordCount;
       this.comments = new ArrayList<>(original.comments.size());
       for (Comment comment: comments) {
           this.comments.add(new Comment(comment));
       }
    }
    
}
