package info.rsdev.mysite.stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ip2CountryService {

    private static final Logger logger = LoggerFactory.getLogger(Ip2CountryService.class);

    public static final String UNKNOWN_COUNTRYCODE = "XX";

    private final TreeMap<Long, String> ip2LocationDb;

    public Ip2CountryService(TreeMap<Long, String> ip2LocationDb) {
        // contains approx. 250,000 entries
        this.ip2LocationDb = ip2LocationDb;
    }

    public Ip2CountryService(Path csvFileLocation) {
        this.ip2LocationDb = new TreeMap<Long, String>();

        try {
            // fill with file data
            Files.lines(csvFileLocation).forEach(this::processIp2CountryDbLine);
            logger.info("Loaded {} entries from Ip2Location csv file {}", ip2LocationDb.size(), csvFileLocation);
        } catch (IOException e) {
            logger.error("Could not load Ip2Country database", e);
        }
    }

    private void processIp2CountryDbLine(String line) {
        String[] fields = line.split(",");
        Long ipLowerBounds = Long.parseLong(unquote(fields[0]));
        String countryCode = unquote(fields[2]);
        if (countryCode.equals("-")) {
            countryCode = UNKNOWN_COUNTRYCODE;
        }
        this.ip2LocationDb.put(ipLowerBounds, countryCode);
    }

    /**
     * Remove double quotes from start and end of this string if so exists
     * 
     * @param fieldValue
     * @return the fieldValue without quotations
     */
    private String unquote(String fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue.length() >= 2) {
            if (fieldValue.charAt(0) == '"' && fieldValue.charAt(fieldValue.length() - 1) == '"') {
                return fieldValue.substring(1, fieldValue.length() - 1);
            }
        }
        return fieldValue;
    }

    public String getCountryCode(String ipAddress) {
        long ipNumber = IpConverter.calculateIpNumber(ipAddress);
        Long targetKey = ip2LocationDb.floorKey(ipNumber);
        if (targetKey == null) {
            targetKey = ip2LocationDb.ceilingKey(ipNumber);
            if (targetKey == null) {
                logger.info("No Floor nor Ceiling key for ip {}/{} is null; falling back to country code {}", ipAddress, ipNumber,
                        UNKNOWN_COUNTRYCODE);
                return UNKNOWN_COUNTRYCODE;
            }
        }
        return ip2LocationDb.getOrDefault(targetKey, UNKNOWN_COUNTRYCODE);
    }
}
