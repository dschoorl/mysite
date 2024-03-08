package info.rsdev.mysite.stats;

import static org.junit.Assert.assertEquals;

import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

public class Ip2CountryServiceTest {
    
    private Ip2CountryService serviceUnderTest = null;
    
    @Before
    public void setup() {
        TreeMap<Long, String> data = new TreeMap<>();
        data.put(0L, "FR");
        data.put(1234567890L, "BE");
        data.put(1408714700L, "NL");
        data.put(1408714800L, "DE");
        data.put(3758096384L, "XX");
        serviceUnderTest = new Ip2CountryService(data);
    }

    @Test
    public void test() {
        assertEquals("NL", serviceUnderTest.getCountryCode("83.247.71.238"));
    }

}
