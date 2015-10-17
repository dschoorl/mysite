package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    @SuppressWarnings("unused")
    private ConcurrentHashMap<String, String> crawlerUserAgents = null;
    
    private final String targetWebsite;

    /**
     * Cache the queries for ip2Country mapping
     */
    private Map<String, Locale> ip2Country = null;
    
    /**
     * The ip numbers to ignore when they appear in the logfile. Crawlers are ignored automatically (a crawler is identified by
     * their User-Agent string. The list is read from the module configuration file.
     */
    private Set<String> ipsToIgnore = new HashSet<>(Arrays.asList("87.212.128.27", "127.0.0.1"));    //TODO: configure in properties file
    
    private final Map<String, VisitorsByMonth> visitorsByMonth = new HashMap<>();
    
    private Set<String> browserUserAgents = new HashSet<>();
    
    private static final String[] months = {"januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", 
        "september", "oktober", "november", "december"};
    
    public AccessLogReport(String targetSite, ConcurrentHashMap<String, Locale> ip2Country, ConcurrentHashMap<String, String> crawlerUserAgents) {
        this.ip2Country = ip2Country;
        this.crawlerUserAgents = crawlerUserAgents;
        this.targetWebsite = targetSite;
    }

    public void process(AccessLogEntry logEntry) {
        if (mustIgnore(logEntry.getIpRequester()) || isCrawler(logEntry) || isNotTargetSite(logEntry)) { return; }
        checkAndEnrichDerivedInformation(logEntry);
        String mapKey = String.format("%4d-%2d-%s", logEntry.getYear(), logEntry.getMonth(), logEntry.getWebsite());
        VisitorsByMonth monthStats = visitorsByMonth.get(mapKey);
        if (monthStats == null) {
            monthStats = new VisitorsByMonth(logEntry.getWebsite(), logEntry.getMonth(), logEntry.getYear());
            visitorsByMonth.put(mapKey, monthStats);
        }
        monthStats.process(logEntry);
    }
    
    private boolean isNotTargetSite(AccessLogEntry logEntry) {
        return !targetWebsite.equalsIgnoreCase(logEntry.getWebsite());
    }

    private boolean isCrawler(AccessLogEntry logEntry) {
        String uas = logEntry.getUserAgentString();
        boolean isBrowser = uas.contains("Firefox") || uas.contains("Gecko") || uas.contains("MSIE");
        if (isBrowser) {
            browserUserAgents.add(uas);
        }
        return !isBrowser;
    }
    
    public List<String> getBrowserAgentStrings() {
        ArrayList<String> agents = new ArrayList<>(this.browserUserAgents);
        Collections.sort(agents);
        return agents;
    }
    
    private boolean mustIgnore(String ipAddress) {
        return this.ipsToIgnore.contains(ipAddress);
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
        
        long startTime = System.currentTimeMillis();
        
        Locale country = null;
//        String host = "http://freegeoip.net";
        String host = "http://api.wipmania.com";
        try {
            StringBuilder sb = new StringBuilder();
            URL wipmaniaUrl = new URL(String.format("%s/%s", host, ipAddress));
//            URL freeGeoIpUrl = new URL("http://freegeoip.net/");
            URLConnection urlConnection = wipmaniaUrl.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String inputLine = null;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
            }
            if (sb.length() > 0) {
                /* Examples for json format with freegeoip.net:
                 * {"ip":"87.212.128.27","country_code":"NL","country_name":"Netherlands","region_code":"NH","region_name":"North Holland","city":"Amsterdam","zip_code":"1000","time_zone":"Europe/Amsterdam","latitude":52.374,"longitude":4.89,"metro_code":0}
                 * {"ip":"127.0.0.1","country_code":"","country_name":"","region_code":"","region_name":"","city":"","zip_code":"","time_zone":"","latitude":0,"longitude":0,"metro_code":0}
                 * 
                 * Examples for api.wipmania.com:
                 * NL
                 * XX
                 */
                String result = sb.toString();
                if (result.startsWith("{") && result.endsWith("}")) {   //json from freegeoip.net
                    result = result.substring(1, result.length());
                    String[] pairs = result.split(",");
                    assert(pairs.length == 11); //we expect eleven information parts
                    String countryCodePair = pairs[1];
                    String [] keyValue = countryCodePair.split(":");
                    assert(keyValue.length == 2);
                    assert(keyValue[0].contains("country_code"));
                    if ((keyValue[1].length() > 2) && keyValue[1].startsWith("\"") && keyValue[1].endsWith("\\\"")) {
                        country = new Locale(keyValue[1].substring(1, keyValue[1].length()));
                    }
                } else if(result.length() == 2) {
                    country = new Locale("", result.toUpperCase());   //XX for unknown
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Could not resolve country for ip '%s' through %s", ipAddress, host), e);
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("IP %s resolved to %s by %s in %d millies", ipAddress, country, host, System.currentTimeMillis() - startTime));
        }
        return country;
    }
    
    public VisitorsByMonth getLatestMonth() {
        if (visitorsByMonth.isEmpty()) { return null; }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);
        return visitors.get(0);
    }
    
    public List<VisitorsByMonth> getOlderMonths() {
        if (visitorsByMonth.size() <= 1) { return null; }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);
        visitors.remove(0);
        return visitors;
    }
    
    public List<VisitorsAndPageViews<Locale>> getVisitorsByCountry() {
        Map<Locale, VisitorsAndPageViews<Locale>> aggregate = new HashMap<>();
        for (VisitorsByMonth monthly: visitorsByMonth.values()) {
            Collection<VisitorsAndPageViews<Locale>> byCountry = monthly.getByCountry();
            for (VisitorsAndPageViews<Locale> entry: byCountry) {
                if (!aggregate.containsKey(entry.getGroupedBy())) {
                    aggregate.put(entry.getGroupedBy(), entry);
                } else {
                    VisitorsAndPageViews<Locale> aggregated = aggregate.get(entry.getGroupedBy());
                    aggregated = aggregated.combine(entry);
                    aggregate.put(entry.getGroupedBy(), aggregated);
                }
            }
        }
        ArrayList<VisitorsAndPageViews<Locale>> totalsByCountry = new ArrayList<>(aggregate.values());
        Collections.sort(totalsByCountry, SortOnPageViews.INSTANCE);
        return totalsByCountry;
    }
    
    public List<VisitorsAndPageViews<Locale>> getVisitorsByCountryLatestMonth() {
        if (visitorsByMonth.isEmpty()) { return Collections.emptyList(); }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);
        
        List<VisitorsAndPageViews<Locale>> latestMonth = new ArrayList<>(visitors.get(0).getByCountry());
        Collections.sort(latestMonth, SortOnPageViews.INSTANCE);
        return latestMonth;
    }
    
    public String getTitle() {
        VisitorsByMonth latestMonth = getLatestMonth();
        return String.format(String.format("%s, %s %d", latestMonth.getWebsite(), months[latestMonth.getMonth()], latestMonth.getYear()));
    }
}
