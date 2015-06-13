package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.util.HashSet;
import java.util.Set;

public class VisitorsAndPageViews {
    
    private int newVisitors = 0;
    
    private int recurrentVisitors = 0;
    
    private int visits = 0;
    
    private int pageViews = 0;
    
    /**
     * The number of different content items that a unique user has been watching during a session. Duplicate counts of the same
     * web resource is prevented as much as reasonably possible.
     */
    private int uniquePageViews = 0;
    
    private Set<String> sessionIds = new HashSet<>();
    
    private Set<String> sessionAndContentIds = new HashSet<>();
    
    public VisitorsAndPageViews() {}
    
    public VisitorsAndPageViews(VisitorsAndPageViews first, VisitorsAndPageViews second) {
        this.newVisitors = first.newVisitors + second.newVisitors;
        this.recurrentVisitors = first.recurrentVisitors + second.recurrentVisitors;
        this.visits = first.visits + second.visits;
        this.pageViews = first.pageViews + second.pageViews;
        this.uniquePageViews = first.uniquePageViews + second.uniquePageViews;
    }

    public void process(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        if (!sessionIds.contains(logEntry.getSessionId()) && isPageView(logEntry)) {
            visits++;
            String ipAddress = logEntry.getIpRequester();
            if (previouslyVisitedFrom.contains(ipAddress)) {
                this.recurrentVisitors++;
            } else {
                this.newVisitors++;
                previouslyVisitedFrom.add(ipAddress);
            }
            sessionIds.add(logEntry.getSessionId());
        }
        if (isPageView(logEntry)) {
            pageViews++;
            String key = String.format("%s-%s", logEntry.getSessionId(), logEntry.getContentId());
            if (!sessionAndContentIds.contains(key)) {
                sessionAndContentIds.add(key);
                uniquePageViews++;
            }
        }
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

    public VisitorsAndPageViews combine(VisitorsAndPageViews other) {
        return new VisitorsAndPageViews(this, other);
    }
    
}
