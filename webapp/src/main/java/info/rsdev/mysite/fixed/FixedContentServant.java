package info.rsdev.mysite.fixed;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.domain.DefaultImage;
import info.rsdev.mysite.util.ServletUtils;
import info.rsdev.mysite.util.ThumbnailCreator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FixedContentServant implements RequestHandler, ConfigKeys {
    
    private static final Logger logger = LoggerFactory.getLogger(FixedContentServant.class);
    
    private static final List<String> knownTextMimeTypes = Arrays.asList("text/html", "text/css", "text/plain", "text/javascript",
            "application/javascript");
    
    private static final Tika tikaMimetypeDetector = new Tika();
    
    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        if (pathInfo == null) { pathInfo = "/"; }
        if (pathInfo.length() >= 1) {
            resourceLocation = resourceLocation.resolve(pathInfo.substring(1));
        }
        if (resourceLocation.toFile().isDirectory()) {
            resourceLocation = resourceLocation.resolve(fixedConfig.getIndexFilename());
        }
        if (DefaultImage.isThumbnail(resourceLocation.getFileName().toString())) {
            //when applicable, create a thumbnail on the fly
            ThumbnailCreator.make(resourceLocation);
        }
        if (!resourceLocation.toFile().isFile()) {
            String resource = ServletUtils.concatenatePaths(request.getContextPath(), pathInfo);
            response.sendError(404, String.format("Resource %s does not exist", resource));
            return ModuleHandlerResult.NO_CONTENT;
        }
        String mimeType = getMimeType(resourceLocation);
        if ((mimeType == null) || !mimeType.equals("text/html")) {
            response.addHeader("Cache-Control", "max-age="+60*60*24*7); //cache everything, except html files, for one week
        }
        response.setContentType(mimeType);
        if (isBinary(mimeType)) {
            ServletUtils.writeBinary(response, resourceLocation);
        } else {
            response.setCharacterEncoding("UTF-8");
            ServletUtils.writeText(response, resourceLocation.toFile());
        }
        if (mimeType.equals("text/html") || mimeType.equals("application/pdf")) {
            //only log html AND pdf content in the access log
            return new ModuleHandlerResult(null, fixedConfig.getSiteRoot().relativize(resourceLocation).toString());
        }
        return ModuleHandlerResult.NO_CONTENT;    //not worth logging in access log
    }

    private boolean isBinary(String mimeType) {
        return !knownTextMimeTypes.contains(mimeType);
    }

    protected String getMimeType(Path resource) {
        String mimeType = null;
        try {
            mimeType = tikaMimetypeDetector.detect(resource);    //Files.probeContentType(resource);
            if (mimeType == null) {
                try (FileInputStream stream = new FileInputStream(resource.toFile())) {
                    mimeType = URLConnection.guessContentTypeFromStream(stream);
                }
                if ((mimeType != null) && logger.isDebugEnabled()) {
                    logger.debug(String.format("MimeType resolved to %s by URLConnection.guessContentTypeFromStream(%s)", mimeType, resource));
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug(String.format("MimeType resolved to %s by Apache Tika", mimeType, resource));
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

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        //this module does not provide menu items
        return DefaultMenuGroup.EMPTY_GROUP;
    }
    
}
