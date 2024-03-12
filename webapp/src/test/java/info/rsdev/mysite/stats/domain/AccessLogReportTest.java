package info.rsdev.mysite.stats.domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogReportTest {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogReportTest.class);

    @Test
    public void detectCrawlers() {
        //crawlers are in the logfile, but we do not want to see them in the report
        AccessLogReport sut = new AccessLogReport("not relevant site name");
        assertEquals(true, sut.isCrawler("Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)"));
        assertEquals(0, sut.getBrowserAgentStrings().size());
    }

    @Test
    public void processLogfile() throws IOException {
        // the file location is temporary, 'test' too, maybe
        File logFile = new File("/home/dschoorl/data/websites/mysite/sites/sylviaborst.nl/logs/mysite-accesslog.log");

        Long startTime = System.currentTimeMillis();

        // code copied and adjusted from StatsContentServant
        AccessLogReport report = new AccessLogReport("sylviaborst.nl");
        AccessLogIterator logItems = new AccessLogIterator(logFile);
        int entryCount = 0;
        while (logItems.hasNext()) {
            report.process(logItems.next());
            entryCount++;
        }

        logger.info(
                String.format("Finished processing %d logitems in %d seconds", entryCount, System.currentTimeMillis() - startTime));

        print(report.getLatestMonth());
        for (VisitorsByMonth previous : report.getOlderMonths()) {
            print(previous);
        }
        logger.info("Split by Country for Latest month:");
        print(report.getVisitorsByCountryLatestMonth());
        logger.info("Split by Country for Latest month, grand total:");
        print(report.getVisitorsByCountry());

        logger.info(String.format("The following %d browser / OS combinations have been encountered:",
                report.getBrowserAgentStrings().size()));
        for (String browserUA : report.getBrowserAgentStrings()) {
            logger.info(String.format("Browser UserAgent: %s", browserUA));
        }
    }

    private void print(List<VisitorsAndPageViews<String>> visitorsByCountry) {
        for (VisitorsAndPageViews<String> stats : visitorsByCountry) {
            String country = stats.getGroupedBy();
            logger.info(String.format("Country = %s, visits = %d, new = %d, recurrent = %d, pageViews = %d, uniquePageViews = %d",
                    country, stats.getVisits(), stats.getNewVisitors(), stats.getRecurrentVisitors(), stats.getPageViews(),
                    stats.getUniquePageViews()));
        }
    }

    private void print(VisitorsByMonth monthly) {
        logger.info(String.format(
                "website = %s, year = %d, month = %d, visits = %d, new = %d, recurrent = %d, pageViews = %d, uniquePageViews = %d",
                monthly.getWebsite(), monthly.getYear(), monthly.getMonth(), monthly.getVisits(), monthly.getNewVisitors(),
                monthly.getRecurrentVisitors(), monthly.getPageViews(), monthly.getUniquePageViews()));
    }
}
