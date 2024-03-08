package info.rsdev.mysite.stats;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IpConverterTest {
    
    @Test
    public void dissectIpNumberInArrayOfInts() {
        assertArrayEquals(new int[] {83, 247, 71, 238}, IpConverter.getSegments("83.247.71.238"));
    }

    @Test
    public void calculateFlatIpNumber() {
        assertEquals(83*16777216+247*65536+71*256+238, IpConverter.calculateIpNumber("83.247.71.238"));
    }

}
