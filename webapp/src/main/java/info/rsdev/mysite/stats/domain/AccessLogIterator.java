package info.rsdev.mysite.stats.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;

/**
 * This class is not thread safe!
 */
public class AccessLogIterator implements Iterator<AccessLogEntry> {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessLogIterator.class);
    
    private static final Map<String, Constructor<AccessLogEntry>> cachedConstructors = new ConcurrentHashMap<>();
    
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
    
    private CSVReader csvReader = null;
    
    private String[] nextLine = null;
    
    public AccessLogIterator(File logfile) throws FileNotFoundException {
        logger.info("Processing logfile " + logfile.getAbsolutePath());
        csvReader = new CSVReader(new FileReader(logfile));
        readNextlineIntoBuffer();
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    private void readNextlineIntoBuffer() {
        try {
            nextLine = csvReader.readNext();
            if (nextLine == null) {
                close();
            }
        } catch (IOException | CsvValidationException e) {
            close();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Close the underlying {@link CSVReader}, because we are done with it
     */
    public void close() {
        try {
            csvReader.close();
        } catch (IOException e1) {
            logger.error("Exception occured when closing Access logfile", e1);
        }
    }
    
    @Override
    public AccessLogEntry next() {
        String[] tmp = nextLine;
        readNextlineIntoBuffer();
        return makeAccessLogEntry(tmp);
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
    
    private AccessLogEntry makeAccessLogEntry(String[] fields) {
        String typeName = AccessLogEntry.class.getName().concat(fields[0].toUpperCase());
        Constructor<AccessLogEntry> logEntryConstructor = cachedConstructors.get(typeName);
        
        if (logEntryConstructor == null) {
            try {
                @SuppressWarnings("unchecked")
                Class<AccessLogEntry> clazz = (Class<AccessLogEntry>)getClass().getClassLoader().loadClass(typeName);
                logEntryConstructor = clazz.getDeclaredConstructor(dateFormatter.getClass(), String[].class);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException("Could not find a valid Constructor to create AccessLogEntry for version "
                        .concat(fields[0]), e);
            }
        }
        try {
            return logEntryConstructor.newInstance(new Object[] { dateFormatter, fields });
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException("Could not create AccessLogEntry for version ".concat(fields[0]), e);
        }
    }
    
}
