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

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;

/**
 * This class is responsible to preprocess the log entries, meaning: complete
 * the line when necessary related by adding E.g. country, os type and browser
 * type and maybe other derived information, depending on the version of the log
 * entry.
 */
public class AccessLogReport {

    private final String targetWebsite;

    private final Map<String, VisitorsByMonth> visitorsByMonth = new HashMap<>();

    private Set<String> browserUserAgents = new HashSet<>();

    private static final String[] months = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus",
            "september", "oktober", "november", "december" };

    public AccessLogReport(String targetSite) {
        this.targetWebsite = targetSite;
    }

    public void process(AccessLogEntry logEntry) {
        if (isNotTargetSite(logEntry) || isCrawler(logEntry.getUserAgentString())) {
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

    /**
     * Best effort to determine if the request is done by a user or by a crawler, based on the User Agent String
     * @param userAgentString
     * @return true if the request is likely done by a crawler, false otherwise
     */
    protected boolean isCrawler(String userAgentString) {
        String uas = userAgentString.toLowerCase();
        boolean couldBeUser = uas.startsWith("mozilla");
        couldBeUser |= uas.startsWith("firefox");
        couldBeUser |= uas.startsWith("blackberry");
        couldBeUser |= uas.startsWith("chrome");
        couldBeUser |= uas.startsWith("opera");
        boolean ohWaitLooksLikeCrawler = couldBeUser && uas.contains("bot");
        ohWaitLooksLikeCrawler |= uas.contains("crawler");
        ohWaitLooksLikeCrawler |= uas.contains("spider");
        ohWaitLooksLikeCrawler |= uas.contains("slurp");
        ohWaitLooksLikeCrawler |= uas.contains("http");
        ohWaitLooksLikeCrawler |= uas.contains("@");
        if (!ohWaitLooksLikeCrawler) {
            browserUserAgents.add(userAgentString);
        }
        return ohWaitLooksLikeCrawler;
    }

    public List<String> getBrowserAgentStrings() {
        ArrayList<String> agents = new ArrayList<>(this.browserUserAgents);
        Collections.sort(agents);
        return agents;
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

    public List<VisitorsAndPageViews<String>> getVisitorsByUrlLatestMonth() {
        if (visitorsByMonth.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<VisitorsByMonth> visitors = new ArrayList<>(visitorsByMonth.values());
        Collections.sort(visitors);

        List<VisitorsAndPageViews<String>> latestMonth = new ArrayList<>(visitors.get(0).getByContent());
        Collections.sort(latestMonth, SortOnPageViews.INSTANCE);
        return latestMonth;
    }

    public String getTitle() {
        VisitorsByMonth latestMonth = getLatestMonth();
        return String.format(
                String.format("%s, %s %d", latestMonth.getWebsite(), months[latestMonth.getMonth()], latestMonth.getYear()));
    }
}
