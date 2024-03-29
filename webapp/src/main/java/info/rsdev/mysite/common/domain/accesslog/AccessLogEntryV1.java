package info.rsdev.mysite.common.domain.accesslog;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.stats.Ip2CountryService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Version 1 of the AccessLogEntry. The version determines which data will be
 * collected per request.
 */
public class AccessLogEntryV1 implements AccessLogEntry {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogEntryV1.class);

    private static final String EMPTY = "";

    private static final String LOG_VERSION = "v1";

    private static final String IP_UNKNOWN = "unknown";

    static interface V1 {
        public int VERSION_ID = 0;
        public int DATE = 1;
        public int TIME = 2;
        public int DURATION = 3;
        /*
         * TODO: do not log IP but a hash from the IP. This allows us to: (1) simplify
         * AVG compliance and (2) still detect previous visits from same IP
         */
        public int IP_MD5_HASH = 4;
        public int COUNTRY = 5;
        public int METHOD = 6;
        public int SERVER_HOST = 7;
        public int WEBSITE = 8;
        public int PATH = 9;
        public int STATUS_CODE = 10;
        public int SESSION_ID = 11;
        public int MOUNTPOINT = 12;
        public int TEMPLATENAME = 13;
        public int CONTENT_ID = 14;
        public int USER_AGENT_STRING = 15;
        public int OS_TYPE = 16;
        public int BROWSER_TYPE = 17;
    }

    private Calendar timestampRequestReceived = GregorianCalendar.getInstance();

    private int durationInMs = 0;

    /**
     * An MD5 hash of the requester's IP address
     */
    private String requesterIpHash = EMPTY;

    private String countryRequester = null; // looked up in ip2country table

    /**
     * The request method, like GET or POST
     */
    private String httpMethod = EMPTY;

    private String serverHostname = EMPTY;

    private String website = EMPTY;

    /**
     * The path relative to the mountpoint that was requested, including query
     * parameters
     */
    private String path = EMPTY;

    private int statusCode = -1;

    private String sessionId = EMPTY;

    private String mountpoint = EMPTY;

    private String templateName = EMPTY;

    private String contentId = EMPTY;

    private String userAgentString = EMPTY;

    @SuppressWarnings("unused")
    private boolean isCrawler = false; // derived from userAgentString

    private String osVersion = EMPTY; // derived from userAgentString

    private String browserVersion = EMPTY; // derived from userAgentString

    /**
     * Create an empty {@link AccessLogEntryV1} for the purpose of writing a line to
     * the access log file. Only the creation timestamp is set and all text fields
     * are initialized to empty strings The fields will be filled externally from
     * the http request and Module configuration by calling the appropriate methods.
     */
    public AccessLogEntryV1() {
    }

    /**
     * Create a new {@link AccessLogEntryV1} from data that was read from the access
     * log file. It's purpose is to aid in generating statistics.
     * 
     * @param rawLogdata
     */
    public AccessLogEntryV1(SimpleDateFormat dateFormatter, String[] rawLogdata) {
        if (rawLogdata == null) {
            throw new IllegalArgumentException("Logdata cannot be null");
        }
        if ((rawLogdata.length != 18) || !rawLogdata[V1.VERSION_ID].equals(LOG_VERSION)) {
            throw new IllegalArgumentException("logdata does not represent a valid V1 access log entry");
        }

        String date = rawLogdata[V1.DATE];
        String time = rawLogdata[V1.TIME];
        synchronized (dateFormatter) {
            try {
                this.timestampRequestReceived.setTime(dateFormatter.parse(date.concat(" ").concat(time)));
            } catch (ParseException e) {
                throw new RuntimeException("Could not create a valid date", e);
            }
        }

        this.durationInMs = Integer.parseInt(rawLogdata[V1.DURATION]);
        this.requesterIpHash = rawLogdata[V1.IP_MD5_HASH];
        this.countryRequester = rawLogdata[V1.COUNTRY];
        this.httpMethod = rawLogdata[V1.METHOD];
        this.serverHostname = rawLogdata[V1.SERVER_HOST];
        this.website = rawLogdata[V1.WEBSITE];
        this.path = rawLogdata[V1.PATH];
        this.statusCode = Integer.parseInt(rawLogdata[V1.STATUS_CODE]);
        this.sessionId = rawLogdata[V1.SESSION_ID];
        this.mountpoint = rawLogdata[V1.MOUNTPOINT];
        this.templateName = rawLogdata[V1.TEMPLATENAME];
        this.contentId = rawLogdata[V1.CONTENT_ID];
        this.userAgentString = rawLogdata[V1.USER_AGENT_STRING];
        this.osVersion = rawLogdata[V1.OS_TYPE];
        this.browserVersion = rawLogdata[V1.BROWSER_TYPE];
    }

    public AccessLogEntryV1 feedModuleConfig(ModuleConfig config) {
        mountpoint = config.getMountPoint();
        // templateName = config.getString(DefaultConfigKeys.TEMPLATE_NAME_KEY);
        website = config.getString(DefaultConfigKeys.SITENAME_KEY);
        return this;
    }

    @SuppressWarnings("deprecation")
    public AccessLogEntryV1 feedRequest(HttpServletRequest request, Ip2CountryService ip2CountryLookup) {
        httpMethod = request.getMethod();
        sessionId = request.getSession().getId();
        String ipAddress = getClientAddress(request);
        if (ipAddress == null) {
            logger.info("No client ip address resolved from request");
        }

        // countryCode as salt -- I know, still easy to generate rainbow table and find out
        // the ip address behind the hash code, but it is a slightly bigger obstacle
        // than no salt. And I need to detect returning visitors, so... that's why :-(
        countryRequester = ip2CountryLookup.getCountryCode(ipAddress);
        requesterIpHash = Hashing.md5().hashString(countryRequester + ipAddress, StandardCharsets.UTF_8).toString();
        serverHostname = request.getServerName();
        // TODO: resolve os / browser using E.g.
        // http://www.useragentstring.com/pages/api.php
        // TODO: derive isCrawler from User-Agent string
        userAgentString = request.getHeader("User-Agent");
        if (request.getRequestURI() != null) {
            path = request.getRequestURI();
        }

        if (request.getQueryString() != null) {
            path = path.concat("?").concat(request.getQueryString()); // URL
                                                                      // encoded
        }
        return this;
    }
    
    private String getClientAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // in case no proxy server is used
        }
        return ip;
    }

    public AccessLogEntryV1 markFinished(ModuleHandlerResult result, int statusCode) {
        this.statusCode = statusCode;
        this.durationInMs = (int) (System.currentTimeMillis() - this.timestampRequestReceived.getTimeInMillis());
        this.statusCode = statusCode;
        if (result != null) {
            this.contentId = result.getContentId();
            this.templateName = result.getTemplateName();
        }
        return this;
    }

    protected String quote(String quotable) {
        if ((quotable == null) || quotable.isEmpty()) {
            return EMPTY;
        }
        return "\"".concat(quotable.replaceAll("\"", "\"\"")).concat("\""); // escape
                                                                            // double
                                                                            // quotes
                                                                            // within
                                                                            // the
                                                                            // string
                                                                            // value

        // escape every double quote within the string with two double quotes
//        return "\"".concat(quotable.replace("\"", "\"\"")).concat("\"");
    }

    @Override
    public String toString() {
        return String.format("%s,%tF,%tT,%d,%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s", quote(LOG_VERSION),
                timestampRequestReceived, timestampRequestReceived, durationInMs, quote(requesterIpHash), quote(countryRequester),
                quote(httpMethod), quote(serverHostname), quote(website), quote(path), statusCode, quote(sessionId),
                quote(mountpoint), quote(templateName), quote(contentId), quote(userAgentString),
                // EMPTY, //placeholder for boolean value 'isCrawler'
                quote(osVersion), quote(browserVersion));
    }

    @Override
    public String getRequesterIpHash() {
        return this.requesterIpHash;
    }

    @Override
    public int getYear() {
        return this.timestampRequestReceived.get(Calendar.YEAR);
    }

    @Override
    public int getMonth() {
        return this.timestampRequestReceived.get(Calendar.MONTH);
    }

    @Override
    public int getDayOfMonth() {
        return this.timestampRequestReceived.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getHour() {
        return this.timestampRequestReceived.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public String getUserAgentString() {
        return this.userAgentString;
    }

    @Override
    public String getWebsite() {
        return this.website;
    }

    @Override
    public String getVersion() {
        return LOG_VERSION;
    }

    @Override
    public String getCountry() {
        return this.countryRequester;
    }

    @Override
    public Calendar getTimestamp() {
        return this.timestampRequestReceived;
    }

    @Override
    public String getSessionId() {
        if ((this.sessionId != null) && !this.sessionId.isEmpty()) {
            return this.sessionId;
        }
        return null;
    }

    @Override
    public String getContentId() {
        if ((this.contentId != null) && !this.contentId.isEmpty()) {
            return this.contentId;
        }
        return null;
    }

    @Override
    public String getTemplateName() {
        if ((this.templateName != null) && !this.templateName.isEmpty()) {
            return this.templateName;
        }
        return null;
    }

    @Override
    public boolean ignoreMe() {
        return getContentId() == null;
    }

}
