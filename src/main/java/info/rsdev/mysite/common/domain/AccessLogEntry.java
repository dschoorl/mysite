package info.rsdev.mysite.common.domain;

import java.util.Calendar;
import java.util.Locale;

public interface AccessLogEntry {
    
    public String getIpRequester();
    
    public int getYear();
    
    public int getMonth();
    
    public int getDayOfMonth();
    
    public int getHour();
    
    public String getUserAgentString();
    
    public String getWebsite();
    
   /**
     * Get the version of this access log entry.
     * @return a version number, like E.g. v1.
     */
    public String getVersion();

    public Locale getCountry();

    public void setCountry(Locale country);

    public Calendar getTimestamp();

    public String getSessionId();

    public String getContentId();

    public String getTemplateName();
    
}
