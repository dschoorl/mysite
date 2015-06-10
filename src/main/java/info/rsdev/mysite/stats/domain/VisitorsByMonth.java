package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class VisitorsByMonth implements Comparable<VisitorsByMonth> {
    
    private final String website;
    
    private final int month;
    
    private final int year;
    
    /**
     * The list contains an element for each day of the month. And each day contains a two column array containing
     * first the number of unique visitors for that day and secondly the pageviews for that day.
     */
    private VisitorsAndPageViews[] visitorsAndPageViewsByMonthday = null;
    
    private Map<String, AtomicInteger> visitorByCountry = new HashMap<>();
    
    public VisitorsByMonth(String website, int month, int year) {
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
        return 0;
    }

    public void process(AccessLogEntry logEntry) {
        
    }
    
    
}
