package info.rsdev.mysite.singlepage.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

public class SinglePage {
    
    private final File location;
    
    private final String mountPoint;
    
    public SinglePage(File location, String mountPoint) {
        if (location == null) {
            throw new NullPointerException("Location cannot be null");
        }
        if (!location.isFile()) {
            throw new IllegalArgumentException(String.format("Not a file: %s", location.getAbsolutePath()));
        }
        this.location = location;
        this.mountPoint = mountPoint;
    }
    
    public String getContent() {
        StringWriter out = new StringWriter();
        try (BufferedReader reader = new BufferedReader(new FileReader(location), 2048)) {
            char[] buffer = new char[2048];
            int charsRead = 0;
            while ((charsRead = reader.read(buffer)) != -1) {
                out.write(buffer, 0, charsRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }
    
    /**
     * Get the name of the file without the file extension
     * @return the name of the file without extension
     */
    public String getName() {
        String name = location.getName();
        int lastDotIndex = name.lastIndexOf('.');
        return lastDotIndex >= 0? name.substring(0, lastDotIndex): name;
    }
    
    public String getMountPoint() {
        return this.mountPoint;
    }
    
}
