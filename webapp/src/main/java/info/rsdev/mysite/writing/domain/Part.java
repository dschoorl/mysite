package info.rsdev.mysite.writing.domain;

import java.util.ArrayList;

public class Part {
    
    private String title = null;
    
    private String text = null;
    
    private ArrayList<Comment> comments = null;
    
    private int wordCount = 0;
    
    private boolean appendPageBreak = false;
    
    public int getWordCount() {
        return wordCount;
    }

    public Part() {
        comments = new ArrayList<>();
    }
    
    protected Part(Part original) {
        this.title = original.title;
        this.text = original.text;
        this.wordCount = original.wordCount;
        this.comments = new ArrayList<>(original.comments.size());
        for (Comment comment: comments) {
            this.comments.add(new Comment(comment));
        }
    }
    
    public Part setText(String newContent) {
        this.text = newContent;
        comments.clear(); //invalidate all comments
        return this;
    }
    
    public String getText()  {
        return this.text;
    }
    
    public Part setTitle(String newTitle) {
        this.title = newTitle;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public Part setPageBreak(boolean appendPageBreak) {
        this.appendPageBreak = appendPageBreak;
        return this;
    }
    
    public boolean isAppendPageBreak() {
        return this.appendPageBreak;
    }
    
}
