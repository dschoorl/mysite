package info.rsdev.mysite.writing.domain;

import java.util.ArrayList;
import java.util.Date;

public class Feedback {
    
    private String browserFingerprint = null;
    
    private String registeredIpAddress = null;
    
    private String readerName = null;
    
    private Date dateCommented = null;
    
    private String comment = null;
    
    private ArrayList<Opinion> opinions;

    public Feedback() {
        opinions = new ArrayList<>();
    }
    
    protected Feedback(Feedback original) {
        this.browserFingerprint = original.browserFingerprint;
        this.registeredIpAddress = original.registeredIpAddress;
        this.readerName = original.readerName;
        this.dateCommented = original.dateCommented==null?null:(Date)original.dateCommented.clone();
        this.comment = original.comment;
        this.opinions = new ArrayList<>(original.opinions.size());
        for (Opinion opinion: original.opinions) {
            this.opinions.add(new Opinion(opinion));
        }
    }
}
