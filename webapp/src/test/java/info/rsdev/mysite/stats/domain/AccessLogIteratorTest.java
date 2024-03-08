package info.rsdev.mysite.stats.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;

public class AccessLogIteratorTest {
    
    private AccessLogIterator logRecords = null;
    
    private Path logFile = null;
    
    @After
    public void cleanup() throws IOException {
        if (logRecords != null) {
            logRecords.close();
        }
        if (logFile != null) {
            Files.delete(logFile);
        }
    }
    
    @Test
    public void testReadValidLine() throws IOException {
        String content = "\"v1\",2015-06-07,21:36:04,15668,\"127.0.0.1\",,\"GET\",\"local.sylviaborst.nl\",\"sylviaborst.nl\",\"/mysite/\",200,\"AFA600983D28E77D5C56D96DB9CCCFF4\",,\"slideshow\",\"108825.jpg\",\"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0\",,"; 
        this.logFile = makeFile(content);
        this.logRecords = new AccessLogIterator(this.logFile.toFile());
        
        assertTrue(logRecords.hasNext());
        AccessLogEntry entry = this.logRecords.next();
        assertNotNull(entry);
        assertEquals("127.0.0.1", entry.getRequesterIpHash());
        
        //roundtrip test
        assertEquals(content, entry.toString());
    }
    
    private Path makeFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("AccessLogIteratorTest", "log");
        Files.write(tempFile, content.getBytes());
        return tempFile;
    }
    
}
