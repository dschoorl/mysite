package info.rsdev.mysite.story.domain;

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
    
}
