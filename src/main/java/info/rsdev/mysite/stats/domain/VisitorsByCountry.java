package info.rsdev.mysite.stats.domain;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class VisitorsByCountry {
    
    private static final Locale UNKNOWN = new Locale("XX");
    
    private Map<Locale, VisitorsAndPageViews> visitorsByCountry = new HashMap<>();
    
    private Set<String> previouslyVisitedFrom = new HashSet<>();
    
    public void process(AccessLogEntry logEntry) {
        Locale country = logEntry.getCountry();
        if (country == null) {
            country = UNKNOWN;
        }
        VisitorsAndPageViews visitors = visitorsByCountry.get(country);
        if (visitors == null) {
            visitors = new VisitorsAndPageViews();
            visitorsByCountry.put(country, visitors);
        }
        visitors.process(logEntry, previouslyVisitedFrom);
    }
    
    public Map<Locale, VisitorsAndPageViews> getVisitorsByCountry() {
        return visitorsByCountry;
    }
    
}
