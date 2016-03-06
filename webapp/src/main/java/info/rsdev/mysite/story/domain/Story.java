package info.rsdev.mysite.story.domain;

import java.util.List;

public class Story {
    
    /**
     * E-mail address of the author to foreward {@link Comment} and {@link Feedback} to.
     */
    private String authorEmail = null;
    
    private List<Page> preface = null;
    
    private List<Version> versions = null;
    
    private List<Page> epilogue = null;

}
