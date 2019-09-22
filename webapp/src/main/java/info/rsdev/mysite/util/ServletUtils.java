package info.rsdev.mysite.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServletUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ServletUtils.class);
    
    private static final String PATH_SEPARATOR = "/";
    
    /**
     * do not instantiate this bag of static utility methods
     */
    private ServletUtils() {}

    public static void writeText(HttpServletResponse response, File resourceLocation) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Write TEXT response for %s", resourceLocation));
        }
        PrintWriter out = response.getWriter();
        try (BufferedReader reader = new BufferedReader(new FileReader(resourceLocation), 2048)) {
            char[] buffer = new char[2048];
            int charsRead = 0;
            while ((charsRead = reader.read(buffer)) != -1) {
                out.write(buffer, 0, charsRead);
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s file", resourceLocation), e);
            String msg = String.format("...OMG. Something went horribly wrong while serving resource %s for you. Sorry. " +
                    "Let us know if the error keeps occuring.", resourceLocation);
            if (!response.isCommitted()) {
                response.sendError(500, msg);
            } else {
                out.write(msg);
            }
        }
        out.flush();
    }
    
    public static String concatenatePaths(String path1, String path2) {
        if (path1 == null) { path1 = ""; }
        if (path2 == null) { path2 = ""; }
        if (path1.endsWith("/")) { path1 = path1.substring(0, path1.length() - 1); }
        if (path2.startsWith("/")) { path2 = path2.substring(1); }
        return path1.concat("/").concat(path2);
    }
    
    public static String getFirstPathElement(String urlEncodedPath) {
        if ((urlEncodedPath == null) || urlEncodedPath.isEmpty()) {
            return null;
        }
        int indexOfSlash = urlEncodedPath.indexOf(PATH_SEPARATOR);
        try {
            if (indexOfSlash < 0) {
                return URLDecoder.decode(urlEncodedPath, "UTF-8");
            } else if (indexOfSlash == 0) {
                return getFirstPathElement(urlEncodedPath.substring(1));
            }
            return URLDecoder.decode(urlEncodedPath.substring(0, indexOfSlash), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
