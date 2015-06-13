package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class VisitorsByMonth implements Comparable<VisitorsByMonth> {
    
    private final String website;
    
    private final int month;
    
    private final int year;
    
    /**
     * The list contains an element for each day of the month. And each day contains a two column array containing
     * first the number of unique visitors for that day and secondly the pageviews for that day.
     */
    private VisitorsAndPageViews[] visitorsAndPageViewsByMonthday = null;
    
    private VisitorsByCountry visitorsByCountry = new VisitorsByCountry();
    
    private Set<String> previouslyVisitedFrom = new HashSet<>();
    
    public VisitorsByMonth(String website, int month, int year) {
        if (website.isEmpty()) {
            website = null;
        }
        this.website = website;
        this.month = month;
        this.year = year;
        
        int daysInMonth = new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        visitorsAndPageViewsByMonthday = new VisitorsAndPageViews[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            visitorsAndPageViewsByMonthday[i] = new VisitorsAndPageViews();
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
        /* The natural order of instances of this class is (first) alphabetical by website, (second) by year, descending and
         * (third) by month, descending.
         */
        if (o == null) { return 1; }
        
        int i = 0;
        
        //take into account that website could be null or empty
        if ((website == null) && (o.website != null)) { return -1; } 
        if ((o.website == null) && (website != null)) { return 1; }
        if ((website != null && o.website != null)) {
            i = website.compareTo(o.website);
        }
        if (i != 0) return i;

        i = Integer.compare(year, o.year);
        if (i != 0) return -1 * i;  //descending order

        return -1 * Integer.compare(month, o.month);    //descending order
    }

    public void process(AccessLogEntry logEntry) {
        
        //TODO: check if we process log entries for same website
        
        int month = logEntry.getMonth();
        int year = logEntry.getYear();
        if ((this.year != year) || (this.month != month)) {
            throw new IllegalArgumentException(String.format("AccessLogEntry for %Fd is not applicable for VisitorsByMonth %d-%d",
                    logEntry.getTimestamp(), this.year, this.month));
        }
        
        int day = logEntry.getDayOfMonth();
        visitorsAndPageViewsByMonthday[day - 1].process(logEntry, previouslyVisitedFrom);
        visitorsByCountry.process(logEntry);
    }
    
    public int getNewVisitors() {
        int total = 0;
        for (VisitorsAndPageViews daily: visitorsAndPageViewsByMonthday) {
            total += daily.getNewVisitors();
        }
        return total;
    }
    
    public int getRecurrentVisitors() {
        int total = 0;
        for (VisitorsAndPageViews daily: visitorsAndPageViewsByMonthday) {
            total += daily.getRecurrentVisitors();
        }
        return total;
    }
    
    public int getVisits() {
        int total = 0;
        for (VisitorsAndPageViews daily: visitorsAndPageViewsByMonthday) {
            total += daily.getVisits();
        }
        return total;
    }
    
    public int getPageViews() {
        int total = 0;
        for (VisitorsAndPageViews daily: visitorsAndPageViewsByMonthday) {
            total += daily.getPageViews();
        }
        return total;
    }

    public int getUniquePageViews() {
        int total = 0;
        for (VisitorsAndPageViews daily: visitorsAndPageViewsByMonthday) {
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

    public Map<Locale, VisitorsAndPageViews> getByCountry() {
        return visitorsByCountry.getVisitorsByCountry();
    }
    
    public List<VisitorsAndPageViews> getByDay() {
        return Arrays.asList(visitorsAndPageViewsByMonthday);
    }
}
