package info.rsdev.mysite.stats.domain;

import java.util.HashSet;
import java.util.Set;

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;

/**
 * This class is responsible to keep track of the number of visitors and page views for a specific group, e.g. a
 * specific day, country or content item. It also keeps track of the number of unique page views, which is the number of
 * different content items that a unique user has been watching during a session. Duplicate counts of the same web
 * resource is prevented as much as reasonably possible.
 * 
 * @param <T> the type of the group by value, e.g. Integer for day of month, String for country or content item
 */
public class VisitorsAndPageViews<T> {

    private int newVisitors = 0;

    private int recurrentVisitors = 0;

    private int visits = 0;

    private int pageViews = 0;

    /**
     * The number of different content items that a unique user has been watching during a session. Duplicate counts of
     * the same web resource is prevented as much as reasonably possible.
     */
    private int uniquePageViews = 0;

    private Set<String> sessionIds = new HashSet<>();

    private Set<String> sessionAndContentIds = new HashSet<>();

    /** 
     * The value by which the visitors and page views are grouped, e.g. day of month, country or content item 
     */
    private T groupBy = null;

    public VisitorsAndPageViews(T groupBy) {
        this.groupBy = groupBy;
    }

    public VisitorsAndPageViews(VisitorsAndPageViews<T> first, VisitorsAndPageViews<T> second) {
        if (!first.groupBy.equals(second.groupBy)) {
            throw new IllegalArgumentException(String.format("Grouped by different values: %s vs. %s",
                    first.groupBy, second.groupBy));
        }
        this.newVisitors = first.newVisitors + second.newVisitors;
        this.recurrentVisitors = first.recurrentVisitors + second.recurrentVisitors;
        this.visits = first.visits + second.visits;
        this.pageViews = first.pageViews + second.pageViews;
        this.uniquePageViews = first.uniquePageViews + second.uniquePageViews;
        this.groupBy = first.groupBy;
    }

    public boolean process(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        boolean isNewVisitor = false;
        if (!sessionIds.contains(logEntry.getSessionId()) && isPageView(logEntry)) {
            visits++;
            sessionIds.add(logEntry.getSessionId());
            String ipAddress = logEntry.getRequesterIpHash();
            if (previouslyVisitedFrom.contains(ipAddress)) {
                this.recurrentVisitors++;
            } else {
                this.newVisitors++;
                isNewVisitor = true;
            }
        }
        if (isPageView(logEntry)) {
            pageViews++;
            String key = String.format("%s-%s", logEntry.getSessionId(), logEntry.getContentId());
            if (!sessionAndContentIds.contains(key)) {
                sessionAndContentIds.add(key);
                uniquePageViews++;
            }
        }
        return isNewVisitor;
    }
    
    /** 
     * @return true if this object contains non-zero statistic values, false otherwise
     */
    public boolean hasData() {
        return visits + pageViews + uniquePageViews + newVisitors > 0;
    }

    private boolean isPageView(AccessLogEntry logEntry) {
        return logEntry.getTemplateName() != null;
    }

    public int getUniquePageViews() {
        return this.uniquePageViews;
    }

    public int getPageViews() {
        return this.pageViews;
    }

    public int getVisits() {
        return this.visits;
    }

    public int getNewVisitors() {
        return this.newVisitors;
    }

    public int getRecurrentVisitors() {
        return this.recurrentVisitors;
    }

    public T getGroupedBy() {
        return this.groupBy;
    }

    public String getGroupedByToString() {
        return this.groupBy.toString();
    }

    public VisitorsAndPageViews<T> combine(VisitorsAndPageViews<T> other) {
        return new VisitorsAndPageViews<T>(this, other);
    }

}
