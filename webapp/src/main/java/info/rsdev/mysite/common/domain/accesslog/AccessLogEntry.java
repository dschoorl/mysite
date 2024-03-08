package info.rsdev.mysite.common.domain.accesslog;

import java.util.Calendar;

public interface AccessLogEntry {
    
    /**
     * Get the MD5 hash for the IP address
     * @return
     */
    public String getRequesterIpHash();
    
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

    public String getCountry();

    public Calendar getTimestamp();

    public String getSessionId();

    public String getContentId();

    public String getTemplateName();
    
    public boolean ignoreMe();
    
}
