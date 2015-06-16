package info.rsdev.mysite.stats.domain;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import info.rsdev.mysite.common.domain.AccessLogEntry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VisitorsAndPageViewsTest {
    
    private VisitorsAndPageViews<String> stats = null;
    Set<String> previouslyVisitedFrom = null;
    
    @Before
    public void init() {
        this.stats = new VisitorsAndPageViews<String>("group");
        assertEquals(0, stats.getNewVisitors());
        assertEquals(0, stats.getPageViews());
        
        previouslyVisitedFrom = new HashSet<>();
    }
    
    @Test
    public void processPagesFromTwoUniqueVisitors() {
        AccessLogEntry firstEntry = mock(AccessLogEntry.class);
        when(firstEntry.getSessionId()).thenReturn("sessie 1");
        when (firstEntry.getTemplateName()).thenReturn("mytemplate");
        AccessLogEntry secondEntry = mock(AccessLogEntry.class);
        when(secondEntry.getSessionId()).thenReturn("sessie 2");
        when (secondEntry.getTemplateName()).thenReturn("mytemplate");
        
        stats.process(firstEntry, previouslyVisitedFrom);
        stats.process(secondEntry, previouslyVisitedFrom);
        
        assertEquals(2, stats.getPageViews());
        assertEquals(2, stats.getNewVisitors());
    }
    
    @Test
    public void processTwoDifferentPagesFromSameVisitor() {
        AccessLogEntry firstEntry = mock(AccessLogEntry.class);
        when(firstEntry.getSessionId()).thenReturn("sessie 1");
        when (firstEntry.getTemplateName()).thenReturn("mytemplate");
        AccessLogEntry secondEntry = mock(AccessLogEntry.class);
        when(secondEntry.getSessionId()).thenReturn("sessie 1");
        when (secondEntry.getTemplateName()).thenReturn("mytemplate");
        
        stats.process(firstEntry, previouslyVisitedFrom);
        stats.process(secondEntry, previouslyVisitedFrom);
        
        assertEquals(2, stats.getPageViews());
        assertEquals(1, stats.getNewVisitors());
    }
    
}
