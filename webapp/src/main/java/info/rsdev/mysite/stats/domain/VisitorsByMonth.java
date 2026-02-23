package info.rsdev.mysite.stats.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;
import info.rsdev.mysite.stats.Ip2CountryService;

/**
 * This class is responsible to keep track of the number of visitors and page views for a specific month and year, for a
 * specific website. It also keeps track of the number of visitors and page views per day, per country and per content
 * item.
 */
public class VisitorsByMonth implements Comparable<VisitorsByMonth> {

    private final String website;

    private final int month;

    private final int year;

    /**
     * The list contains an element for each day of the month. And each day contains a two column array containing first
     * the number of unique visitors for that day and secondly the pageviews for that day.
     */
    private List<VisitorsAndPageViews<Integer>> visitorsByMonthday = null;

    private Map<String, VisitorsAndPageViews<String>> visitorsByCountry = new HashMap<>();

    private Map<String, VisitorsAndPageViews<String>> visitorsByUrl = new HashMap<>();

    private Map<String, VisitorsAndPageViews<String>> visitorsByReferer = new HashMap<>();

    /**
     * This set contains the unique identifiers of the visitors that have been already counted for this month, to
     * prevent double counting of visitors that have visited the website multiple times during the month. The unique
     * identifier is currently based on the hash of the requester IP address, but in future versions it could be based
     * on a combination of IP address and user agent string, or even on a cookie value if available.
     */
    private Set<String> previouslyVisitedFrom = new HashSet<>();

    public VisitorsByMonth(String website, int month, int year) {
        if (website.isEmpty()) {
            website = null;
        }
        this.website = website;
        this.month = month;
        this.year = year;

        int daysInMonth = new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        visitorsByMonthday = new ArrayList<VisitorsAndPageViews<Integer>>(daysInMonth);
        for (int i = 0; i < daysInMonth; i++) {
            visitorsByMonthday.add(new VisitorsAndPageViews<>(i + 1));
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + month;
        result = prime * result + ((website == null) ? 0 : website.hashCode());
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VisitorsByMonth other = (VisitorsByMonth) obj;
        if (month != other.month) return false;
        if (website == null) {
            if (other.website != null) return false;
        } else if (!website.equals(other.website)) return false;
        if (year != other.year) return false;
        return true;
    }

    @Override
    public int compareTo(VisitorsByMonth o) {
        /*
         * The natural order of instances of this class is (first) alphabetical by website, (second) by year, descending
         * and (third) by month, descending.
         */
        if (o == null) {
            return 1;
        }

        int i = 0;

        // take into account that website could be null or empty
        if ((website == null) && (o.website != null)) {
            return -1;
        }
        if ((o.website == null) && (website != null)) {
            return 1;
        }
        if ((website != null && o.website != null)) {
            i = website.compareTo(o.website);
        }
        if (i != 0) return i;

        i = Integer.compare(year, o.year);
        if (i != 0) return -1 * i; // descending order

        return -1 * Integer.compare(month, o.month); // descending order
    }

    public void process(AccessLogEntry logEntry) {

        // TODO: check if we process log entries for same website

        int month = logEntry.getMonth();
        int year = logEntry.getYear();
        if ((this.year != year) || (this.month != month)) {
            throw new IllegalArgumentException(String.format("AccessLogEntry for %Fd is not applicable for VisitorsByMonth %d-%d",
                    logEntry.getTimestamp(), this.year, this.month));
        }

        boolean isNewByDay = processDayStats(logEntry, this.previouslyVisitedFrom);
        boolean isNewByCountry = processCountryStats(logEntry, this.previouslyVisitedFrom);
        boolean isNewByUrl = processVisitedUrlStats(logEntry, this.previouslyVisitedFrom);
        boolean isNewByReferer = processRefererStats(logEntry, this.previouslyVisitedFrom);
        if (isNewByDay || isNewByCountry || isNewByUrl || isNewByReferer) {
            this.previouslyVisitedFrom.add(logEntry.getRequesterIpHash());
        }
    }

    private boolean processDayStats(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        int day = logEntry.getDayOfMonth();
        return visitorsByMonthday.get(day - 1).process(logEntry, previouslyVisitedFrom);
    }

    private boolean processCountryStats(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        String country = logEntry.getCountry();
        if (country == null) {
            country = Ip2CountryService.UNKNOWN_COUNTRYCODE;
        }
        VisitorsAndPageViews<String> visitors = visitorsByCountry.get(country);
        if (visitors == null) {
            visitors = new VisitorsAndPageViews<String>(country);
            visitorsByCountry.put(country, visitors);
        }
        return visitors.process(logEntry, previouslyVisitedFrom);
    }

    private boolean processVisitedUrlStats(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        String contentId = logEntry.getContentId();
        VisitorsAndPageViews<String> visitors = visitorsByUrl.get(contentId);
        if (visitors == null) {
            visitors = new VisitorsAndPageViews<String>(contentId);
            visitorsByUrl.put(contentId, visitors);
        }
        return visitors.process(logEntry, previouslyVisitedFrom);
    }

    private boolean processRefererStats(AccessLogEntry logEntry, Set<String> previouslyVisitedFrom) {
        String referer = logEntry.getReferer();
        if (referer != null && !referer.isEmpty()) {
            VisitorsAndPageViews<String> visitors = visitorsByReferer.get(referer);
            if (visitors == null) {
                visitors = new VisitorsAndPageViews<String>(referer);
                visitorsByReferer.put(referer, visitors);
            }
            return visitors.process(logEntry, previouslyVisitedFrom);
        }
        return false;
    }

    public int getNewVisitors() {
        int total = 0;
        for (VisitorsAndPageViews<Integer> daily : visitorsByMonthday) {
            total += daily.getNewVisitors();
        }
        return total;
    }

    public int getRecurrentVisitors() {
        int total = 0;
        for (VisitorsAndPageViews<Integer> daily : visitorsByMonthday) {
            total += daily.getRecurrentVisitors();
        }
        return total;
    }

    public int getVisits() {
        int total = 0;
        for (VisitorsAndPageViews<Integer> daily : visitorsByMonthday) {
            total += daily.getVisits();
        }
        return total;
    }

    public int getPageViews() {
        int total = 0;
        for (VisitorsAndPageViews<Integer> daily : visitorsByMonthday) {
            total += daily.getPageViews();
        }
        return total;
    }

    public int getUniquePageViews() {
        int total = 0;
        for (VisitorsAndPageViews<Integer> daily : visitorsByMonthday) {
            total += daily.getUniquePageViews();
        }
        return total;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public Object getWebsite() {
        return this.website;
    }

    public Collection<VisitorsAndPageViews<String>> getByCountry() {
        // filter out empty entries and sort on page views
        return visitorsByCountry.values().stream().filter(val -> val.hasData()).sorted(SortOnPageViews.INSTANCE).toList();
    }

    public List<VisitorsAndPageViews<Integer>> getByDay() {
        return visitorsByMonthday;
    }

    public Collection<VisitorsAndPageViews<String>> getByContent() {
        return visitorsByUrl.values().stream().filter(val -> val.hasData()).sorted(SortOnPageViews.INSTANCE).toList();
    }
    
    public Collection<VisitorsAndPageViews<String>> getByReferer() {
        return visitorsByReferer.values().stream().filter(val -> val.hasData()).sorted(SortOnPageViews.INSTANCE).toList();
    }

}
