package info.rsdev.mysite.common.domain.accesslog;

import java.util.Calendar;

public interface AccessLogEntry {
    
    /**
     * Get the MD5 hash for the IP address
     * @return
     */
    public String getRequesterIpHash();
    
    public int getYear();
    
    /**
     * Determine the number of the month, ranging from 0 (January) to 11 (December)
     * @return the (zero-based) number of the month
     */
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
    
    public String getReferer();
    
    public boolean ignoreMe();
    
}
