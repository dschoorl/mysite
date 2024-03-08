package info.rsdev.mysite.stats;

/**
 * Helper class to convert ip address to a 32 bit number
 */
public abstract class IpConverter {

    private static final int[] segmentMultipliers = new int[] { 16777216, 65536, 256, 1 };

    /**
     * Convert an ip number to an integer value where the ip number is interpreted
     * as a 32 bit value by treating each segment as an 8 bit part of the whole.
     * This representation makes looking up country codes per ip address more
     * efficient.
     */
    public static long calculateIpNumber(String ipNumber) {
        long result = 0;
        int[] segments = getSegments(ipNumber);
        if (segments.length == segmentMultipliers.length) {
            for (int index = 0; index < segments.length; index++) {
                result += segments[index] * segmentMultipliers[index];
            }
        }
        return result;
    }

    protected static int[] getSegments(String ipNumber) {
        String current = "";
        int[] segments = new int[4];
        int segmentIndex = 0;
        for (int index = 0; index < ipNumber.length(); index++) {
            if (ipNumber.charAt(index) == '.') {
                segments[segmentIndex] = Integer.parseInt(current);
                segmentIndex++;
                current = "";
            } else {
                current += ipNumber.charAt(index);
            }
        }
        segments[segmentIndex] = Integer.parseInt(current);
        return segments;
    }

}
