package info.rsdev.mysite.writing.domain;

public class Comment extends Feedback {
    
    private String textCommentedOn = null;
    
    public Comment() {}
    
    protected Comment(Comment original) {
        super(original);
        this.textCommentedOn = original.textCommentedOn;
    }
}
