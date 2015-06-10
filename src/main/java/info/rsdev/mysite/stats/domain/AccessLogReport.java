package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to preprocess the log entries, meaning: complete the line when necessary related by adding E.g. 
 * country, os type and browser type and maybe other derived information, depending on the version of the log entry.
 */
public class AccessLogReport {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessLogReport.class);
    
    /**
     * Cache the queries for UserAgent strings to Bot/crawler agents. The value of the entry is a static string 'Crawler'
     */
    private ConcurrentHashMap<String, String> crawlerUserAgents = null;

    /**
     * Cache the queries for ip2Country mapping
     */
    private Map<String, Locale> ip2Country = null;
    
    /**
     * The ip numbers to ignore when they appear in the logfile. Crawlers are ignored automatically (a crawler is identified by
     * their User-Agent string. The list is read from the module configuration file.
     */
    private Set<String> ipsToIgnore = Collections.emptySet();
    
    private final Map<String, VisitorsByMonth> visitorsByMonth = new HashMap<>();
    
    public AccessLogReport(ConcurrentHashMap<String, Locale> ip2Country, ConcurrentHashMap<String, String> crawlerUserAgents) {
        this.ip2Country = ip2Country;
        this.crawlerUserAgents = crawlerUserAgents;
    }

    public void process(AccessLogEntry logEntry) {
        checkAndEnrichDerivedInformation(logEntry);
        String mapKey = String.format("%4d-%2d-%s", logEntry.getYear(), logEntry.getMonth(), logEntry.getWebsite());
        VisitorsByMonth monthStats = visitorsByMonth.get(mapKey);
        if (monthStats == null) {
            monthStats = new VisitorsByMonth(logEntry.getWebsite(), logEntry.getMonth(), logEntry.getYear());
            visitorsByMonth.put(mapKey, monthStats);
        }
        monthStats.process(logEntry);
    }
    
    
    private void checkAndEnrichDerivedInformation(AccessLogEntry logEntry) {
        //is country set
        if (logEntry.getCountry() == null) {
            String ipAddress = logEntry.getIpRequester();
            if (!ip2Country.containsKey(ipAddress)) {
                Locale country = resolveCountry(ipAddress, ip2Country);
                ip2Country.put(ipAddress, country);
            }
            logEntry.setCountry(ip2Country.get(ipAddress));
        }
        
    }

    public static Locale resolveCountry(String ipAddress, Map<String, Locale> ip2Country) {
        if (ipAddress == null) { return null; }
        
        if ((ip2Country != null) && ip2Country.containsKey(ipAddress)) {
            return ip2Country.get(ipAddress);
        }
        Locale country = null;
        String host = "http://freegeoip.net";
        try {
            StringBuilder sb = new StringBuilder();
            URL freeGeoIpUrl = new URL(String.format("%s/json/%s", host, ipAddress));
//            URL freeGeoIpUrl = new URL("http://freegeoip.net/");
            URLConnection urlConnection = freeGeoIpUrl.openConnection();
//            urlConnection.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String inputLine = null;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
            }
            if (sb.length() > 0) {
                /* Examples:
                 * {"ip":"87.212.128.27","country_code":"NL","country_name":"Netherlands","region_code":"NH","region_name":"North Holland","city":"Amsterdam","zip_code":"1000","time_zone":"Europe/Amsterdam","latitude":52.374,"longitude":4.89,"metro_code":0}
                 * {"ip":"127.0.0.1","country_code":"","country_name":"","region_code":"","region_name":"","city":"","zip_code":"","time_zone":"","latitude":0,"longitude":0,"metro_code":0}
                 */
                String json = sb.toString();
                if (json.startsWith("{") && json.endsWith("}")) {
                    json = json.substring(1, json.length());
                    String[] pairs = json.split(",");
                    assert(pairs.length == 11); //we expect eleven information parts
                    String countryCodePair = pairs[1];
                    String [] keyValue = countryCodePair.split(":");
                    assert(keyValue.length == 2);
                    assert(keyValue[0].contains("country_code"));
                    if ((keyValue[1].length() > 2) && keyValue[1].startsWith("\"") && keyValue[1].endsWith("\\\"")) {
                        country = new Locale(keyValue[1].substring(1, keyValue[1].length()));
                    }
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Could not resolve country for ip '%s' through %s", ipAddress, host), e);
        }
        return country;
    }
}
