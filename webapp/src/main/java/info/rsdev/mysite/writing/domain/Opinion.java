package info.rsdev.mysite.writing.domain;

import java.util.Date;

/**
 * Expose what someone thinks of someone's {@link Comment} or Feedback
 */
public class Opinion {
    
    public enum OpinionType {
        
        /**
         * The reader agrees with the commenter. Instead of supplying the same comment, he can simply agree.
         */
        AGREE,
        
        /**
         * The reader does NOT agree with the commenter. However, he is too lazy to write his own comment and simply disagrees.
         */
        DISAGREE,
        
        /**
         * The response of the commenter is elaborate. The reader agrees with some and disagrees with other statements made
         * by the responder. He want to express that he has mixed emotions about the commenter's reaction.
         */
        MIXED_EMOTIONS
    }
    
    private OpinionType opinionType = null;
    
    private String browserFingerprint = null;
    
    private String registeredIpAddress = null;
    
    private Date dateExpressed = null;
    
    public Opinion() {}
    
    protected Opinion(Opinion original) {
        this.browserFingerprint = original.browserFingerprint;
        this.dateExpressed = original.dateExpressed==null?null:(Date)original.dateExpressed.clone();
        this.opinionType = original.opinionType;
        this.registeredIpAddress = original.registeredIpAddress;
    }
}
