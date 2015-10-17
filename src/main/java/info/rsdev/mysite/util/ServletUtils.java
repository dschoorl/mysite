package info.rsdev.mysite.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServletUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ServletUtils.class);
    
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
    
}
