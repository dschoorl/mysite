package info.rsdev.mysite.stats.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
//import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;
import info.rsdev.mysite.util.ServletUtils;

/**
 * This class is responsible to preprocess the log entries, meaning: complete
 * the line when necessary related by adding E.g. country, os type and browser
 * type and maybe other derived information, depending on the version of the log
 * entry.
 */
public class AccessLogReport {

    /**
     * Cache the queries for UserAgent strings to Bot/crawler agents. The value of
     * the entry is a static string 'Crawler'
     */
    @SuppressWarnings("unused")
    private ConcurrentHashMap<String, String> crawlerUserAgents = null;

    private final String targetWebsite;

    /**
     * The ip numbers to ignore when they appear in the logfile. Crawlers are
     * ignored automatically (a crawler is identified by their User-Agent string.
     * The list is read from the module configuration file.
     */
    private Set<String> ipHashesToIgnore = Set.of(ServletUtils.asMd5("87.212.128.27"),
            ServletUtils.asMd5("143.176.159.198")/* , ServletUtils.asMd5("127.0.0.1") */); // TODO: configure in properties file

    private final Map<String, VisitorsByMonth> visitorsByMonth = new HashMap<>();

    private Set<String> browserUserAgents = new HashSet<>();

    private static final String[] months = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus",
            "september", "oktober", "november", "december" };

    public AccessLogReport(String targetSite, ConcurrentHashMap<String, String> crawlerUserAgents) {
        this.crawlerUserAgents = crawlerUserAgents;
        this.targetWebsite = targetSite;
    }

    public void process(AccessLogEntry logEntry) {
        if (mustIgnore(logEntry.getRequesterIpHash()) || isCrawler(logEntry) || isNotTargetSite(logEntry)) {
            return;
        }
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
        return this.ipHashesToIgnore.contains(ipAddress);
    }

    public VisitorsByMonth getLatestMonth() {
        if (visitorsByMonth.isEmpty()) {
            return null;
        }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);
        return visitors.get(0);
    }

    public List<VisitorsByMonth> getOlderMonths() {
        if (visitorsByMonth.size() <= 1) {
            return null;
        }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);
        visitors.remove(0);
        return visitors;
    }

    public List<VisitorsAndPageViews<String>> getVisitorsByCountry() {
        Map<String, VisitorsAndPageViews<String>> aggregate = new HashMap<>();
        for (VisitorsByMonth monthly : visitorsByMonth.values()) {
            Collection<VisitorsAndPageViews<String>> byCountry = monthly.getByCountry();
            for (VisitorsAndPageViews<String> entry : byCountry) {
                if (!aggregate.containsKey(entry.getGroupedBy())) {
                    aggregate.put(entry.getGroupedBy(), entry);
                } else {
                    VisitorsAndPageViews<String> aggregated = aggregate.get(entry.getGroupedBy());
                    aggregated = aggregated.combine(entry);
                    aggregate.put(entry.getGroupedBy(), aggregated);
                }
            }
        }
        ArrayList<VisitorsAndPageViews<String>> totalsByCountry = new ArrayList<>(aggregate.values());
        Collections.sort(totalsByCountry, SortOnPageViews.INSTANCE);
        return totalsByCountry;
    }

    public List<VisitorsAndPageViews<String>> getVisitorsByCountryLatestMonth() {
        if (visitorsByMonth.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);

        List<VisitorsAndPageViews<String>> latestMonth = new ArrayList<>(visitors.get(0).getByCountry());
        Collections.sort(latestMonth, SortOnPageViews.INSTANCE);
        return latestMonth;
    }

    public String getTitle() {
        VisitorsByMonth latestMonth = getLatestMonth();
        return String.format(
                String.format("%s, %s %d", latestMonth.getWebsite(), months[latestMonth.getMonth()], latestMonth.getYear()));
    }
}
