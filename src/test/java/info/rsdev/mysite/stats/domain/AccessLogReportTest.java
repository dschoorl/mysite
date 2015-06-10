package info.rsdev.mysite.stats.domain;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccessLogReportTest {
    
    @Test
    public void testResolveCountry() {
        Locale country = AccessLogReport.resolveCountry("87.212.128.27", null);
        assertEquals(new Locale("NL"), country);
    }
    
}
