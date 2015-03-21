package info.rsdev.mysite.fixed;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.exception.ConfigurationException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedContentServant implements RequestHandler, ConfigKeys {
    
    private static final Logger logger = LoggerFactory.getLogger(FixedContentServant.class);
    
    private static final List<String> knownTextMimeTypes = Arrays.asList("text/html", "text/css", "text/plain", "text/javascript",
            "application/javascript");
    
    @Override
    public void handle(ModuleConfig config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof FixedContentModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s", 
                FixedContentModuleConfig.class.getSimpleName(), config));
        }
        FixedContentModuleConfig fixedConfig = (FixedContentModuleConfig)config;
        Path resourceLocation = fixedConfig.getSiteRoot();
        String pathInfo = request.getPathInfo();
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String servletPath = request.getServletPath();
        if (servletPath.length() > 1) {
            resourceLocation = resourceLocation.resolve(servletPath.substring(1));
        }
        if (resourceLocation.toFile().isDirectory()) {
            resourceLocation = resourceLocation.resolve(fixedConfig.getIndexFilename());
        }
        if (!resourceLocation.toFile().isFile()) {
            response.sendError(404, String.format("Resource %s does not exist", resourceLocation));
            return;
        }
        String mimeType = getMimeType(resourceLocation);
        if (isBinary(mimeType)) {
            writeBinary(response, resourceLocation);
        } else {
            writeText(response, resourceLocation);
        }
        response.setContentType(mimeType);
    }
    
    private void writeBinary(HttpServletResponse response, Path resourceLocation) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Write BINARY response for %s", resourceLocation));
        }
        ServletOutputStream out = response.getOutputStream();
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(resourceLocation.toFile()), 2048)) {
            byte[] buffer = new byte[2048];
            while (reader.read(buffer) != -1) {
                out.write(buffer);
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s file", resourceLocation), e);
        }
        out.flush();
    }
    
    private void writeText(HttpServletResponse response, Path resourceLocation) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Write TEXT response for %s", resourceLocation));
        }
        PrintWriter out = response.getWriter();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(resourceLocation.toFile()), "UTF-8"), 2048)) {
            char[] buffer = new char[2048];
            while (reader.read(buffer) != -1) {
                out.write(buffer);
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
    
    private boolean isBinary(String mimeType) {
        return !knownTextMimeTypes.contains(mimeType);
    }

    protected String getMimeType(Path resource) {
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(resource);
            if (mimeType == null) {
                try (FileInputStream stream = new FileInputStream(resource.toFile())) {
                    mimeType = URLConnection.guessContentTypeFromStream(stream);
                }
                if ((mimeType != null) && logger.isDebugEnabled()) {
                    logger.debug(String.format("MimeType resolved to %s by URLConnection.guessContentTypeFromStream(%s)", mimeType, resource));
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug(String.format("MimeType resolved to %s by Files.probeContentType(%s)", mimeType, resource));
            }
        } catch (IOException e) {
            logger.error(String.format("Cannot determine mime type from content for %s", resource), e);
        }
        if (mimeType == null) {
            mimeType = URLConnection.guessContentTypeFromName(resource.toFile().getName());
            logger.debug(String.format("MimeType resolved to %s by URLConnection.guessContentTypeFromName(%s)", mimeType, resource));
        }
        return mimeType;
    }
    
}
