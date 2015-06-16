package info.rsdev.mysite.stats.domain;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class AccessLogReportTest {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessLogReportTest.class);
    
    @Test
    public void testResolveCountry() {
        Locale country = AccessLogReport.resolveCountry("87.212.128.27", null);
        assertEquals(new Locale("", "NL"), country);
        assertEquals("Nederland", country.getDisplayCountry(new Locale("nl", "NL")));
    }
    
    @Test  @Ignore
    public void processLogfile() throws IOException {
        //the file location is temporary, test too, maybe
        File logFile = new File("/home/dschoorl/myown/websites/mysite/sites/sylviaborst.nl/logs/mysite-accesslog.log");
        ConcurrentHashMap<String, String> crawlerUserAgents = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Locale> ip2Country = new ConcurrentHashMap<>();
        
        Long startTime = System.currentTimeMillis();
        
        //code copied and adjusted from StatsContentServant
        AccessLogReport report = new AccessLogReport("sylviaborst.nl", ip2Country, crawlerUserAgents);
        AccessLogIterator logItems = new AccessLogIterator(logFile);
        int entryCount = 0;
        while (logItems.hasNext()) {
            report.process(logItems.next());
            entryCount++;
        }
        
        logger.info(String.format("Finished processing %d logitems in %d seconds", entryCount, System.currentTimeMillis() - startTime));
        
        print(report.getLatestMonth());
        for (VisitorsByMonth previous: report.getOlderMonths()) {
            print(previous);
        }
        logger.info(String.format("Resolved %d IP addresses to country codes", ip2Country.size()));
        
        logger.info("Split by Country for Latest month:");
        print(report.getVisitorsByCountryLatestMonth());
        logger.info("Split by Country for Latest month, grand total:");
        print(report.getVisitorsByCountry());
        
        logger.info(String.format("The following %d browser / OS combinations have been encountered:", report.getBrowserAgentStrings().size()));
        for (String browserUA: report.getBrowserAgentStrings()) {
            logger.info(String.format("Browser UserAgent: %s", browserUA));
        }
    }
    
    private void print(List<VisitorsAndPageViews<Locale>> visitorsByCountry) {
        Locale dutch = new Locale("nl");
        for (VisitorsAndPageViews<Locale> stats: visitorsByCountry) {
            String country = stats.getGroupedBy().getDisplayCountry(dutch);
            logger.info(String.format("Country = %s, visits = %d, new = %d, recurrent = %d, pageViews = %d, uniquePageViews = %d",
                    country,
                    stats.getVisits(),
                    stats.getNewVisitors(),
                    stats.getRecurrentVisitors(),
                    stats.getPageViews(),
                    stats.getUniquePageViews()));
        }
    }
    
    private void print(VisitorsByMonth monthly) {
        logger.info(String.format("website = %s, year = %d, month = %d, visits = %d, new = %d, recurrent = %d, pageViews = %d, uniquePageViews = %d",
                monthly.getWebsite(), 
                monthly.getYear(), 
                monthly.getMonth(), 
                monthly.getVisits(),
                monthly.getNewVisitors(), 
                monthly.getRecurrentVisitors(), 
                monthly.getPageViews(), 
                monthly.getUniquePageViews()));
    }
}
