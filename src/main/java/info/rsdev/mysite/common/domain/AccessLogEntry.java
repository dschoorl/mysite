package info.rsdev.mysite.common.domain;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.ModuleConfig;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class AccessLogEntry {
    
    private static final String EMPTY = "";
    
    private static final String LOG_VERSION = "v1";
    
    private Date timestampRequestReceived = new Date();
    
    private int durationInMs = 0;
    
    private String ipRequester = EMPTY;
    
    private String countryRequester = EMPTY;    //derived from ipRequester
    
    /**
     * The request method, like GET or POST
     */
    private String httpMethod = EMPTY;
    
    private String serverHostname = EMPTY;
    
    private String website = EMPTY;
    
    /**
     * The path relative to the mountpoint that was requested, including query parameters
     */
    private String path = EMPTY;
    
    private int statusCode = -1;
    
    private String sessionId = EMPTY;
    
    private String mountpoint = EMPTY;
    
    private String templateName = EMPTY;
    
    private String contentId = EMPTY;
    
    private String userAgentString = EMPTY;
    
    private String osVersion = EMPTY;  //derived from userAgentString
    
    private String browserVersion = EMPTY; //derived from userAgentString
    
    public AccessLogEntry feedModuleConfig(ModuleConfig config) {
        mountpoint = config.getMountPoint();
        templateName = config.getString(DefaultConfigKeys.TEMPLATE_NAME_KEY);
        website = config.getString(DefaultConfigKeys.SITENAME_KEY);
        return this;
    }
    
    public AccessLogEntry feedRequest(HttpServletRequest request) {
        httpMethod = request.getMethod();
        ipRequester = request.getRemoteAddr();
        sessionId = request.getSession().getId();
        serverHostname = request.getServerName();
        userAgentString = request.getHeader("User-Agent");
        if (request.getRequestURI() != null) {
            path = request.getRequestURI();
        }
        
        if (request.getQueryString() != null) {
            path = path.concat("?").concat(request.getQueryString());   //URL encoded
        }
        return this;
    }
    
    public AccessLogEntry markFinished(String contentId, int statusCode) {
        this.statusCode = statusCode;
        this.durationInMs = (int)(System.currentTimeMillis() - this.timestampRequestReceived.getTime());
        this.contentId = contentId;
        return this;
    }
    
    private String quote(String quotable) {
        if ((quotable == null) || quotable.isEmpty()) {
            return quotable;
        }
        return "\"".concat(quotable).concat("\"");
    }
    
    @Override
    public String toString() {
        return String.format("%s,%tF,%tT,%d,%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s",
                quote(LOG_VERSION),
                timestampRequestReceived,
                timestampRequestReceived,
                durationInMs,
                quote(ipRequester),
                quote(countryRequester),
                quote(httpMethod),
                quote(serverHostname),
                quote(website),
                quote(path),
                statusCode,
                quote(sessionId),
                quote(mountpoint),
                quote(templateName),
                quote(contentId),
                quote(userAgentString),
                quote(osVersion),
                quote(browserVersion)
                );
    }
}
